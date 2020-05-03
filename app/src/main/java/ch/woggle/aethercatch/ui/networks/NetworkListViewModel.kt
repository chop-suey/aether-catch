package ch.woggle.aethercatch.ui.networks

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ch.woggle.aethercatch.AetherCatchApplication
import ch.woggle.aethercatch.model.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "NetworkListViewModel"

class NetworkListViewModel(application: Application) : AndroidViewModel(application) {
    private val networks: MutableLiveData<List<Network>> by lazy {
        MutableLiveData(listOf<Network>()).also {
            initNetworkLoading()
        }
    }

    fun getNetworks(): LiveData<List<Network>> = networks

    private fun initNetworkLoading() {
        viewModelScope.launch(Dispatchers.IO) {
            val networkFlow = getApplication<AetherCatchApplication>().database
                .getNetworkDao()
                .getAll()
            collectNetworks(networkFlow)
        }
    }

    private suspend fun collectNetworks(flow: Flow<List<Network>>) {
        try {
            flow.collect { networks.postValue(it) }
        } catch (e: Throwable) {
            Log.i(TAG, "Error in network collection", e)
        }
    }
}