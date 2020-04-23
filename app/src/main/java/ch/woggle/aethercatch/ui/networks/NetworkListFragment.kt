package ch.woggle.aethercatch.ui.networks

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.woggle.aethercatch.AetherCatchApplication
import ch.woggle.aethercatch.R

class NetworkListFragment : Fragment() {

    companion object {
        fun newInstance() = NetworkListFragment()
    }

    private val networkListItemAdapter = NetworkListRecyclerViewAdapter()

    private lateinit var viewModel: NetworkListViewModel
    private lateinit var networkListView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.network_list_fragment, container, false)

        if (view is RecyclerView) {
            networkListView = view
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = networkListItemAdapter
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NetworkListViewModel::class.java)
        viewModel.getNetworks().observe(viewLifecycleOwner, Observer { networkListItemAdapter.setNetworks(it) })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NetworkSelectionListener) {
            networkListItemAdapter.setNetworkSelectionListener(context)
        }
    }

    override fun onStart() {
        super.onStart()
        val application = requireActivity().application as AetherCatchApplication
        viewModel.loadNetworks(application)
    }

    override fun onDetach() {
        super.onDetach()
        networkListItemAdapter.setNetworkSelectionListener(null)
    }
}
