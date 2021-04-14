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

private val credentialsDao = CredentialsDAO()
private val dataDAO = DataDAO()
private val gson = Gson()

fun Application.initRoutes() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK,"HELLO WORLD!")
        }
        get("/token") {
            val generatedHash = HashGenerator.generateHash(12L)
            val token = generateToken(generatedHash)
            val tokenHashDTO = TokenHashDTO(generatedHash, token)

            if (credentialsDao.insertHash(generatedHash)) {
                call.respondText(gson.toJson(tokenHashDTO))
            }
        }

        get ("/token/{hash}") {
            val hash = call.parameters["hash"]
            if (hash != null && credentialsDao.getHash(hash) != null) {
                call.respond(generateToken(hash))
            } else if (hash == null) {
                call.respond(HttpStatusCode.BadRequest, "El hash es requerido")
            } else {
                call.respond(HttpStatusCode.NotFound, hash)
            }
        }

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
                            val dataList = dataDAO.insertData(hash, dataListJson)

                            if (dataList){
                                call.respond(HttpStatusCode.OK, gson.toJson(dataListJson))
                            } else {
                                call.respond(HttpStatusCode.BadRequest)
                            }
                        }
                    }

                } catch (e: JsonSyntaxException) {
                    // un pelín feo, pero así mostramos siempre el error que es..
                    call.respond(HttpStatusCode.BadRequest,
                        e.message?.split(":")?.get(1) ?:
                        "Ha habido un error con la petición, revisa el json.")
                }


            }

            put("/data") {
                val data = call.receive<DataDTO>()
                val hash = call.user?.hash

                if (hash != null) {
                    if (dataDAO.updateData(data, hash)) {
                        call.respond(HttpStatusCode.OK,
                            gson.toJson(dataDAO.getDataList(hash, data.id) ?: ""))
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
                            gson.toJson(dataDAO.getDataList(hash, dataId) ?: ""))
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
