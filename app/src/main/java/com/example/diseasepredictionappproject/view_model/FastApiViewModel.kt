package com.example.diseasepredictionappproject.view_model

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diseasepredictionappproject.data.PillInfoDataRequest
import com.example.diseasepredictionappproject.data.PillInfoDataResponse
import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.data.PredictionImageResponse
import com.example.diseasepredictionappproject.view_model.repository.RetrofitFastApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FastApiViewModel @Inject constructor (
    private val retrofitRepository: RetrofitFastApiRepository
) : ViewModel() {

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
                                if (_uiState.value !is UiState.DiseasePrediction) {
                                    _uiState.value = UiState.DiseasePrediction(diseaseName)
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


    //image로 pill 예측
    fun fetchPillPrediction(image : Uri, context : Context) {
        viewModelScope.launch {
            try {
                val call = retrofitRepository.postImagePredictionPill(image, context)

                call.enqueue(object : Callback<PredictionImageResponse> {
                    override fun onResponse(
                        call: Call<PredictionImageResponse>,
                        response: Response<PredictionImageResponse>
                    ) {
                        if (response.isSuccessful) {
                            val pillName = response.body()?.predictionPillName
                            if (pillName != null) {
                                // Success 상태가 여러 번 호출되지 않도록 보장
                                if (_uiState.value !is UiState.PillPrediction) {
                                    _uiState.value = UiState.PillPrediction(pillName)
                                }
                            } else {
                                _uiState.value = UiState.Error("예측된 결과가 존재하지 않습니다. 다시 시도해주세요.")
                            }
                        } else {
                            _uiState.value = UiState.Error("Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<PredictionImageResponse>, t: Throwable) {
                        _uiState.value = UiState.Error("Failure: ${t.message}")
                    }
                })
            } catch (e : Exception) {
                _uiState.value = UiState.Error("Exception: ${e.message}")
            }
        }
    }


    //pillname으로 효능, 업소명 가져오기
    fun fetchPillInfo(pillName : PillInfoDataRequest) {
        viewModelScope.launch {
            try {
                val call = retrofitRepository.postPillNameInfo(pillName)

                call.enqueue(object : Callback<PillInfoDataResponse> {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResponse(
                        call: Call<PillInfoDataResponse>,
                        response: Response<PillInfoDataResponse>
                    ) {
                        if (response.isSuccessful) {
                            val pillInfo = response.body()
                            Log.e("pillinfo", pillInfo.toString())
                            if (pillInfo != null) {
                                // Success 상태가 여러 번 호출되지 않도록 보장
                                if (_uiState.value !is UiState.PillInfo) {
                                    val date = parseDateWithFormatter(pillInfo.openDate)//pillInfo.openDate.substring(0,4) + "-" +  pillInfo.openDate.substring(4,6) + "-" + pillInfo.openDate.substring(6,8)
                                    _uiState.value = UiState.PillInfo("${pillInfo.businessName}/${pillInfo.classificationName}/${date}")
                                }
                            } else {
                                _uiState.value = UiState.Error("예측된 결과가 존재하지 않습니다. 다시 시도해주세요.")
                            }
                        } else {
                            _uiState.value = UiState.Error("Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<PillInfoDataResponse>, t: Throwable) {
                        _uiState.value = UiState.Error("Failure: ${t.message}")
                    }
                })
            } catch (e : Exception) {
                _uiState.value = UiState.Error("Exception: ${e.message}")
            }
        }
    }

    sealed class UiState {
        data object Loading : UiState()
        data class DiseasePrediction(val data: String) : UiState()
        data class PillPrediction(val data: String) : UiState()
        data class PillInfo(val data: String) : UiState()
        data class Error(val message: String) : UiState()
        data object Wait : UiState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseDateWithFormatter(dateStr: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")  // 날짜 포맷 정의
        return LocalDate.parse(dateStr, formatter)
    }
}