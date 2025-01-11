package com.example.diseasepredictionappproject.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diseasepredictionappproject.data.DrugInfoRequest
import com.example.diseasepredictionappproject.data.DrugInfoResponse
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.view_model.repository.OpenApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class OpenApiViewModel @Inject constructor(
    private val openRepo : OpenApiRepository
) : ViewModel() {

    //open api 연동하여 질병 값 가져오기
    private val _openApiUiState = MutableStateFlow<OpenApiUiState>(OpenApiUiState.Loading)
    val openApiUiState: StateFlow<OpenApiUiState> = _openApiUiState

    fun fetchOpenApiUIState(state: OpenApiUiState) {
        _openApiUiState.value = state
    }

    fun fetchDrugInfo(
        serviceKey: String,
        pageNo: Int? = 1,
        numOfRows: Int? = 5,
        entpName: String? = null,
        itemName: String? = null,
        itemSeq: String? = null,
        efcyQesitm: String? = null,
        useMethodQesitm: String? = null,
        atpnWarnQesitm: String? = null,
        atpnQesitm: String? = null,
        intrcQesitm: String? = null,
        seQesitm: String? = null,
        depositMethodQesitm: String? = null,
        openDe: String? = null,
        updateDe: String? = null,
        type: String? = "json"
    ) {
        viewModelScope.launch {
            _openApiUiState.value = OpenApiUiState.Loading
            try {
                val call = openRepo.getDrugInfoData(
                    serviceKey = serviceKey,
                    pageNo = pageNo,
                    numOfRows = numOfRows,
                    entpName = entpName,
                    itemName = itemName,
                    itemSeq = itemSeq,
                    efcyQesitm = efcyQesitm,
                    useMethodQesitm = useMethodQesitm,
                    atpnWarnQesitm = atpnWarnQesitm,
                    atpnQesitm = atpnQesitm,
                    intrcQesitm = intrcQesitm,
                    seQesitm = seQesitm,
                    depositMethodQesitm = depositMethodQesitm,
                    openDe = openDe,
                    updateDe = updateDe,
                    type = type
                )

                call.enqueue(object : Callback<DrugInfoResponse> {
                    override fun onResponse(
                        call: Call<DrugInfoResponse>,
                        response: Response<DrugInfoResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseData = response.body()
                            if (responseData != null) {
                                _openApiUiState.value = OpenApiUiState.Success(responseData.body.items)
                            }
                        } else {
                            _openApiUiState.value = OpenApiUiState.Error("Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<DrugInfoResponse>, t: Throwable) {
                        _openApiUiState.value = OpenApiUiState.Error("Failure: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                _openApiUiState.value = OpenApiUiState.Error("Exception: ${e.message}")
            }
        }
    }

    sealed class OpenApiUiState {
        data object Loading : OpenApiUiState()
        data class Success(val data: List<Item>) : OpenApiUiState()
        data class Error(val message: String) : OpenApiUiState()
        data object Wait : OpenApiUiState()
    }
}