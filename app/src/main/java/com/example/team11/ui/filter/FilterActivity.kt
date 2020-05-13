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
    private var waterRepresentation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        supportActionBar!!.hide()
        buttonBack.setOnClickListener {
            setFilter()
            finish()
        }

        buttonAirTemp.setOnClickListener {
            if(waterRepresentation){
                waterRepresentation = false
                buttonAirTemp.setImageResource(R.drawable.rep_true)
                buttonWaterTemp.setImageResource(R.drawable.rep_false)
            }
        }

        buttonWaterTemp.setOnClickListener {
            if(! waterRepresentation){
                waterRepresentation = true
                buttonWaterTemp.setImageResource(R.drawable.rep_true)
                buttonAirTemp.setImageResource(R.drawable.rep_false)
            }
        }

        viewModel.personalPreferences!!.observe(this, Observer {personalPreferences ->
            makeSeekBar(personalPreferences)
            waterRepresentation = personalPreferences.showBasedOnWater
            if(waterRepresentation){
                buttonWaterTemp.setImageResource(R.drawable.rep_true)
                buttonAirTemp.setImageResource(R.drawable.rep_false)
            }else{
                buttonAirTemp.setImageResource(R.drawable.rep_true)
                buttonWaterTemp.setImageResource(R.drawable.rep_false)
            }
            makeCheckBoxes(personalPreferences)

        })

    }

    /**
     * Lager et nytt objekt som er den nye preferansen til brukeren
     */
    private fun setFilter(){
        viewModel.updatePersonalPreference(
            PersonalPreference(
                waterTempMid = seekBarWater.progress,
                airTempMid = seekBarAir.progress + viewModel.personalPreferences!!.value!!.airTempLow,
                showAirCold = checkBoxColdAir.isChecked,
                showAirWarm = checkBoxWarmAir.isChecked,
                showWaterCold = checkBoxColdWater.isChecked,
                showWaterWarm = checkBoxWarmWater.isChecked,
                showBasedOnWater = waterRepresentation,
                falseData = viewModel.personalPreferences!!.value!!.falseData
        ))
    }

    /**
     * Setter checkBoxene slik at de samsvarer med de brukerens preferanser
     */
    private fun makeCheckBoxes(personalPreference: PersonalPreference){
        checkBoxColdWater.isChecked = personalPreference.showWaterCold
        checkBoxWarmWater.isChecked = personalPreference.showWaterWarm
        checkBoxColdAir.isChecked = personalPreference.showAirCold
        checkBoxWarmAir.isChecked = personalPreference.showAirWarm
    }

    /**
     * Logikken til seek barene ligger her. Legg merke til at den nedereste må bruke
     * personalPreferance.airTempLow i en del av utreiningene sine det er fordi det ikke finnes
     * no min instansvariabel for seekbar
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

    override fun onPause() {
        super.onPause()
        setFilter()
    }
}
