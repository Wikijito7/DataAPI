package es.wokis.images.dto

import io.ktor.auth.*

data class CredentialsDTO(val hash: String): Principal