package ch.woggle.aethercatch.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.woggle.aethercatch.model.CaptureReport
import ch.woggle.aethercatch.model.CapturedNetworks
import ch.woggle.aethercatch.model.Network

@Dao
interface CapturedNetworksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(capturedNetworks: List<CapturedNetworks>)

    @Query("SELECT r.* FROM reports r " +
            "JOIN capturedNetworks cn " +
            "ON r.timestamp = cn.timestamp AND cn.ssid = :ssid AND cn.bssid = :bssid")
    fun getReportsByNetwork(ssid: String, bssid: String): List<CaptureReport>

    @Query("SELECT n.* FROM networks n " +
            "JOIN capturedNetworks cn " +
            "ON n.ssid = cn.ssid AND n.bssid = cn.bssid AND cn.timestamp = :timestamp")
    fun getNetworksByReportTimestamp(timestamp: Long): List<Network>
}