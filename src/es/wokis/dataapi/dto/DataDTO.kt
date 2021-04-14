package es.wokis.dataapi.dto

import com.google.gson.annotations.SerializedName

data class DataDTO(@SerializedName("id") val id: Int,
                   @SerializedName("title") val title: String,
                   @SerializedName("description") val description: String,
                   @SerializedName("urlImage") val urlImage: String,
                   @SerializedName("isFavorite") val isFavorite: Boolean)
