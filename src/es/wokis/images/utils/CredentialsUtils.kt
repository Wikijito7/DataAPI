package es.wokis.utils

import es.wokis.images.dto.CredentialsDTO
import io.ktor.application.*
import io.ktor.auth.*


val ApplicationCall.user: CredentialsDTO? get() = authentication.principal()