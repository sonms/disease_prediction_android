package com.example.diseasepredictionappproject.room_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.diseasepredictionappproject.data.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionDao {
    //질병
    @Query("SELECT * FROM PredictionTable ORDER BY createDate DESC LIMIT 1")
    fun getLatestPrediction(): Flow<PredictionEntity?> // Flow 사용, 없을 경우 null 반환

    @Query("SELECT * FROM PredictionTable ORDER BY createDate DESC")
    fun getPredictionList(): Flow<List<PredictionEntity>> // Flow 사용

    @Query("SELECT * FROM PredictionTable WHERE createDate = :date ORDER BY createDate DESC")
    fun getEventsByDate(date: String): Flow<List<PredictionEntity>> // 특정 날짜의 이벤트 조회

    @Query("SELECT * FROM PredictionTable WHERE strftime('%Y', createDate) = :year AND strftime('%m', createDate) = :month ORDER BY createDate DESC")
    fun getEventsByMonth(year: String, month: String): Flow<List<PredictionEntity>> // 년도와 월 데이터 조회

    @Query("SELECT * FROM PredictionTable WHERE isBookMark = 1 ORDER BY createDate DESC")
    fun getBookMarkedPredictions(): Flow<List<PredictionEntity>> // 북마크 필터링

    @Query("SELECT * FROM PredictionTable WHERE id = :id")
    suspend fun getSavedDataById(id : Long) : PredictionEntity?

    @Query(
        """
        SELECT * FROM PredictionTable 
        WHERE diseaseName LIKE '%' || :searchText || '%' 
        OR diseaseContent LIKE '%' || :searchText || '%'
        """
    )
    fun searchPredictionByTitleOrContent(searchText: String): Flow<List<PredictionEntity>>


    //예측 데이터 insert, delete, update
    @Insert
    suspend fun insertPredictionData(predictionData: PredictionEntity) // Room과 ViewModel의 비동기 처리 일관성을 위해 suspend로 변경

    @Query("DELETE FROM PredictionTable WHERE id = :id")
    suspend fun deletePredictionData(id: Long) // Room과 ViewModel의 비동기 처리 일관성을 위해 suspend로 변경

    // 전체 PredictionEntity 업데이트하는 메서드 (기본 방법)
    @Update
    suspend fun updatePredictionData(predictionData: PredictionEntity)

    // 특정 필드만 업데이트하는 메서드 (예: content, expenditure 및 income만)
    @Query("UPDATE PredictionTable SET isBookMark = :isBookMarked WHERE id = :id")
    suspend fun updatePredictionFields(id: Long, isBookMarked : Boolean)

}