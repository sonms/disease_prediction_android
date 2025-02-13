package com.example.diseasepredictionappproject.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.diseasepredictionappproject.service.AlarmReceiver
import java.util.Calendar

class AlarmHelper(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val TAG = "AlarmHelper"
    private val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)

    fun scheduleAlarm(dateTime: Calendar, requestCode: Int, title: String, message: String) {
        if (!canScheduleExactAlarms()) {
            requestExactAlarmPermission()
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dateTime.timeInMillis, pendingIntent)
            saveAlarm(requestCode)
            Log.d(TAG, " 알람 설정 완료: ${dateTime.time}, RequestCode: $requestCode")
        } catch (e: SecurityException) {
            Log.e(TAG, " 알람 설정 실패: SecurityException 발생", e)
            requestExactAlarmPermission()
        }
    }

    fun scheduleWeeklyAlarm(dayOfWeek: Int, hour: Int, minute: Int, requestCode: Int, title: String, message: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek+1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }
        Log.d(TAG, " 주간 알람 설정: 요일 ${dayOfWeek+1}, 시간 $hour:$minute")
        scheduleAlarm(calendar, requestCode, title, message)
    }

    fun cancelAlarm(requestCode: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        removeAlarm(requestCode)
        Log.d(TAG, " 알람 취소 완료: RequestCode: $requestCode")
    }

    fun cancelAllAlarms() {
        val alarmSet = getSavedAlarms()
        for (requestCode in alarmSet) {
            cancelAlarm(requestCode)
        }
        clearAllAlarms()
        Log.d(TAG, "모든 알람이 취소되었습니다.")
    }


    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Log.w(TAG, "⚠정밀 알람 권한 요청 필요")
        }
    }

    private fun saveAlarm(requestCode: Int) {
        val alarmSet = getSavedAlarms().toMutableSet()
        alarmSet.add(requestCode)
        prefs.edit().putStringSet("alarm_list", alarmSet.map { it.toString() }.toSet()).apply()
    }

    private fun removeAlarm(requestCode: Int) {
        val alarmSet = getSavedAlarms().toMutableSet()
        alarmSet.remove(requestCode)
        prefs.edit().putStringSet("alarm_list", alarmSet.map { it.toString() }.toSet()).apply()
    }

    private fun getSavedAlarms(): Set<Int> {
        return prefs.getStringSet("alarm_list", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }

    private fun clearAllAlarms() {
        prefs.edit().remove("alarm_list").apply()
    }
}