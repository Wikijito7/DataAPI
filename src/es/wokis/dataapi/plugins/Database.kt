package es.wokis.dataapi.plugins

import es.wokis.dataapi.models.Credential
import es.wokis.dataapi.models.Credentials
import es.wokis.dataapi.models.Data
import es.wokis.dataapi.models.Datas
import io.ktor.application.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.IOException
import java.time.LocalDate

fun Application.initDatabase() {
    val path = environment.config.property("db.path").getString()
    val filename = environment.config.property("db.databaseName").getString()
    checkDatabaseExists(path, filename)
    Database.connect("jdbc:sqlite:$path$filename", "org.sqlite.JDBC") // TODO: Change to app config

    transaction {
        // Creamos las tablas si no existen
        SchemaUtils.create(Datas, Credentials)
        // datos de serie, podemos borrarlo. Solo se insertan una vez.
        try {
            val credential = Credential.new {
                hash = "aaaaabbbbb123"
                createdOn = LocalDate.now()
            }

            val image = Data.new {
                dataId = 0
                title = "Abstract"
                description = "Arte abstracto, no tiene más!"
                urlImage = "https://images.unsplash.com/photo-1615012553971-f7251c225e01?i" +
                        "xid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1" +
                        "&auto=format&fit=crop&w=1868&q=80"
                isFavorite = false
                uploadBy = credential
            }

            println("credential: $credential, image: $image")
        } catch (e: ExposedSQLException) {
            environment.log.warn("[database] Creo que ha habido un error, puede que no sea " +
                    "importante, peeero: ${e.message}")
        }

    }
}

private fun checkDatabaseExists(path: String, filename: String) {
    val file = File(path, filename)

    if (!file.exists()) {
        try {
            file.parentFile.mkdirs()
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}