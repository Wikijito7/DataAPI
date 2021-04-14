package es.wokis.images.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import es.wokis.images.dto.CredentialsDTO
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*


private lateinit var algorithm: Algorithm

fun Application.initAuth() {
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val secretKey = environment.config.property("secretkey").getString()
    algorithm = Algorithm.HMAC256(secretKey)

    authentication {
        jwt {
            realm = jwtRealm
            verifier(makeJwtVerifier())
            validate { credential ->
                val token = credential.payload.claims["token"]?.asString()
                if (token != null)
                    CredentialsDTO(token) else null
            }
        }
    }

}

private fun makeJwtVerifier() : JWTVerifier = JWT
    .require(algorithm)
    .build()

fun generateToken(randomHash: String): String = JWT.create()
    .withSubject("Authentification")
    .withClaim("token", randomHash)
    .sign(algorithm)