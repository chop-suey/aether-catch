package ch.woggle.aethercatch.ui.capture

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ch.woggle.aethercatch.R
import ch.woggle.aethercatch.model.CaptureReport


class CaptureConfigurationFragment : Fragment() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 1234

        fun newInstance() =
            CaptureConfigurationFragment()
    }

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var reportText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.capture_configuration_fragment, container, false)
        startButton = view.findViewById(R.id.button_start_capturing)
        stopButton = view.findViewById(R.id.button_stop_capturing)
        reportText = view.findViewById(R.id.report_text)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProvider(this).get(CaptureConfigurationViewModel::class.java)
        startButton.setOnClickListener { viewModel.startCaptureService(requireContext()) }
        stopButton.setOnClickListener { viewModel.stopCaptureService(requireContext()) }
        viewModel.getLatestReport().observe(viewLifecycleOwner, Observer { setLatestReport(it) })
    }

    override fun onResume() {
        super.onResume()

        // TODO location must be enabled

        if (hasFineLocationPermission()) {
            setButtonsEnabled(true)
        } else {
            setButtonsEnabled(false)
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val index = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)
            setButtonsEnabled(index >= 0 && grantResults[index] == PackageManager.PERMISSION_GRANTED)
        }
    }

    private fun setLatestReport(report: CaptureReport) {
        if (report.networkCount > 0) {
            reportText.text =
                getString(R.string.capture_report_indicator, report.getDate(), report.networkCount)
        }
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        startButton.isEnabled = enabled
        stopButton.isEnabled = enabled
    }

    private fun hasFineLocationPermission() = requireActivity()
        .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

}
