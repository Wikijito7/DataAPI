package es.wokis.images.dao.interfaces

import es.wokis.images.dto.ImageDTO

interface IImagenesDAO {
    fun getImages(hash: String): List<ImageDTO>?
    fun getImage(hash: String, imageId: Int): ImageDTO?
    fun insertImages(hash: String, images: List<ImageDTO>): Boolean
    fun updateImage(imageId: Int): Boolean
}