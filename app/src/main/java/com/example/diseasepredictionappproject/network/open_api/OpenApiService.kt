package com.example.diseasepredictionappproject.network.open_api

import com.example.diseasepredictionappproject.data.DrugInfoRequest
import com.example.diseasepredictionappproject.data.DrugInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenApiService {
    /*@POST("getDrbEasyDrugList")
    fun getDrbEasyDrugList(@Body request : DrugInfoRequest) : Call<DrugInfoResponse>*/

    @GET("getDrbEasyDrugList")
    fun getDrbEasyDrugList(
        @Query("serviceKey") serviceKey: String, // 인증키
        @Query("pageNo") pageNo: Int? = 1, // 페이지 번호 (옵션)
        @Query("numOfRows") numOfRows: Int? = 3, // 한 페이지 결과 수 (옵션)
        @Query("entpName") entpName: String? = null, // 업체명 (옵션)
        @Query("itemName") itemName: String? = null, // 제품명 (옵션)
        @Query("itemSeq") itemSeq: String? = null, // 품목기준코드 (옵션)
        @Query("efcyQesitm") efcyQesitm: String? = null, // 문항1(효능) (옵션)
        @Query("useMethodQesitm") useMethodQesitm: String? = null, // 문항2(사용법) (옵션)
        @Query("atpnWarnQesitm") atpnWarnQesitm: String? = null, // 문항3(주의사항경고) (옵션)
        @Query("atpnQesitm") atpnQesitm: String? = null, // 문항4(주의사항) (옵션)
        @Query("intrcQesitm") intrcQesitm: String? = null, // 문항5(상호작용) (옵션)
        @Query("seQesitm") seQesitm: String? = null, // 문항6(부작용) (옵션)
        @Query("depositMethodQesitm") depositMethodQesitm: String? = null, // 문항7(보관법) (옵션)
        @Query("openDe") openDe: String? = null, // 공개일자 (옵션)
        @Query("updateDe") updateDe: String? = null, // 수정일자 (옵션)
        @Query("type") type: String? // 데이터 포맷 (옵션, 기본값: xml)
    ): Call<DrugInfoResponse>
}