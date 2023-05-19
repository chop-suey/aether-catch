package ch.woggle.aethercatch.service

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import ch.woggle.aethercatch.AetherCatchApplication
import ch.woggle.aethercatch.MainActivity
import ch.woggle.aethercatch.R
import ch.woggle.aethercatch.dao.CaptureReportDao
import ch.woggle.aethercatch.dao.CapturedNetworksDao
import ch.woggle.aethercatch.dao.NetworkDao
import ch.woggle.aethercatch.model.CaptureReport
import ch.woggle.aethercatch.model.CapturedNetworks
import ch.woggle.aethercatch.model.Network
import ch.woggle.aethercatch.util.createDefaultNotificationChannelAndGetId
import ch.woggle.aethercatch.util.isLocationEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TAG = "AetherCatchService"

class AetherCatchService : Service() {
    private lateinit var captureHandler: Handler

    private lateinit var networkDao: NetworkDao
    private lateinit var captureReportDao: CaptureReportDao
    private lateinit var capturedNetworksDao: CapturedNetworksDao

    private var isLocationEnabled = false

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            captureNetworks()
        }
    }

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
        capturedNetworksDao = database.getCapturedNetworksDao()
        registerScanReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isLocationEnabled = isLocationEnabled(this)
        startForeground(SERVICE_FOREGROUND_NOTIFICATION_ID, createServiceNotification())
        captureHandler.post { runCaptureInterval() }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        unregisterReceiver(wifiScanReceiver)
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
        if (getWifiManager().startScan()) {
            Log.i(TAG, "Scan started")
        } else {
            Log.w(TAG, "Failed to start scan")
        }
        captureHandler.postDelayed({ runCaptureInterval() }, SERVICE_CAPTURE_INTERVAL_MILLIS)
    }

    private fun updateNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(SERVICE_FOREGROUND_NOTIFICATION_ID, createServiceNotification())
    }

    private fun registerScanReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)
    }

    private fun captureNetworks() {
        Log.i(TAG, "Capture networks")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            CoroutineScope(Dispatchers.Default).launch {
                val networks = getWifiManager().scanResults.map { Network.fromScanResult(it) }
                persistNetworks(networks)
            }
        }
    }

    private suspend fun persistNetworks(networks: List<Network>) {
        val networkRowIds = networkDao.insertAll(networks)
        val report = createCaptureReport(networkRowIds)
        if (report.networkCount > 0) {
            val capturedNetworks = networks.map { CapturedNetworks(report.timestamp, it.ssid, it.bssid) }
            capturedNetworksDao.insertAll(capturedNetworks)
        }
    }

    private suspend fun createCaptureReport(networkRowIds: List<Long>): CaptureReport {
        val count = networkRowIds.filter { it != -1L }.size
        Log.i(TAG, "Captured $count new networks")
        return CaptureReport.forNetworkCount(count).also {
            captureReportDao.insert(it)
        }
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