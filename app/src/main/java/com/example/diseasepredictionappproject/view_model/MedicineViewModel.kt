package com.example.diseasepredictionappproject.view_model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.view_model.repository.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val repo : MedicineRepository,
) : ViewModel() {
    //모든 데이터
    val allMedicineData = repo.getAllMedicineData()

    //모든 북마크 데이터
    val bookMarkedMedicineData = repo.getBookMarkMedicineData()


    //약 추천 결과값 관찰용
    private val _medicineResultData = MutableStateFlow<List<Item>>(emptyList())
    val medicineResultData: StateFlow<List<Item>> = _medicineResultData.asStateFlow()

    fun updateMedicineResultData(data: List<Item>) {
        viewModelScope.launch {
            _medicineResultData.value = data
        }
    }

    // 선택된 ID를 담을 StateFlow
    private val _selectedId = MutableStateFlow<Long?>(null)
    // 선택된 ID에 해당하는 데이터를 담을 StateFlow
    private val _selectedSavedItem = MutableStateFlow<MedicineEntity?>(null)
    // 외부에서 관찰할 수 있도록 public StateFlow
    val selectedSavedItem: StateFlow<MedicineEntity?> = _selectedSavedItem

    // ID에 따라 데이터를 가져오는 함수
    fun setSelectedId(id: Long?) {
        _selectedId.value = id
    }
    init {
        // _selectedId가 변경될 때마다 이벤트를 처리하도록 설정
        viewModelScope.launch {
            _selectedId.collect { id ->
                // ID가 변경될 때마다 해당 ID에 맞는 데이터를 가져와서 _selectedSavedItem 업데이트
                if (id != null) {
                    val event = repo.getDataById(id)
                    _selectedSavedItem.value = event
                } else {
                    _selectedSavedItem.value = null
                }
            }
        }
    }


    //약 데이터 이동 관찰용
    private val _medicineMoveData = MutableStateFlow<Item?>(null)

    // 외부에서 읽기 전용 StateFlow로 노출
    val medicineMoveData: StateFlow<Item?> = _medicineMoveData.asStateFlow()



    private val _medicineSearchData = MutableStateFlow<List<MedicineEntity>>(emptyList())
    val medicineSearchData: StateFlow<List<MedicineEntity>> = _medicineSearchData.asStateFlow()
    fun searchMedicineData(searchText: String) {
        viewModelScope.launch {
            repo.searchMedicine(searchText) // Repository에서 검색 호출
                .collect { predictions ->
                    _medicineSearchData.value = predictions // 결과 업데이트
                }
        }
    }

    // 데이터 갱신 함수
    fun updateMedicineMoveData(data: Item) {
       viewModelScope.launch {
           _medicineMoveData.value = data
       }
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


   @RequiresApi(Build.VERSION_CODES.O)
    fun updateMedicineData(newData : MedicineEntity?) {
        viewModelScope.launch {
            if (newData != null) {
                repo.updateMedicineData(newData)
            }
        }
    }

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