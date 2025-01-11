package com.example.diseasepredictionappproject.room_db.medicine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "MedicineTable")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo
    var isBookMark : Boolean?, //북마크 여부
    @SerializedName("entpName") val entpName: String?, // 업체명
    @SerializedName("itemName") val itemName: String?, // 제품명
    @SerializedName("itemSeq") val itemSeq: String?, // 품목기준코드
    @SerializedName("efcyQesitm") val efcyQesitm: String?, // 효능
    @SerializedName("useMethodQesitm") val useMethodQesitm: String?, // 사용법
    @SerializedName("atpnWarnQesitm") val atpnWarnQesitm: String?, // 주의사항 경고
    @SerializedName("atpnQesitm") val atpnQesitm: String?, // 주의사항
    @SerializedName("intrcQesitm") val intrcQesitm: String?, // 상호작용
    @SerializedName("seQesitm") val seQesitm: String?, // 부작용
    @SerializedName("depositMethodQesitm") val depositMethodQesitm: String?, // 보관법
    @SerializedName("openDe") val openDe: String?, // 공개일자
    @SerializedName("updateDe") val updateDe: String?, // 수정일자
    @SerializedName("itemImage") val itemImage: String?, // 낱알 이미지 (null일 경우가 있음)
    @SerializedName("bizrno") val bizrno: String? // 사업자번호
)
