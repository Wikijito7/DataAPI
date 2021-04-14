package es.wokis.images.dao

import es.wokis.images.dao.interfaces.IDataDAO
import es.wokis.images.dto.DataDTO
import es.wokis.images.models.Credential
import es.wokis.images.models.Data
import es.wokis.images.models.Datas
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction

class DataDAO : IDataDAO {
    override fun getData(hash: String): List<DataDTO>? {
        val dataList: MutableList<DataDTO> = mutableListOf()
        val credential: Credential = CredentialsDAO().findByHash(hash) ?: return null

        transaction {
            val images = Data.find { Datas.uploadBy eq credential.id }
            images.forEach {
                val imageDTO = DataDTO(it.dataId, it.title, it.description, it.urlImage,
                    it.isFavorite)

                dataList.add(imageDTO)
            }
        }

        return dataList
    }

    override fun getData(hash: String, dataId: Int): DataDTO? {
        var image: DataDTO? = null
        val imageDB = getDataDB(hash, dataId)

        if (imageDB != null) {
            image = DataDTO(imageDB.dataId, imageDB.title, imageDB.description,
                imageDB.urlImage, imageDB.isFavorite)
        }

        return image
    }

    override fun getDataDB(hash: String, dataId: Int): Data? {
        var image: Data? = null
        val credential: Credential = CredentialsDAO().findByHash(hash) ?: return null
        transaction {
            image = Data.find {
                Datas.uploadBy eq credential.id
                Datas.dataId eq dataId
            }.singleOrNull()
        }

        return image
    }

    override fun insertData(hash: String, data: List<DataDTO>): Boolean {
        val credential: Credential = CredentialsDAO().findByHash(hash) ?: return false
        var inserted = true
        println(data)
        transaction {
            for (image: DataDTO in data) {
                try {
                    Data.new {
                        dataId = image.id
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

    override fun updateData(dataId: Int, hash: String): Boolean {
        val image = getDataDB(hash, dataId) ?: return false
        transaction {
            image.isFavorite = true
        }
        return true
    }
}