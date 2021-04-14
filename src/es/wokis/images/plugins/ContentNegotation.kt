package es.wokis.images.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*

fun Application.initContentNegotation() {
    install(ContentNegotiation) {
        gson {
        }
    }
}