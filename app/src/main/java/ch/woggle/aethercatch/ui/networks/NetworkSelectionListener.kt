package ch.woggle.aethercatch.ui.networks

import ch.woggle.aethercatch.model.Network

interface NetworkSelectionListener {
    fun onNetworkSelected(network: Network)
}