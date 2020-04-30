package ch.woggle.aethercatch.ui.networks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.woggle.aethercatch.AetherCatchApplication
import ch.woggle.aethercatch.model.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkListViewModel : ViewModel() {
    private val networks = MutableLiveData<List<Network>>()

    fun getNetworks(): LiveData<List<Network>> = networks

    fun loadNetworks(application: AetherCatchApplication) {
        viewModelScope.launch(Dispatchers.IO) {
            val persistedNetworks = application.database
                .getNetworkDao()
                .getAll()
            networks.postValue(persistedNetworks)
        }
    }
}