package es.wokis.dataapi.dao.interfaces

import es.wokis.dataapi.dto.DataDTO
import es.wokis.dataapi.models.Data

interface IDataDAO {
    fun getDataList(hash: String): List<DataDTO>?
    fun getData(hash: String, dataId: Int): DataDTO?
    fun getDataDB(hash: String, dataId: Int): Data?
    fun insertData(hash: String, data: List<DataDTO>): Boolean?
    fun updateData(dataId: Int, hash: String): Boolean
    fun updateData(data: DataDTO, hash: String): Boolean
    fun deleteData(dataId: Int, hash: String): Boolean
}