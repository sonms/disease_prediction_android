package com.example.diseasepredictionappproject.view_model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diseasepredictionappproject.room_db.alarm.AlarmEntity
import com.example.diseasepredictionappproject.view_model.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor (
    private val repository: AlarmRepository
) : ViewModel() {

    private val _alarmData = MutableStateFlow<AlarmEntity?>(null)
    val alarmData: StateFlow<AlarmEntity?> = _alarmData.asStateFlow()

    fun fetchAlarmData(alarmId: Long) {
        viewModelScope.launch {
            val alarm = repository.getAlarmById(alarmId)
            _alarmData.value = alarm
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarmData(addData : AlarmEntity) {
        viewModelScope.launch {
            repository.insertAlarmData(addData)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateAlarmData(newData : AlarmEntity?) {
        viewModelScope.launch {
            if (newData != null) {
                repository.insertAlarmData(newData)
            }
        }
    }

    fun deleteAlarmData(alarmEntity: AlarmEntity) {
        viewModelScope.launch {
            repository.deleteAlarmDataById(alarmEntity.id)
        }
    }

}