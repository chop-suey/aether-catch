package ch.woggle.aethercatch.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reports")
data class CaptureReport(
    @PrimaryKey val timestamp: Long,
    val networkCount: Int
) {
    companion object {
        val EMPTY = CaptureReport(0, 0)

        fun forNetworkCount(count: Int): CaptureReport {
            return CaptureReport(Date().time, count)
        }
    }

    fun getDate() = Date(timestamp)
}