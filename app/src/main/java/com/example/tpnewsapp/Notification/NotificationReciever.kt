package com.example.tpnewsapp.Notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tpnewsapp.NewsCategories.MainActivity
import com.example.tpnewsapp.R


private val CHANNEL_ID = "channel_id"
private val notificationId = 101

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager: NotificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context,
                notificationId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT)

        val bitmap =
            BitmapFactory.decodeResource(context.resources,
                R.drawable.logo
            )
        val bitmapLargeIcon =
            BitmapFactory.decodeResource(context.resources,
                R.drawable.ic_launcher_foreground
            )

        val builder = NotificationCompat.Builder(context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.news_header)
            .setContentTitle("TPNEWS ALERT!!!")
            .setContentText("Tap to View The Latest News")
            .setLargeIcon(bitmapLargeIcon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)){
            notify(notificationId, builder.build())
        }
    }

}

