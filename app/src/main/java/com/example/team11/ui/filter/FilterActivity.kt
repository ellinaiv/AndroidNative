package com.example.team11.ui.filter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.team11.PersonalPreference
import com.example.team11.R
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_filter.textTempMidWater

class FilterActivity : AppCompatActivity() {
    private val viewModel: FilterActivityViewModel by viewModels{ FilterActivityViewModel.InstanceCreator() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        supportActionBar!!.hide()
        buttonBack.setOnClickListener {
            finish()
        }

        buttonReset.setOnClickListener {
            viewModel.resetPersonalPreference()
        }

        buttonFilter.setOnClickListener {
            setFilter()
            finish()
        }

        viewModel.personalPreferences!!.observe(this, Observer {personalPreferences ->
            makeSeekBar(personalPreferences)
            switchRepresentation.isChecked = personalPreferences.showBasedOnWater
            makeCheckBoxes(personalPreferences)

        })

    }

    private fun setFilter(){
        viewModel.updatePersonalPreference(
            PersonalPreference(
                waterTempMid = seekBarWater.progress,
                airTempMid = seekBarAir.progress + viewModel.personalPreferences!!.value!!.airTempLow,
                showAirCold = checkBoxColdAir.isChecked,
                showAirWarm = checkBoxWarmAir.isChecked,
                showWaterCold = checkBoxColdWater.isChecked,
                showWaterWarm = checkBoxWarmWater.isChecked,
                showBasedOnWater = switchRepresentation.isChecked
        ))
    }

    private fun makeCheckBoxes(personalPreference: PersonalPreference){
        checkBoxColdWater.isChecked = personalPreference.showWaterCold
        checkBoxWarmWater.isChecked = personalPreference.showWaterWarm
        checkBoxColdAir.isChecked = personalPreference.showAirCold
        checkBoxWarmAir.isChecked = personalPreference.showAirWarm
    }

    /**
     * Logikken til seek baren ligger her
     */
    private fun makeSeekBar(personalPreference: PersonalPreference){
        seekBarWater.progress = personalPreference.waterTempMid
        textTempLowWater.text = getString(R.string.tempC, personalPreference.waterTempLow)
        textTempHighWater.text = getString(R.string.tempC, personalPreference.waterTempHigh)
        seekBarWater.max = personalPreference.waterTempHigh

        seekBarWater.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                val degreeMid = getString(R.string.tempC, progress)
                textTempMidWater.text = degreeMid
                textTempMidWater.x = seekBarWater.x + value
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                //write custom code when done touching
            }
        })


        seekBarAir.progress = personalPreference.airTempMid - personalPreference.airTempLow
        textTempLowAir.text = getString(R.string.tempC, personalPreference.airTempLow)
        textTempHighAir.text = getString(R.string.tempC, personalPreference.airTempHigh)
        seekBarAir.max = personalPreference.airTempHigh - personalPreference.airTempLow

        seekBarAir.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                val newProgress = progress + personalPreference.airTempLow
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                val degreeMid = getString(R.string.tempC, newProgress)
                textTempMidAir.text = degreeMid
                textTempMidAir.x = seekBarAir.x + value
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //write custom code when done touching
            }

        })
    }
}
