package com.example.diseasepredictionappproject.room_db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
@Entity(
    tableName = "PredictionTable",
   /* foreignKeys = [
        ForeignKey(
            entity = AlarmEntity::class,
            parentColumns = ["id"],
            childColumns = ["alarmId"],
            onDelete = ForeignKey.SET_NULL // 알람이 삭제되면 관련 데이터의 alarmId를 NULL로 설정
        )
    ],
    indices = [Index(value = ["alarmId"])] // 검색 속도 향상을 위한 인덱스 추가*/
)

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
    var recommendMedication : String?, //추천 약
    @ColumnInfo
    val alarmId: Long? = null // 연결된 알람 ID (없을 수도 있음)
)
