package ch.woggle.aethercatch.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.woggle.aethercatch.model.CaptureReport
import kotlinx.coroutines.flow.Flow

@Dao
interface CaptureReportDao {
    @Query("SELECT * FROM reports ORDER BY timestamp DESC LIMIT 1")
    fun getLatest(): Flow<CaptureReport?>

    @Query("SELECT * FROM reports WHERE networkCount > 0 ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSuccessful(): Flow<CaptureReport?>

    @Query("SELECT count(*) FROM reports WHERE timestamp > ((strftime('%s', CURRENT_TIMESTAMP)  - 24 * 3600) * 1000)")
    fun getReportsCountLast24h(): Flow<Int>

    @Query("SELECT count(*) FROM reports WHERE networkCount > 0 AND timestamp > ((strftime('%s', CURRENT_TIMESTAMP)  - 24 * 3600) * 1000)")
    fun getSuccessfulReportsCountLast24h(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: CaptureReport)
}