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

    private val reportMutableLiveData: MutableLiveData<CaptureReport> by lazy {
        MutableLiveData(CaptureReport.EMPTY).also {
            initReportLoading()
        }
    }

    fun getLatestReport(): LiveData<CaptureReport> = reportMutableLiveData

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

    private fun initReportLoading() {
        viewModelScope.launch(Dispatchers.IO) {
            val reportDao = database.getCaptureReportDao()
            hookupReportLiveData(reportDao.getLatestSuccessful())
        }
    }

    private suspend fun hookupReportLiveData(reportFlow: Flow<CaptureReport?>) {
        try {
            reportFlow
                .filter { it != null }
                .collect { reportMutableLiveData.postValue(it) }
        } catch (e: Throwable) {
            Log.i(TAG, "Error in capture report", e)
        }
    }
}
