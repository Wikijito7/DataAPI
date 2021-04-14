package es.wokis.images.dto

data class ImageDTO(val id: Int,
                     val title: String,
                     val description: String,
                     val urlImage: String,
                     val isFavorite: Boolean)
