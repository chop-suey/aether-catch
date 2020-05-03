package ch.woggle.aethercatch.ui.capture

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import ch.woggle.aethercatch.AetherCatchApplication
import ch.woggle.aethercatch.model.CaptureReport
import ch.woggle.aethercatch.service.AetherCatchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private const val TAG = "CaptureConfigurationViewModel"

class CaptureConfigurationViewModel(application: Application) : AndroidViewModel(application) {
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

    private fun getCaptureServiceIntent(context: Context): Intent {
        return Intent(context, AetherCatchService::class.java)
    }

    private fun initReportLoading() {
        viewModelScope.launch(Dispatchers.IO) {
            val reportDao = getApplication<AetherCatchApplication>().database.getCaptureReportDao()
            hookupReportLiveData(reportDao.getLatestSuccessfull())
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
