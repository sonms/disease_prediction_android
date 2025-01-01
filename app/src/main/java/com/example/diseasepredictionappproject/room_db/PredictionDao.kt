package com.example.diseasepredictionappproject.room_db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionDao {
    @Query("SELECT * FROM PredictionTable ORDER BY createDate DESC")
    fun getPredictionList(): Flow<List<PredictionEntity>> // Flow 사용

    @Query("SELECT * FROM PredictionTable WHERE createDate = :date ORDER BY createDate DESC")
    fun getEventsByDate(date: String): Flow<List<PredictionEntity>> // 특정 날짜의 이벤트 조회

    @Query("SELECT * FROM PredictionTable WHERE strftime('%Y', createDate) = :year AND strftime('%m', createDate) = :month ORDER BY createDate DESC")
    fun getEventsByMonth(year: String, month: String): Flow<List<PredictionEntity>> // 년도와 월 데이터 조회

    @Query("SELECT * FROM PredictionTable WHERE isBookMark = 1 ORDER BY createDate DESC")
    fun getBookMarkedPredictions(): Flow<List<PredictionEntity>> // 북마크 필터링
}