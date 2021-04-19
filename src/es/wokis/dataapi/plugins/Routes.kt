package es.wokis.dataapi.plugins

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import es.wokis.dataapi.dao.CredentialsDAO
import es.wokis.dataapi.dao.DataDAO
import es.wokis.dataapi.dto.DataDTO
import es.wokis.dataapi.dto.TokenHashDTO
import es.wokis.dataapi.generator.HashGenerator
import es.wokis.dataapi.utils.receiveTextWithCorrectEncoding
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import es.wokis.dataapi.utils.user
import io.ktor.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import java.sql.SQLException

private val credentialsDao = CredentialsDAO()
private val dataDAO = DataDAO()
private val gson = Gson()

fun Application.initRoutes() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "HELLO WORLD!")
        }

        get("/token/") {
            val generatedHash = HashGenerator.generateHash(12L)
            val token = generateToken(generatedHash)
            val tokenHashDTO = TokenHashDTO(generatedHash, token)

            if (credentialsDao.insertHash(generatedHash)) {
                call.respondText(gson.toJson(tokenHashDTO))
            }
        }

        get("/token/{hash}/") {
            val hash = call.parameters["hash"]
            if (hash != null && credentialsDao.getHash(hash) != null) {
                call.respond(generateToken(hash))
            } else if (hash == null) {
                call.respond(HttpStatusCode.BadRequest, "Hash is required")
            } else {
                call.respond(HttpStatusCode.NotFound, hash)
            }
        }

        get("/data/{hash}/") {
            val hash = call.parameters["hash"]
            if (hash != null) {
                val dataList = dataDAO.getDataList(hash)
                if (dataList != null) {
                    call.respond(HttpStatusCode.OK, dataList)
                } else {
                    call.respond(HttpStatusCode.NotFound, "$hash doesn't exists.")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest,
                        "Client-id is required")
            }
        }

        post("/data/{hash}/") {
            val hash = call.parameters["hash"]

            try {
                // primero la string, luego lista objetos, así GSON está contento.. facepalm
                // NOTA: Ktor ha decidido que UTF-8 no sea el encoding principal.. yay
                // tiramos de asincronía para poder mantener el encoding.. because why not ktor
                withContext(Dispatchers.IO) {
                    val json = call.receiveTextWithCorrectEncoding()
                    val dataListJson = Gson().fromJson(json, Array<DataDTO>::class.java).toList()
                    if (hash != null) {

                        when (dataDAO.insertData(hash, dataListJson)) {
                            null -> {
                                call.respond(HttpStatusCode.Conflict,
                                        "Data already on database")
                            }

                            true -> {
                                call.respond(HttpStatusCode.OK, gson.toJson(dataListJson))
                            }

                            else -> {
                                call.respond(HttpStatusCode.BadRequest)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                // un pelín feo, pero así mostramos siempre el error que es..
                call.respond(HttpStatusCode.BadRequest,
                        e.message?.split(":")?.get(1)
                                ?: "There's something weird with that request, try it again..")
            }
        }

        put("/data/{hash}/{id}/") {
            try {
                val dataId = call.parameters["id"]?.toInt()
                val hash = call.parameters["hash"]

                if (hash != null && dataId != null) {
                    if (dataDAO.updateData(dataId, hash)) {
                        call.respond(HttpStatusCode.OK,
                                gson.toJson(dataDAO.getData(hash, dataId)))
                    } else {
                        call.respond(HttpStatusCode.NotFound,
                                "Are you sure it exists? client-id: $hash, dataId: $dataId")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest,
                            "Client-id and Data-id are required")
                }
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest,
                        "The second argument MUST be a number, received: " +
                                "${e.message?.split(":")?.get(1)}")
            }

        }

        // TODO: 19/04/21 REMOVE THIS SECTION ON UPDATE 1.2
        authenticate {
            post("/hash") {
                call.respond(HttpStatusCode.OK, "${call.user?.hash}")
            }

            get("/data") {
                val hash = call.user?.hash
                if (hash != null) {
                    val data = dataDAO.getDataList(hash)

                    if (data != null){
                        call.respond(gson.toJson(data))
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }

                }

            }

            post("/data") {
                // por si nos pasan lo que no deben, les devolvemos el 400 y que ellos se encarguen
                try {
                    // primero la string, luego lista objetos, así GSON está contento.. facepalm
                    // NOTA: Ktor ha decidido que UTF-8 no sea el encoding principal.. yay
                    // tiramos de asincronía para poder mantener el encoding.. because why not ktor
                    withContext(Dispatchers.IO) {
                        val json = call.receiveTextWithCorrectEncoding()
                        val dataListJson = Gson().fromJson(json, Array<DataDTO>::class.java).toList()
                        val hash = call.user?.hash
                        if (hash != null) {

                            when (dataDAO.insertData(hash, dataListJson)) {
                                true -> {
                                    call.respond(HttpStatusCode.OK, gson.toJson(dataListJson))
                                }

                                false -> {
                                    call.respond(HttpStatusCode.BadRequest)
                                }

                                else -> {
                                    call.respond(HttpStatusCode.Conflict,
                                            "Data already on database")
                                }
                            }
                        }
                    }

                } catch (e: JsonSyntaxException) {
                    // un pelín feo, pero así mostramos siempre el error que es..
                    call.respond(HttpStatusCode.BadRequest,
                            e.message?.split(":")?.get(1)
                                    ?: "There's something weird with that request, try it again..")
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest,
                            e.message?.split(":")?.get(1)
                                    ?: "There's something weird with that request, try it again..")
                }


            }

            put("/data") {
                val data = call.receive<DataDTO>()
                val hash = call.user?.hash

                if (hash != null) {
                    if (dataDAO.updateData(data, hash)) {
                        call.respond(HttpStatusCode.OK,
                                gson.toJson(dataDAO.getData(hash, data.id) ?: ""))
                    } else {
                        call.respond(HttpStatusCode.NotFound,
                            "No data found")
                    }
                }
            }

            put("/data/{id}") {
                val dataId = call.parameters["id"]?.toInt()
                val hash = call.user?.hash

                if (hash != null && dataId != null) {
                    if (dataDAO.updateData(dataId, hash)) {
                        call.respond(HttpStatusCode.OK,
                                gson.toJson(dataDAO.getData(hash, dataId) ?: ""))
                    } else {
                        call.respond(HttpStatusCode.NotFound,
                            "Are you sure it exists? dataId: $dataId")
                    }
                }
            }

            delete("/data/{id}") {
                val dataId = call.parameters["id"]?.toInt()
                val hash = call.user?.hash

                if (hash != null && dataId != null) {
                    if (dataDAO.deleteData(dataId, hash)) {
                        call.respond(HttpStatusCode.OK, dataId)
                    } else {
                        call.respond(HttpStatusCode.NotFound,
                            "Are you sure it exists? dataId: $dataId")
                    }
                }
            }

        }

    }
}
