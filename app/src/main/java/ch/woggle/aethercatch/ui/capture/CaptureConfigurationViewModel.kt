package ch.woggle.aethercatch.ui.capture

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.woggle.aethercatch.AetherCatchApplication
import ch.woggle.aethercatch.model.CaptureReport
import ch.woggle.aethercatch.service.AetherCatchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

const val TAG = "CaptureConfigurationViewModel"

class CaptureConfigurationViewModel : ViewModel() {
    private val reportMutableLiveData = MutableLiveData<CaptureReport>(CaptureReport.EMPTY)

    fun getLatestReport(): LiveData<CaptureReport> = reportMutableLiveData

    fun startCaptureService(context: Context) {
        context.startForegroundService(getCaptureServiceIntent(context))
    }

    fun stopCaptureService(context: Context) {
        context.stopService(getCaptureServiceIntent(context))
    }

    private fun getCaptureServiceIntent(context: Context): Intent {
        return Intent(context, AetherCatchService::class.java)
    }

    fun init(application: AetherCatchApplication) {
        viewModelScope.launch(Dispatchers.IO) {
            val reportDao = application.database.getCaptureReportDao()
            hookupReportLiveData(reportDao.getLatest())
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
