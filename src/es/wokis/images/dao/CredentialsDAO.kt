package es.wokis.images.dao

import es.wokis.images.dao.interfaces.ICredentialsDAO
import es.wokis.images.models.Credential
import es.wokis.images.models.Credentials
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class CredentialsDAO : ICredentialsDAO {
    override fun insertHash(hash: String): Boolean {
        var inserted = true

        if (getHash(hash) != null) return false // ya existe el token

        transaction {
            try {
                Credential.new {
                    this.hash = hash
                    createdOn = LocalDate.now()
                }
            } catch (e: ExposedSQLException) { // por si acaso ocurre cualquier error.
                inserted = false
            }
        }
        return inserted
    }

    override fun getHash(hash: String): String? {
        var tokenRetrieved: String? = null
        transaction {
            val credential = Credential.find { Credentials.hash eq hash }.singleOrNull()

            if (credential != null) {
                tokenRetrieved = credential.hash
            }
        }

        return tokenRetrieved
    }

    override fun removeHash(hash: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun findByHash(hash: String): Credential? {
        var credential: Credential? = null
        transaction {
            credential = Credential.find { Credentials.hash eq hash }.singleOrNull()
        }

        return credential
    }
}