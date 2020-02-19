package com.elioms.testfirebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.elioms.testfirebase.model.FirebaseBody
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class FireBaseService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result!!.token
                Log.e("token", token)
            })
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        super.onMessageReceived(remoteMessage)

        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d("firebase", "Message data payload: " + remoteMessage.data)

            // Compose and show notification
            if (!remoteMessage.data.isNullOrEmpty()) {
                val msg: String = remoteMessage.data.get("message").toString()

                val gson = Gson()
                val data = gson.fromJson(msg, FirebaseBody::class.java)


                Log.d("firebase", "Message data payload: " + data.title)
                sendNotification(data)
            }

        }
//
//    remoteMessage?.notification?.let {
//        sendNotification(remoteMessage.notification?.body)
//    }

    }

    private fun sendNotification(messageBody: FirebaseBody) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = "icn"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_android_black)
            .setContentTitle(messageBody.title)
            .setContentText(messageBody.body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}