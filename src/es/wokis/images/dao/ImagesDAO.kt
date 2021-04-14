package es.wokis.images.dao

import es.wokis.images.dao.interfaces.IImagenesDAO
import es.wokis.images.dto.ImageDTO
import es.wokis.images.models.Credential
import es.wokis.images.models.Image
import es.wokis.images.models.Images
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction

class ImagesDAO : IImagenesDAO {
    override fun getImages(hash: String): List<ImageDTO>? {
        val imageList: MutableList<ImageDTO> = mutableListOf()
        val credential: Credential = CredentialsDAO().findByHash(hash) ?: return null

        transaction {
            val images = Image.find { Images.uploadBy eq credential.id }
            images.forEach {
                val imageDTO = ImageDTO(it.imageId, it.title, it.description, it.urlImage,
                    it.isFavorite)

                imageList.add(imageDTO)
            }
        }

        return imageList
    }

    override fun getImage(hash: String, imageId: Int): ImageDTO? {
        TODO("Not yet implemented")
    }

    override fun insertImages(hash: String, images: List<ImageDTO>): Boolean {
        val credential: Credential = CredentialsDAO().findByHash(hash) ?: return false
        var inserted = true
        println(images)
        transaction {
            for (image: ImageDTO in images) {
                try {
                    Image.new {
                        imageId = image.id
                        title = image.title
                        description = image.description
                        urlImage = image.urlImage
                        isFavorite = image.isFavorite
                        uploadBy = credential
                    }
                } catch (e: ExposedSQLException) {
                    inserted = false
                    break
                }
            }
        }
        return inserted
    }

    override fun updateImage(imageId: Int): Boolean {
        TODO("Not yet implemented")
    }
}