package ch.woggle.aethercatch.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import ch.woggle.aethercatch.R
import ch.woggle.aethercatch.model.Network
import ch.woggle.aethercatch.util.createDefaultNotificationChannelAndGetId

class AetherCatchService : Service() {
    private lateinit var captureHandler: Handler

    private companion object {
        const val SERVICE_FOREGROUND_NOTIFICIATION_ID = 4711
        const val SERVICE_CAPTURE_THREAD_NAME = "AetherCatchCaptureThread"
        const val SERVICE_CAPTURE_INTERVAL_MILLIS = 30 * 1000L
    }

    override fun onCreate() {
        super.onCreate()
        val handlerThread = HandlerThread(SERVICE_CAPTURE_THREAD_NAME)
        handlerThread.start()
        captureHandler = Handler(handlerThread.looper)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(SERVICE_FOREGROUND_NOTIFICIATION_ID, createServiceNotification())
        captureHandler.post { runCaptureInterval() }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        captureHandler.looper.quitSafely()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun runCaptureInterval() {
        Log.i("blubb", "runCaptureInterval")
        captureNetworks()
        captureHandler.postDelayed({ runCaptureInterval() }, SERVICE_CAPTURE_INTERVAL_MILLIS)
    }

    private fun captureNetworks() {
        val networks = getWifiManager().scanResults.map { Network(it.SSID, it.BSSID) }
        Log.i("blubb", networks.joinToString())
    }

    private fun createServiceNotification(): Notification {
        val  notificationChannelId = createDefaultNotificationChannelAndGetId(this)
        return Notification.Builder(this, notificationChannelId)
            .setContentTitle(getText(R.string.service_notification_title))
            .setContentText(getText(R.string.service_notification_text))
            .setSmallIcon(R.drawable.ic_pacman)
            .build()
    }

    private fun getWifiManager(): WifiManager {
        return getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
}