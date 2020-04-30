package ch.woggle.aethercatch.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.woggle.aethercatch.model.Network

@Dao
interface NetworkDao {
    @Query("SELECT * FROM networks")
    suspend fun getAll(): List<Network>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(networks: List<Network>): List<Long>
}