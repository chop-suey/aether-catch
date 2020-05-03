package ch.woggle.aethercatch.dao

import android.graphics.Paint
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
    fun getLatestSuccessfull(): Flow<CaptureReport?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: CaptureReport)
}