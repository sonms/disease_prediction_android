package com.example.diseasepredictionappproject.room_db.medicine

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    //약
    @Query("SELECT * FROM MedicineTable ORDER BY updateDe DESC")
    fun getMedicineList(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM MedicineTable WHERE isBookMark = 1 ORDER BY updateDe DESC")
    fun getBookMarkedMedicines(): Flow<List<MedicineEntity>> // 북마크 필터링

    @Query("SELECT * FROM MedicineTable WHERE id = :id")
    suspend fun getSavedMedicineDataById(id : Long) : MedicineEntity?

    @Query(
        """
        SELECT * FROM MedicineTable 
        WHERE itemName LIKE '%' || :searchText || '%' 
        OR entpName LIKE '%' || :searchText || '%'
        OR efcyQesitm LIKE '%' || :searchText || '%'
        """
    )
    fun searchMedicineByTitleOrContent(searchText: String): Flow<List<MedicineEntity>>


    @Insert
    suspend fun insertMedicineData(medicineData: MedicineEntity) // Room과 ViewModel의 비동기 처리 일관성을 위해 suspend로 변경

    @Query("DELETE FROM MedicineTable WHERE id = :id")
    suspend fun deleteMedicineData(id: Long) // Room과 ViewModel의 비동기 처리 일관성을 위해 suspend로 변경

    // 전체 PredictionEntity 업데이트하는 메서드 (기본 방법)
    @Update
    suspend fun updateMedicineData(medicineData: MedicineEntity)

    // 특정 필드만 업데이트하는 메서드 (예: content, expenditure 및 income만)
    @Query("UPDATE MedicineTable SET isBookMark = :isBookMarked WHERE id = :id")
    suspend fun updateMedicineFields(id: Long, isBookMarked : Boolean)
}