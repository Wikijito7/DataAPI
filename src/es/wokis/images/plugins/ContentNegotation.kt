package es.wokis.images.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.serialization.*

fun Application.initContentNegotation() {
    install(ContentNegotiation) {
        gson {
            disableHtmlEscaping()
        }
    }
}