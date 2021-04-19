package es.wokis.dataapi.utils

import es.wokis.dataapi.models.Credential
import io.ktor.application.*
import io.ktor.auth.*


val ApplicationCall.user: Credential? get() = authentication.principal()