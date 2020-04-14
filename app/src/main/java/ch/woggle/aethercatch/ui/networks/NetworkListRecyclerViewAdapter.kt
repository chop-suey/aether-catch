package ch.woggle.aethercatch.ui.networks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.woggle.aethercatch.R
import ch.woggle.aethercatch.model.Network
import kotlinx.android.synthetic.main.network_list_fragment_item.view.*

class NetworkListRecyclerViewAdapter() : RecyclerView.Adapter<NetworkListRecyclerViewAdapter.ViewHolder>() {
    private val networks = mutableListOf<Network>()

    private var networkSelectionListener: NetworkSelectionListener? = null

    private val itemClickListener = View.OnClickListener { v ->
        networkSelectionListener?.onNetworkSelected(v.tag as Network)
    }

    fun setNetworks(networks: List<Network>) {
        this.networks.clear()
        this.networks.addAll(networks)
    }

    fun setNetworkSelectionListener(networkSelectionListener: NetworkSelectionListener?) {
        this.networkSelectionListener = networkSelectionListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.network_list_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = networks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val network = networks[position]
        holder.idView.text = network.ssid
        holder.contentView.text = network.bssid

        with(holder.view) {
            tag = network
            setOnClickListener(itemClickListener)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.item_number
        val contentView: TextView = view.content
    }
}