package es.wokis.images.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Datas : IntIdTable() {
    val dataId = integer("dataId")
    val title = varchar("title", 100)
    val description = text("description", eagerLoading = true)
    val urlImage = varchar("urlImage", 255)
    val isFavorite = bool("isFavorite")
    val uploadBy = reference("hash", Credentials)
}

class Data(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Data>(Datas)
    var dataId by Datas.dataId
    var title by Datas.title
    var description by Datas.description
    var urlImage by Datas.urlImage
    var isFavorite by Datas.isFavorite
    var uploadBy by Credential referencedOn Datas.uploadBy

    override fun toString(): String {
        return "Data(id: $dataId, title: $title, description: $description, url: $urlImage, " +
                "uploadBy: $uploadBy)"
    }
}