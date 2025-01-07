package com.example.diseasepredictionappproject.data

import com.google.gson.annotations.SerializedName

data class DrugInfoRequest(
    @SerializedName("ServiceKey") val serviceKey: String, // 인증키
    @SerializedName("pageNo") val pageNo: Int? = 1, // 페이지 번호 (옵션)
    @SerializedName("numOfRows") val numOfRows: Int? = 3, // 한 페이지 결과 수 (옵션)
    @SerializedName("entpName") val entpName: String? = null, // 업체명 (옵션)
    @SerializedName("itemName") val itemName: String? = null, // 제품명 (옵션)
    @SerializedName("itemSeq") val itemSeq: String? = null, // 품목기준코드 (옵션)
    @SerializedName("efcyQesitm") val efcyQesitm: String? = null, // 문항1(효능) (옵션)
    @SerializedName("useMethodQesitm") val useMethodQesitm: String? = null, // 문항2(사용법) (옵션)
    @SerializedName("atpnWarnQesitm") val atpnWarnQesitm: String? = null, // 문항3(주의사항경고) (옵션)
    @SerializedName("atpnQesitm") val atpnQesitm: String? = null, // 문항4(주의사항) (옵션)
    @SerializedName("intrcQesitm") val intrcQesitm: String? = null, // 문항5(상호작용) (옵션)
    @SerializedName("seQesitm") val seQesitm: String? = null, // 문항6(부작용) (옵션)
    @SerializedName("depositMethodQesitm") val depositMethodQesitm: String? = null, // 문항7(보관법) (옵션)
    @SerializedName("openDe") val openDe: String? = null, // 공개일자 (옵션)
    @SerializedName("updateDe") val updateDe: String? = null, // 수정일자 (옵션)
    @SerializedName("type") val type: String? // 데이터 포맷 (옵션, 기본값: xml)
)
