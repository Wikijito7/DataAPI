package es.wokis.images.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.date

object Credentials : IntIdTable() {
    val hash = varchar("token", 15).uniqueIndex()
    val createdOn = date("createdOn")
}

// DAO
class Credential(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Credential>(Credentials)
    var hash by Credentials.hash
    var createdOn by Credentials.createdOn
    override fun toString(): String {
        return "Credential(token: $hash, createdOn: $createdOn)"
    }
}
