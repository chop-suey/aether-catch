package ch.woggle.aethercatch.ui.capture

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import ch.woggle.aethercatch.R


class CaptureConfigurationFragment : Fragment() {

    companion object {
        fun newInstance() =
            CaptureConfigurationFragment()
    }

    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.capture_configuration_fragment, container, false)
        startButton = view.findViewById(R.id.button_start_capturing)
        stopButton = view.findViewById(R.id.button_stop_capturing)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProvider(this).get(CaptureConfigurationViewModel::class.java)
        startButton.setOnClickListener { viewModel.startCaptureService(requireContext()) }
        stopButton.setOnClickListener { viewModel.stopCaptureService(requireContext()) }
    }
}
