package com.example.diseasepredictionappproject.view_model.repository

import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineDao
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MedicineRepository @Inject constructor(private val dao: MedicineDao) {

    fun getAllMedicineData(): Flow<List<MedicineEntity>> {
        return dao.getMedicineList()
    }

    //즐겨찾기 = 북마크 설정되어있는 데이터 가져오기
    fun getBookMarkMedicineData() : Flow<List<MedicineEntity>> {
        return dao.getBookMarkedMedicines()
    }

    suspend fun getDataById(id: Long?): MedicineEntity? {
        return id?.let {
            dao.getSavedMedicineDataById(id)
        }
    }

    suspend fun insertMedicineData(data: MedicineEntity) {
        dao.insertMedicineData(data)
    }

    //데이터 수정
    suspend fun updateMedicineData(data : MedicineEntity) {
        dao.updateMedicineData(data)
    }

    // 할 일 삭제
    suspend fun deleteMedicineDataById(id: Long) {
        dao.deleteMedicineData(id)
    }

    //특정 데이터만 수정
    suspend fun updateOnlySpecificMedicineData(id : Long, isBookMarked : Boolean) {
        dao.updateMedicineFields(id, isBookMarked)
    }
}