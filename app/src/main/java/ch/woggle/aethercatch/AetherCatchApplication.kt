package ch.woggle.aethercatch

import android.app.Application
import androidx.room.Room

class AetherCatchApplication : Application() {
    val database: AetherCatchDatabase by lazy {
        Room.databaseBuilder(
            this,
            AetherCatchDatabase::class.java,
            "aethercatch.db"
        ).build()
    }
}