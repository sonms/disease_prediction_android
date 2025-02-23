package com.example.diseasepredictionappproject.view_model.repository

import com.example.diseasepredictionappproject.room_db.alarm.AlarmDao
import com.example.diseasepredictionappproject.room_db.alarm.AlarmEntity
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val dao: AlarmDao) {
    suspend fun insertAlarmData(data: AlarmEntity) {
        dao.insertAlarm(data)
    }

    //데이터 수정
    suspend fun updateAlarmData(data : AlarmEntity) {
        dao.updateAlarm(data)
    }

    // 삭제
    suspend fun deleteAlarmDataById(id: Long) {
        dao.deleteAlarm(id)
    }

    suspend fun getAlarmById(alarmId: Long): AlarmEntity? {
        return dao.getAlarmById(alarmId)
    }
}