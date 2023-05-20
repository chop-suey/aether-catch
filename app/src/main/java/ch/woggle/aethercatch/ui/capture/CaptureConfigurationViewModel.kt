package ch.woggle.aethercatch.ui.capture

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import ch.woggle.aethercatch.AetherCatchApplication
import ch.woggle.aethercatch.model.CaptureReport
import ch.woggle.aethercatch.service.AetherCatchService
import ch.woggle.aethercatch.util.exportToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "CaptureConfigurationViewModel"

class CaptureConfigurationViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getApplication<AetherCatchApplication>().database

    private val mutableExportSuccess = MutableSharedFlow<Boolean>(replay = 0)
    val exportSuccess = mutableExportSuccess.asSharedFlow()

    val ssids = database.getNetworkDao().getSsidCount()
    val distinctSsids = database.getNetworkDao().getDistinctSsidCount()

    val scans24h = database.getCaptureReportDao().getReportsCountLast24h()
    val succesfulScans24h = database.getCaptureReportDao().getSuccessfulReportsCountLast24h()
    val ssids24h = database.getCapturedNetworksDao().getSsidCountLast24h()
    val distinctSsids24h = database.getCapturedNetworksDao().getDistinctSsidCountLast24h()

    val latestSuccessfulReport = MutableStateFlow(CaptureReport.EMPTY).also { flow ->
        viewModelScope.launch(Dispatchers.IO) {
            try {
                database.getCaptureReportDao()
                    .getLatestSuccessful()
                    .filter { it != null }
                    .collect { flow.emit(it!!) }
            } catch (e: Throwable) {
                Log.w(TAG, "Error in capture report", e)
            }
        }
    }.asStateFlow()

    fun startCaptureService(context: Context) {
        context.startForegroundService(getCaptureServiceIntent(context))
    }

    fun stopCaptureService(context: Context) {
        context.stopService(getCaptureServiceIntent(context))
    }

    fun export() {
        viewModelScope.launch(Dispatchers.IO) {
            val networksDao = database.getNetworkDao()
            networksDao.getAll().take(1).collect {
                try {
                    exportToFile(getApplication(), it)
                    mutableExportSuccess.emit(true)
                } catch (exception: Exception) {
                    mutableExportSuccess.emit(false)
                }
            }
        }
    }

    private fun getCaptureServiceIntent(context: Context): Intent {
        return Intent(context, AetherCatchService::class.java)
    }
}
