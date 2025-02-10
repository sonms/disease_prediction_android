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

        Log.d(TAG, " 알람 수신: Title = $title, Message = $message")

        // 알림 표시
        NotificationHelper(context).showNotification(title, message)
    }
}