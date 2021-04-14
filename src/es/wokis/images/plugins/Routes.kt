package es.wokis.images.plugins

import com.google.gson.Gson
import es.wokis.images.dao.CredentialsDAO
import es.wokis.images.dao.ImagesDAO
import es.wokis.images.dto.ImageDTO
import es.wokis.images.dto.TokenHashDTO
import es.wokis.images.generator.HashGenerator
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import es.wokis.utils.user
import io.ktor.request.*

private val credentialsDao = CredentialsDAO()
private val imagesDAO = ImagesDAO()
private val gson = Gson()

fun Application.initRoutes() {
    routing {
        get("/token") {
            val generatedHash = HashGenerator.generateHash(12L)
            val token = generateToken(generatedHash)
            val tokenHashDTO = TokenHashDTO(generatedHash, token)

            if (credentialsDao.insertHash(generatedHash)) {
                call.respondText(gson.toJson(tokenHashDTO))
            }
        }

        get ("/token/{hash}"){
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
            get("/hash") {
                call.respond(HttpStatusCode.OK, "${call.user?.hash}")
            }

            get("/data") {
                val hash = call.user?.hash
                if (hash != null) {
                    val images = imagesDAO.getImages(hash)

                    if (images != null){
                        call.respond(gson.toJson(images))
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }

                }

            }

            post("/data") {
                val imageList = call.receive<List<ImageDTO>>()
                println(imageList)
                val hash = call.user?.hash

                if (hash != null) {
                    val images = imagesDAO.insertImages(hash, imageList)

                    if (images){
                        call.respond(HttpStatusCode.OK, call.respond(gson.toJson(imageList)))
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }


            }

            put("/data/{id}") {
                //TODO: Llamar al controlador para actualizar los datos
                call.parameters["id"]
            }

        }

    }
}

