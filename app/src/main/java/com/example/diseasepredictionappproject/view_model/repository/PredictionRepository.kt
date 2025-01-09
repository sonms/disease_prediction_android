package com.example.diseasepredictionappproject.view_model.repository

import com.example.diseasepredictionappproject.room_db.PredictionDao
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PredictionRepository @Inject constructor(private val predictionDao: PredictionDao) {

    fun getLatestPredictionData() : Flow<PredictionEntity?> {
        return predictionDao.getLatestPrediction()
    }

    //모든 저장된 예측데이터 가져오기
    fun getAllPredictionData(): Flow<List<PredictionEntity>> {
        return predictionDao.getPredictionList()
    }

    //date에 해당하는 데이터들 가져오기
    fun getDatePredictionData(date : String) : Flow<List<PredictionEntity>> {
        return predictionDao.getEventsByDate(date)
    }

    //해당 년도, 월에 해당하는 데이터 가져오기
    fun getMonthYear(year: String, month : String) : Flow<List<PredictionEntity>> {
        return predictionDao.getEventsByMonth(year, month)
    }

    //즐겨찾기 = 북마크 설정되어있는 데이터 가져오기
    fun getBookMarkPredictionData() : Flow<List<PredictionEntity>> {
        return predictionDao.getBookMarkedPredictions()
    }



    // 특정 ID에 해당하는 데이터를 가져오는 함수
    suspend fun getDataById(id: Long?): PredictionEntity? {
        return id?.let {
            predictionDao.getSavedDataById(id)
        }
    }

    suspend fun insertPredictionData(data: PredictionEntity) {
        predictionDao.insertPredictionData(data)
    }

    //데이터 수정
    suspend fun updateData(data : PredictionEntity) {
        predictionDao.updatePredictionData(data)
    }

    // 할 일 삭제
    suspend fun deleteDataById(id: Long) {
        predictionDao.deletePredictionData(id)
    }

    //특정 데이터만 수정
    suspend fun updateOnlySpecificData(id : Long, isBookMarked : Boolean) {
        predictionDao.updatePredictionFields(id, isBookMarked)
    }
}