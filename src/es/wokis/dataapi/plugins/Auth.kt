package es.wokis.dataapi.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import es.wokis.dataapi.dao.CredentialsDAO
import es.wokis.dataapi.dto.CredentialsDTO
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*


private lateinit var algorithm: Algorithm

@Deprecated("It will have support up to version 1.2")
fun Application.initAuth() {
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val secretKey = environment.config.property("secretkey").getString()
    algorithm = Algorithm.HMAC256(secretKey)

    authentication {
        jwt {
            realm = jwtRealm
            verifier(makeJwtVerifier())
            validate { credential ->
                val hash = credential.payload.claims["token"]?.asString()
                if (hash != null) {
                    val credentialObj = CredentialsDAO().findByHash(hash)

                    credentialObj
                } else null
            }
        }
    }

}

private fun makeJwtVerifier() : JWTVerifier = JWT
    .require(algorithm)
    .build()

/**
 * Generates a new token to use it as authentication
 * @param hash the client-id, generated randomly.
 * @return the given token for the hash.
 * @Deprecated this function will disappear on version 1.2
 * */
@Deprecated("It will have support up to version 1.2")
fun generateToken(hash: String): String = JWT.create()
    .withSubject("Authentification")
    .withClaim("token", hash)
    .sign(algorithm)