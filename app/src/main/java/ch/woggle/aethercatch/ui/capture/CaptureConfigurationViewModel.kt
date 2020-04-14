package ch.woggle.aethercatch.ui.capture

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import ch.woggle.aethercatch.service.AetherCatchService

class CaptureConfigurationViewModel : ViewModel() {

    fun startCaptureService(context: Context) {
        context.startForegroundService(getCaptureServiceIntent(context))
    }

    fun stopCaptureService(context: Context) {
        context.stopService(getCaptureServiceIntent(context))
    }

    private fun getCaptureServiceIntent(context: Context): Intent {
        return Intent(context, AetherCatchService::class.java)
    }
}
