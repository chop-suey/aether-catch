package ch.woggle.aethercatch.ui.capture

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import ch.woggle.aethercatch.R
import ch.woggle.aethercatch.util.hasFineLocationPermission
import ch.woggle.aethercatch.util.isLocationEnabled
import com.google.accompanist.themeadapter.appcompat.AppCompatTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class CaptureConfigurationFragment : Fragment() {

    companion object {
        fun newInstance() =
            CaptureConfigurationFragment()
    }

    private val hasLocationPermission = MutableStateFlow(false)
    private val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasLocationPermission.tryEmit(it)
    }

    private val viewModel: CaptureConfigurationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.capture_configuration_fragment, container, false).apply {
            findViewById<ComposeView>(R.id.compose_view).setContent {
                AppCompatTheme {
                    val hasLocationPermissionState = hasLocationPermission.collectAsState()
                    Column {
                        StartCapturingButton(hasLocationPermissionState)
                        StopCapturingButton(hasLocationPermissionState)
                        LatestReportText()
                        SsidStatistics()
                        Last24HoursStatistics()
                        ExportButton()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.exportSuccess.collect {
                val textId = when (it) {
                    true -> R.string.export_success
                    else -> R.string.export_failed
                }
                Toast.makeText(requireContext(), textId, Toast.LENGTH_SHORT).show()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                checkLocationPermission()
            }
        }
    }

    private fun checkLocationPermission() {
        val activity = requireActivity()
        if (hasFineLocationPermission(activity)) {
            if (!isLocationEnabled(activity)) {
                askToEnableLocation()
            }
            hasLocationPermission.tryEmit(true)
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /**
     * TODO:
     *  - nice styling / constraint layout
     */

    @Composable
    private fun StartCapturingButton(enabledState: State<Boolean>) {
        Button(enabled = enabledState.value, onClick = { viewModel.startCaptureService(requireContext()) }) {
            Text(stringResource(R.string.start_capturing))
        }
    }
    
    @Composable
    private fun StopCapturingButton(enabledState: State<Boolean>) {
        Button(enabled = enabledState.value, onClick = { viewModel.stopCaptureService(requireContext()) }) {
            Text(stringResource(R.string.stop_capturing))
        }
    }

    @Composable
    private fun ExportButton() {
        Button(onClick = { viewModel.export() }) {
            Text(stringResource(R.string.export))
        }
    }

    @Composable
    private fun LatestReportText() {
        val latestReport = viewModel.latestSuccessfulReport.collectAsState().value
        Text(
            stringResource(
                R.string.capture_report_indicator,
                latestReport.getDate(),
                latestReport.networkCount
            )
        )
    }

    @Composable
    private fun SsidStatistics() {
        Column {
            val ssids = viewModel.ssids.collectAsState(0)
            val distinctSsids = viewModel.distinctSsids.collectAsState(0)
            Text(stringResource(R.string.ssid_count, ssids.value))
            Text(stringResource(R.string.distinct_ssid_count, distinctSsids.value))
        }
    }

    @Composable
    private fun Last24HoursStatistics() {
        Column {
            val scans = viewModel.scans24h.collectAsState(0)
            val successfulScans = viewModel.succesfulScans24h.collectAsState(0)
            Text(stringResource(R.string.last_24_hours_successful_scans, scans.value, successfulScans.value))
            val ssids = viewModel.ssids24h.collectAsState(0)
            val distinctSsids = viewModel.distinctSsids24h.collectAsState(0)
            Text(stringResource(R.string.last_24_hours_ssids, ssids.value, distinctSsids.value))
        }
    }

    private fun askToEnableLocation() {
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setMessage(R.string.please_enable_location)
            .setPositiveButton(R.string.please_enable_location_ok) { dialog, _ -> dialog.cancel() }
            .show()
    }
}
