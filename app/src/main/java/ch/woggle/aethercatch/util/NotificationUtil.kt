package ch.woggle.aethercatch.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import ch.woggle.aethercatch.R

private val AETHER_CATCH_DEFAULT_NOTIFICATION_CHANNEL_ID = "AETHER_CATCH_DEFAULT_NOTIFICATION_CHANNEL_ID"

fun createDefaultNotificationChannelAndGetId(context: Context): String {
    val name = context.getString(R.string.default_notification_channel_name)
    val notificationChannel = NotificationChannel(
        AETHER_CATCH_DEFAULT_NOTIFICATION_CHANNEL_ID,
        name,
        NotificationManager.IMPORTANCE_DEFAULT
    )
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)
    return AETHER_CATCH_DEFAULT_NOTIFICATION_CHANNEL_ID
}