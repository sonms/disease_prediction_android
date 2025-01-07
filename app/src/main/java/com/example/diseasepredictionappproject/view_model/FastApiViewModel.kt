package com.example.diseasepredictionappproject.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.view_model.repository.RetrofitFastApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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