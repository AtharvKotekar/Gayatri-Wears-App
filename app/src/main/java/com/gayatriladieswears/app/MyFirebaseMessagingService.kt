package com.gayatriladieswears.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.gayatriladieswears.app.Activities.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_channel"
const val channelName = "com.gayatriladieswears.app"
const val serverKey = "AAAA0KfKwi4:APA91bH6oLe8JO84VrTctsyfhaPOjwRcdaxgf0cjRjJgN4glq3bHc6eMIG-JRrWKhBTn-6JO35IPOnIXzFhV5Ytxh5OJc58Cl-TApY_pjtcvpSqWdW9L7s2cli_RjlPH_3TPA1_bmZdI"

class MyFirebaseMessagingService: FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null){
            genrateNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)
        }
    }

    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews("com.gayatriladieswears.app",R.layout.notification)

        remoteViews.setTextViewText(R.id.notification_title,title)
        remoteViews.setTextViewText(R.id.notification_message,message)
        remoteViews.setImageViewResource(R.id.app_logo,R.mipmap.ic_launcher)

        return remoteViews
    }

    fun genrateNotification(title:String,message:String){
        val intent = Intent(this,HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        var builder:NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)


        builder = builder.setContent(getRemoteView(title,message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH).apply {
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0,builder.build())
    }


}