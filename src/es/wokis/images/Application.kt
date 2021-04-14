package es.wokis.images

import es.wokis.images.plugins.*
import io.ktor.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    initDatabase()
    initAuth()
    initContentNegotation()
    initCORS()
    initRoutes()

}

