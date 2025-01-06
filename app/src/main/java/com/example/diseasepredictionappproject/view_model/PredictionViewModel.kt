package com.example.diseasepredictionappproject.view_model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.view_model.repository.PredictionRepository
import com.example.diseasepredictionappproject.view_model.repository.RetrofitFastApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val repository: PredictionRepository,
    private val retrofitRepository: RetrofitFastApiRepository
) : ViewModel() {

    //모든 예측데이터
    val allPredictionsData = repository.getAllPredictionData()

    //모든 북마크 데이터
    val bookMarkedPredictionData = repository.getBookMarkPredictionData()


    //---------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchEventsByMonth(yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }

    //선택한 날짜 에 따른 데이터 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedMonth = MutableStateFlow(YearMonth.now()) // 초기값 설정
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedMonthEvents: Flow<List<PredictionEntity>> = _selectedMonth
        .flatMapLatest { month ->
            val formattedMonth = String.format("%02d", month.monthValue) // Format month as two digits
            Log.e("SelectedMonth", "Fetching events for: ${month.year}-$formattedMonth")
            repository.getMonthYear(
                year = month.year.toString(),
                month = formattedMonth
            )
        }


    // 선택된 ID를 담을 StateFlow
    private val _selectedId = MutableStateFlow<Long?>(null)
    // 선택된 ID에 해당하는 데이터를 담을 StateFlow
    private val _selectedSavedItem = MutableStateFlow<PredictionEntity?>(null)
    // 외부에서 관찰할 수 있도록 public StateFlow
    val selectedSavedItem: StateFlow<PredictionEntity?> = _selectedSavedItem

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
                    val event = repository.getDataById(id)
                    _selectedSavedItem.value = event
                } else {
                    _selectedSavedItem.value = null
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addPredictionData(diseaseName : String, diseaseContent : String, isBookMark : Boolean, recommendMedication : String) {
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
            val newData = PredictionEntity(
                diseaseName = diseaseName,
                diseaseContent = diseaseContent,
                isBookMark = isBookMark,
                recommendMedication = recommendMedication
            )
            repository.insertPredictionData(newData)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateFinancialData(id: Long, diseaseName : String, diseaseContent : String, isBookMark : Boolean, recommendMedication : String) {
        /*
        *  val categoryId: Long?,
    val content: String?,
    val createDate: String = LocalDateTime.now().toString(),
    val date: String?,
    val expenditure: Long?,
    val income: Long?*/
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
    }

    //특정필드 업데이트
    fun updateOnlySpecificData(id: Long, isBookMark: Boolean) {
        viewModelScope.launch {
            repository.updateOnlySpecificData(id, isBookMark)
        }
    }

    fun deletePredictionData(predictionEntity: PredictionEntity) {
        viewModelScope.launch {
            repository.deleteDataById(predictionEntity.id)
        }
    }



    //fast api 연동하여 질병 값 가져오기
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun fetchUIState(state: UiState) {
        _uiState.value = state
    }

    fun fetchPrediction(features: PredictionFeaturesData) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val call = retrofitRepository.postPredictionFeatures(features)
                call.enqueue(object : Callback<PredictionDiseaseResponse> {
                    override fun onResponse(
                        call: Call<PredictionDiseaseResponse>,
                        response: Response<PredictionDiseaseResponse>
                    ) {
                        if (response.isSuccessful) {
                            val diseaseName = response.body()?.diseaseName
                            if (diseaseName != null) {
                                // Success 상태가 여러 번 호출되지 않도록 보장
                                if (_uiState.value !is UiState.Success) {
                                    _uiState.value = UiState.Success(diseaseName)
                                }
                            } else {
                                _uiState.value = UiState.Error("예측된 결과가 존재하지 않습니다. 다시 시도해주세요.")
                            }
                        } else {
                            _uiState.value = UiState.Error("Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<PredictionDiseaseResponse>, t: Throwable) {
                        _uiState.value = UiState.Error("Failure: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Exception: ${e.message}")
            }
        }
    }

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val data: String) : UiState()
        data class Error(val message: String) : UiState()
        data object Wait : UiState()
    }
}