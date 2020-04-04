package ch.woggle.aethercatch.ui.networks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.woggle.aethercatch.model.Network

// TODO remove mocked date and store somewhere
private val dummyNetworks = (1..30).map { Network("Network-${it}", "Detail of Network-${it}") }

class NetworkListViewModel : ViewModel() {
    private val networks = MutableLiveData<List<Network>>(dummyNetworks)

    fun getNetworks(): LiveData<List<Network>> = networks
}