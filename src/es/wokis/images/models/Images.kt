package es.wokis.images.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Images : IntIdTable() {
    val imageId = integer("imageId")
    val title = varchar("title", 100)
    val description = text("description", eagerLoading = true)
    val urlImage = varchar("urlImage", 255)
    val isFavorite = bool("isFavorite")
    val uploadBy = reference("hash", Credentials)
}

class Image(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Image>(Images)
    var imageId by Images.imageId
    var title by Images.title
    var description by Images.description
    var urlImage by Images.urlImage
    var isFavorite by Images.isFavorite
    var uploadBy by Credential referencedOn Images.uploadBy

    override fun toString(): String {
        return "Image(id: $imageId, title: $title, description: $description, url: $urlImage, " +
                "uploadBy: $uploadBy)"
    }
}