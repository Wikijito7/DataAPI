package es.wokis.dataapi.dao

import es.wokis.dataapi.dao.interfaces.IDataDAO
import es.wokis.dataapi.dto.DataDTO
import es.wokis.dataapi.models.Credential
import es.wokis.dataapi.models.Data
import es.wokis.dataapi.models.Datas
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
        var data: Data? = null
        val credential: Credential = CredentialsDAO().findByHash(hash) ?: return null

        transaction {
            val dataDB = Data.find { Datas.uploadBy eq credential.id }
                .filter { it.dataId == dataId }

            data = if (dataDB.size == 1) dataDB[0] else null
        }

        return data
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
        val data = getDataDB(hash, dataId) ?: return false

        transaction {
            data.isFavorite = true
            commit()
        }

        return true
    }

    override fun updateData(data: DataDTO, hash: String): Boolean {
        val dataDB = getDataDB(hash, data.id) ?: return false

        transaction {
            dataDB.title = data.title
            dataDB.description = data.description
            dataDB.urlImage = data.urlImage
            dataDB.isFavorite = data.isFavorite
            commit()
        }

        return true
    }

    override fun deleteData(dataId: Int, hash: String): Boolean {
        val data = getDataDB(hash, dataId) ?: return false

        transaction {
            data.delete()
            commit()
        }

        return true
    }
}