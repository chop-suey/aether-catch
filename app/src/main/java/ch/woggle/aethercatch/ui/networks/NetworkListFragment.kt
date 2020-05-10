package ch.woggle.aethercatch.ui.networks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.woggle.aethercatch.R

class NetworkListFragment : Fragment() {

    companion object {
        fun newInstance() = NetworkListFragment()
    }

    private val networkListItemAdapter = NetworkListRecyclerViewAdapter()

    private lateinit var networkListView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        val viewModel: NetworkListViewModel by viewModels()
        viewModel.getNetworks()
            .observe(viewLifecycleOwner, Observer { networkListItemAdapter.setNetworks(it) })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NetworkSelectionListener) {
            networkListItemAdapter.setNetworkSelectionListener(context)
        }
    }

    override fun onDetach() {
        super.onDetach()
        networkListItemAdapter.setNetworkSelectionListener(null)
    }
}
