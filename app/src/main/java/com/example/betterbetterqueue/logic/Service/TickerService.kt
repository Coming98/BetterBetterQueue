package com.example.betterbetterqueue.logic.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.betterbetterqueue.MainActivity
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.ui.TodoItemInfo.TodoItemInfoActivity

class TickerService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("TEMP", "onCreate executed")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("ticker", "BetterBetterQueueTicker", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, TodoItemInfoActivity::class.java)
        // val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, "ticker")
            .setContentTitle("时光滴答...")
            .setContentText("This is content text")
            .setSmallIcon(R.drawable.ic_item_totaltime)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_start))
            // .setContentIntent(pi)
            .build()
        // 让 MyService 变成一个前台 Service，并在系统状态栏显示出来
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TEMP", "onStartCommand executed")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TEMP", "onDestroy executed")
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}