package com.example.diseasepredictionappproject.view_model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.view_model.repository.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val repo : MedicineRepository
) : ViewModel() {
    //모든 데이터
    val allMedicineData = repo.getAllMedicineData()

    //모든 북마크 데이터
    val bookMarkedMedicineData = repo.getBookMarkMedicineData()


    //약 추천 결과값 관찰용
    private val _medicineResultData = MutableStateFlow<List<Item?>>(emptyList())

    // 외부에서 읽기 전용 StateFlow로 노출
    val medicineResultData: StateFlow<List<Item?>> get() = _medicineResultData

    // 데이터 갱신 함수
    fun updateMedicineResultData(data: List<Item>) {
        _medicineResultData.value = data
    }

    //약 데이터 이동 관찰용
    private val _medicineMoveData = MutableStateFlow<Item?>(null)

    // 외부에서 읽기 전용 StateFlow로 노출
    val medicineMoveData: StateFlow<Item?> get() = _medicineMoveData

    // 데이터 갱신 함수
    fun updateMedicineMoveData(data: Item) {
        _medicineMoveData.value = data
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addPredictionData(addData : MedicineEntity) {
        /*
        var diseaseName : String?, //저장된 거 이름
        @ColumnInfo
        var diseaseContent : String?, //저장된 거 정보요약 / itching등
        @ColumnInfo
        val createDate: String = LocalDateTime.now().toString(),
        @ColumnInfo
        var isBookMark : Boolean?, //북마크 여부
        @ColumnInfo
        var recommendMedication : String? //추천 약
        */

        viewModelScope.launch {
            repo.insertMedicineData(addData)
        }
    }


   /* @RequiresApi(Build.VERSION_CODES.O)
    fun updateFinancialData(id: Long, diseaseName : String, diseaseContent : String, isBookMark : Boolean, recommendMedication : String) {
        viewModelScope.launch {
            val newData = PredictionEntity(
                id = id,
                diseaseName = diseaseName,
                diseaseContent = diseaseContent,
                isBookMark = isBookMark,
                recommendMedication = recommendMedication
            )
            repository.updateData(newData)
        }
    }*/

    //특정필드 업데이트
    fun updateOnlySpecificMedicineData(id: Long, isBookMark: Boolean) {
        viewModelScope.launch {
            repo.updateOnlySpecificMedicineData(id, isBookMark)
        }
    }

    fun deleteMedicineData(medicineEntity: MedicineEntity) {
        viewModelScope.launch {
            repo.deleteMedicineDataById(medicineEntity.id)
        }
    }



}