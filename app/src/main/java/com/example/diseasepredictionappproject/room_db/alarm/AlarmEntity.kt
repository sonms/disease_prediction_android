package com.example.diseasepredictionappproject.room_db.alarm

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AlarmTable")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo
    val alarmSetId : Long = 0L,

    @ColumnInfo
    val alarmTime: String, // 예: "08:00 AM" 같은 형식

    @ColumnInfo
    val alarmContent : String
)