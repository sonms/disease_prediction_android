package com.example.diseasepredictionappproject.room_db.alarm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Query("DELETE FROM AlarmTable WHERE id = :alarmId")
    suspend fun deleteAlarm(alarmId: Long)

    @Query("SELECT * FROM AlarmTable WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Long): AlarmEntity?
}
