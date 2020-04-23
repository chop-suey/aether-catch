package ch.woggle.aethercatch.model

import android.net.wifi.ScanResult
import androidx.room.Entity

@Entity(
    tableName = "networks",
    primaryKeys = arrayOf("ssid", "bssid")
)
data class Network(
    val ssid: String,
    val bssid: String,
    val capabilities: String
) {
    companion object {
        fun fromScanResult(scanResult: ScanResult) = Network(
            scanResult.SSID,
            scanResult.BSSID,
            scanResult.capabilities
        )
    }
}