package es.wokis.utils

import es.wokis.dataapi.dto.CredentialsDTO
import io.ktor.application.*
import io.ktor.auth.*


val ApplicationCall.user: CredentialsDTO? get() = authentication.principal()