package io.trewartha.positional.ui.compass

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import io.trewartha.positional.R
import io.trewartha.positional.compass.CompassMode
import kotlinx.android.synthetic.main.compass_fragment.*
import kotlin.math.roundToInt

class CompassFragment : Fragment() {

    private lateinit var viewModel: CompassViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(CompassViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.compass_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // ViewModel data
        viewModel.compassData.observe(viewLifecycleOwner, ::handleCompassData)
    }

    private fun getAccuracyText(accuracy: Int?): String = getString(
            when (accuracy) {
                SensorManager.SENSOR_STATUS_NO_CONTACT -> R.string.compass_accuracy_no_contact
                SensorManager.SENSOR_STATUS_UNRELIABLE -> R.string.compass_accuracy_unreliable
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> R.string.compass_accuracy_low
                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> R.string.compass_accuracy_medium
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> R.string.compass_accuracy_high
                else -> R.string.compass_accuracy_unknown
            }
    )

    private fun handleCompassData(data: CompassViewModel.Data.Compass?) {
        if (data == null) {
            // TODO: Show this as text that replaces the compass visuals instead of using a dialog
            AlertDialog.Builder(requireContext())
                    .setTitle(R.string.compass_sensor_missing_title)
                    .setMessage(R.string.compass_sensor_missing_message)
                    .setPositiveButton(R.string.compass_sensor_missing_button_positive, null)
                    .show()
            return
        }

        data.azimuth?.let { azimuth ->
            backgroundCompassView.rotationUpdate(360f - azimuth, true)
            needleCompassView.rotationUpdate(360f - azimuth, true)
            degreesTextView.text = getString(R.string.compass_degrees, azimuth.roundToInt())

            // If there's an azimuth, we should be showing some accuracies. If they're
            // not present, make sure they're shown as unknown.
            accelerometerAccuracyTextView.text = getAccuracyText(data.accelerometerAccuracy)
            magnetometerAccuracyTextView.text = getAccuracyText(data.magnetometerAccuracy)
        }
        data.mode?.let { mode ->
            modeTextView.text = getString(
                    when (mode) {
                        CompassMode.MAGNETIC_NORTH -> R.string.compass_mode_magnetic_north
                        CompassMode.TRUE_NORTH -> R.string.compass_mode_true_north
                    }
            )
        }
        data.declination?.let {
            declinationTextView.text = getString(R.string.compass_declination, it.roundToInt())
        }
    }
}