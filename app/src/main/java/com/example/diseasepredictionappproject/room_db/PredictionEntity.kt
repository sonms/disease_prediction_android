package com.example.diseasepredictionappproject.room_db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
@Entity(tableName = "PredictionTable")
data class PredictionEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,
    @ColumnInfo
    var diseaseName : String?, //저장된 거 이름
    @ColumnInfo
    var diseaseContent : String?, //저장된 거 정보요약 / itching등
    @ColumnInfo
    val createDate: String = LocalDateTime.now().toString(),
    @ColumnInfo
    var isBookMark : Boolean?, //북마크 여부
    @ColumnInfo
    var recommendMedication : String? //추천 약
)
