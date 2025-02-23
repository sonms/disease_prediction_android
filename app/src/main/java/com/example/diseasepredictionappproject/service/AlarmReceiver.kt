package com.example.diseasepredictionappproject.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.diseasepredictionappproject.utils.NotificationHelper


class AlarmReceiver : BroadcastReceiver() {
    private val TAG = "AlarmReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "알림"
        val message = intent.getStringExtra("message") ?: "It's time!"
        //val requestCode = AlarmHelper(context).

        Log.d(TAG, " 알람 수신: Title = $title, Message = $message")

        // 알림 표시
        NotificationHelper(context).showNotification(title, message)

        /*if (requestCode != null) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                val nextAlarmTime = Calendar.getInstance().apply {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarmTime.timeInMillis, pendingIntent)

                nextAlarmTime.add(Calendar.WEEK_OF_YEAR, 1)

                alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarmTime.timeInMillis, pendingIntent)

                Log.d(TAG, "반복 알람 설정 완료 receiver: ${nextAlarmTime.time}, RequestCode: $requestCode")
            } catch (e: SecurityException) {
                Log.e(TAG, "반복 알람 설정 실패: SecurityException 발생", e)
                AlarmHelper(context).requestExactAlarmPermission()
            }
        }*/
    }
}