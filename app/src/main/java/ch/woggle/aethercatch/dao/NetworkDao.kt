package ch.woggle.aethercatch.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.woggle.aethercatch.model.Network
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkDao {
    @Query("SELECT * FROM networks")
    fun getAll(): Flow<List<Network>>

    @Query("SELECT count(ssid) FROM networks")
    fun getSsidCount(): Flow<Int>

    @Query("SELECT count(DISTINCT ssid) FROM networks")
    fun getDistinctSsidCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(networks: List<Network>): List<Long>
}