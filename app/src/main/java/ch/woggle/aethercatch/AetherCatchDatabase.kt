package ch.woggle.aethercatch

import androidx.room.Database
import androidx.room.RoomDatabase
import ch.woggle.aethercatch.dao.CaptureReportDao
import ch.woggle.aethercatch.dao.NetworkDao
import ch.woggle.aethercatch.model.CaptureReport
import ch.woggle.aethercatch.model.Network

@Database(
    version = 1,
    entities = [Network::class, CaptureReport::class]
)
abstract class AetherCatchDatabase : RoomDatabase() {
    abstract fun getNetworkDao(): NetworkDao

    abstract fun getCaptureReportDao(): CaptureReportDao
}