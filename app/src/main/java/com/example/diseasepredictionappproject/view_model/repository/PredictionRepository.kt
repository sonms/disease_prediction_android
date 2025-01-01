package com.example.diseasepredictionappproject.view_model.repository

import com.example.diseasepredictionappproject.room_db.PredictionDao
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PredictionRepository @Inject constructor(private val predictionDao: PredictionDao) {
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
}