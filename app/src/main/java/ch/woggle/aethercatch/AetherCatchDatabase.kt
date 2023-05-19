package ch.woggle.aethercatch

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import ch.woggle.aethercatch.dao.CaptureReportDao
import ch.woggle.aethercatch.dao.CapturedNetworksDao
import ch.woggle.aethercatch.dao.NetworkDao
import ch.woggle.aethercatch.model.CaptureReport
import ch.woggle.aethercatch.model.CapturedNetworks
import ch.woggle.aethercatch.model.Network

@Database(
    version = 2,
    entities = [Network::class, CaptureReport::class, CapturedNetworks::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AetherCatchDatabase : RoomDatabase() {
    abstract fun getNetworkDao(): NetworkDao

    abstract fun getCaptureReportDao(): CaptureReportDao

    abstract fun getCapturedNetworksDao(): CapturedNetworksDao
}