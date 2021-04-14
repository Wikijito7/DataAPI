package es.wokis.images.dao.interfaces

import es.wokis.images.models.Credential

interface ICredentialsDAO {
    fun insertHash(hash: String): Boolean
    fun getHash(hash: String): String?
    fun removeHash(hash: String): Boolean
    fun findByHash(hash: String): Credential?
}