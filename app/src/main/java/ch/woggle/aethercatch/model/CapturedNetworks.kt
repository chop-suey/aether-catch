package ch.woggle.aethercatch.model

import androidx.room.Entity

@Entity(
    tableName = "capturedNetworks",
    primaryKeys = ["timestamp", "ssid", "bssid"]
)
data class CapturedNetworks(
    val timestamp: Long,
    val ssid: String,
    val bssid: String
)