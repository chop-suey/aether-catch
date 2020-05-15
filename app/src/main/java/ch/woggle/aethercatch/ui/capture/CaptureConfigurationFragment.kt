package ch.woggle.aethercatch.ui.capture

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import ch.woggle.aethercatch.R
import ch.woggle.aethercatch.model.CaptureReport
import ch.woggle.aethercatch.util.hasFineLocationPermission
import ch.woggle.aethercatch.util.isLocationEnabled


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
        val viewModel: CaptureConfigurationViewModel by viewModels()
        startButton.setOnClickListener { viewModel.startCaptureService(requireContext()) }
        stopButton.setOnClickListener { viewModel.stopCaptureService(requireContext()) }
        viewModel.getLatestReport().observe(viewLifecycleOwner, Observer { setLatestReport(it) })
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity()
        if (hasFineLocationPermission(activity)) {
            if (!isLocationEnabled(activity)) {
                askToEnableLocation()
            }
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

    private fun askToEnableLocation() {
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setMessage(R.string.please_enable_location)
            .setPositiveButton(R.string.please_enable_location_ok) { dialog, _ -> dialog.cancel() }
            .show()
    }
}
