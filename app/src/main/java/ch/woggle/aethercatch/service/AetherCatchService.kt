package ch.woggle.aethercatch.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import ch.woggle.aethercatch.AetherCatchApplication
import ch.woggle.aethercatch.MainActivity
import ch.woggle.aethercatch.R
import ch.woggle.aethercatch.dao.CaptureReportDao
import ch.woggle.aethercatch.dao.NetworkDao
import ch.woggle.aethercatch.model.CaptureReport
import ch.woggle.aethercatch.model.Network
import ch.woggle.aethercatch.util.createDefaultNotificationChannelAndGetId
import ch.woggle.aethercatch.util.isLocationEnabled
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val TAG = "AetherCatchService"

class AetherCatchService : Service() {
    private lateinit var captureHandler: Handler

    private lateinit var networkDao: NetworkDao
    private lateinit var captureReportDao: CaptureReportDao

    private var isLocationEnabled = false

    private companion object {
        const val SERVICE_FOREGROUND_NOTIFICATION_ID = 4711
        const val SERVICE_CAPTURE_THREAD_NAME = "AetherCatchCaptureThread"
        const val SERVICE_CAPTURE_INTERVAL_MILLIS = 30 * 1000L
    }

    override fun onCreate() {
        super.onCreate()
        val handlerThread = HandlerThread(SERVICE_CAPTURE_THREAD_NAME)
        handlerThread.start()
        captureHandler = Handler(handlerThread.looper)
        val database = (application as AetherCatchApplication).database
        networkDao = database.getNetworkDao()
        captureReportDao = database.getCaptureReportDao()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isLocationEnabled = isLocationEnabled(this)
        startForeground(SERVICE_FOREGROUND_NOTIFICATION_ID, createServiceNotification())
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
        if (isLocationEnabled(this) != isLocationEnabled) {
            isLocationEnabled = !isLocationEnabled
            updateNotification()
        }
        captureNetworks()
        captureHandler.postDelayed({ runCaptureInterval() }, SERVICE_CAPTURE_INTERVAL_MILLIS)
    }

    private fun updateNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(SERVICE_FOREGROUND_NOTIFICATION_ID, createServiceNotification())
    }

    private fun captureNetworks() {
        Log.i(TAG, "Capture networks")
        GlobalScope.launch {
            val networks = getWifiManager().scanResults.map { Network.fromScanResult(it) }
            val rowIds = networkDao.insertAll(networks)
            createCaptureReport(rowIds)
        }
    }

    private suspend fun createCaptureReport(networkRowIds: List<Long>) {
        val count = networkRowIds.filter { it != -1L }.size
        Log.i(TAG, "Captured $count new networks")
        captureReportDao.insert(CaptureReport.forNetworkCount(count))
    }

    private fun createServiceNotification(): Notification {
        val notificationChannelId = createDefaultNotificationChannelAndGetId(this)
        return Notification.Builder(this, notificationChannelId)
            .setContentTitle(getText(R.string.service_notification_title))
            .setContentText(getNotificationText(isLocationEnabled))
            .setContentIntent(createContentIntent())
            .setSmallIcon(getNotificationIcon(isLocationEnabled))
            .build()
    }

    private fun getNotificationText(isLocationEnabled: Boolean): CharSequence {
        return if (isLocationEnabled) {
            getText(R.string.service_notification_text)
        } else {
            getText(R.string.service_notification_text_location_disabled)
        }
    }

    private fun getNotificationIcon(isLocationEnabled: Boolean): Int {
        return if (isLocationEnabled) {
            R.drawable.ic_pacman
        } else {
            R.drawable.ic_confused
        }
    }

    private fun createContentIntent(): PendingIntent {
        val startActivityIntent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, startActivityIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getWifiManager(): WifiManager {
        return getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
}