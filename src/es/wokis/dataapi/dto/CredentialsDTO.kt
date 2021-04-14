package es.wokis.dataapi.dto

import io.ktor.auth.*

data class CredentialsDTO(val hash: String): Principal