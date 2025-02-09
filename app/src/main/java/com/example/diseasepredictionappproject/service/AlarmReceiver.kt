package com.example.diseasepredictionappproject.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.diseasepredictionappproject.utils.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "알림"
        val message = intent.getStringExtra("message") ?: "It's time!"

        // 알림 표시
        NotificationHelper(context).showNotification(title, message)
    }
}