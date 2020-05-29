package com.example.team11.uiAndViewModels.filter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.R
import com.example.team11.util.Constants
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_filter.textTempMidWater

class FilterActivity : AppCompatActivity() {
    private val viewModel: FilterActivityViewModel by viewModels{ FilterActivityViewModel.InstanceCreator(applicationContext) }
    private var waterRepresentation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        supportActionBar!!.hide()
        buttonBack.setOnClickListener {
            updateFilter()
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
            if(personalPreferences.isNotEmpty()){
                makeSeekBar(personalPreferences[0])
                waterRepresentation = personalPreferences[0].showBasedOnWater
                if(waterRepresentation){
                    buttonWaterTemp.setImageResource(R.drawable.rep_true)
                    buttonAirTemp.setImageResource(R.drawable.rep_false)
                }else{
                    buttonAirTemp.setImageResource(R.drawable.rep_true)
                    buttonWaterTemp.setImageResource(R.drawable.rep_false)
                }
                makeCheckBoxes(personalPreferences[0])
            }
        })

    }

    /**
     * Lager et nytt objekt som er den nye preferansen til brukeren
     */
    private fun updateFilter(){
        val pp = PersonalPreference(
            waterTempMid = seekBarWater.progress + Constants.waterTempLow,
            airTempMid = seekBarAir.progress + Constants.airTempLow,
            showAirCold = checkBoxColdAir.isChecked,
            showAirWarm = checkBoxWarmAir.isChecked,
            showWaterCold = checkBoxColdWater.isChecked,
            showWaterWarm = checkBoxWarmWater.isChecked,
            showBasedOnWater = waterRepresentation
        )
        viewModel.updatePersonalPreference(pp)
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
        textTempLowWater.text = getString(R.string.temp_C, Constants.waterTempLow)
        textTempHighWater.text = getString(R.string.temp_C, Constants.waterTempHigh)
        seekBarWater.max = Constants.waterTempHigh

        seekBarWater.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                val degreeMid = getString(R.string.temp_C, progress)
                textTempMidWater.text = degreeMid
                textTempMidWater.x = seekBarWater.x + value
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                val progress = seek.progress
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                val degreeMid = getString(R.string.temp_C, progress)
                textTempMidWater.text = degreeMid
                textTempMidWater.x = seekBarWater.x + value
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                //write custom code when done touching
            }
        })


        seekBarAir.progress = personalPreference.airTempMid - Constants.airTempLow
        textTempLowAir.text = getString(R.string.temp_C, Constants.airTempLow)
        textTempHighAir.text = getString(R.string.temp_C, Constants.airTempHigh)
        seekBarAir.max = Constants.airTempHigh - Constants.airTempLow

        seekBarAir.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                val newProgress = progress + Constants.airTempLow
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                val degreeMid = getString(R.string.temp_C, newProgress)
                textTempMidAir.text = degreeMid
                textTempMidAir.x = seekBarAir.x + value
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                val seek = seekBar ?: return
                val progress = seek.progress
                val newProgress = progress + Constants.airTempLow
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                val degreeMid = getString(R.string.temp_C, newProgress)
                textTempMidAir.text = degreeMid
                textTempMidAir.x = seekBarAir.x + value
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //write custom code when done touching
            }

        })
    }

    override fun onPause() {
        super.onPause()
        updateFilter()
    }
}
