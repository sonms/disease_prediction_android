package com.example.diseasepredictionappproject.view_model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.view_model.repository.PredictionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val repository: PredictionRepository
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

    fun deletePredictionData(predictionEntity: PredictionEntity) {
        viewModelScope.launch {
            repository.deleteDataById(predictionEntity.id)
        }
    }
}