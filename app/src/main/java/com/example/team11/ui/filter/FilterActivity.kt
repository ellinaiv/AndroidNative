package com.example.team11.ui.filter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.example.team11.PersonalPreference
import com.example.team11.R
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_filter.textTempMid

class FilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        supportActionBar!!.hide()
        buttonBack.setOnClickListener {
            finish()
        }
        makeSeekBar()

    }

    fun progressCalculation(){
        TODO("Regne om verdien til og fra progress")
    }

    /**
     * Logikken til seek baren ligger her
     */
    private fun makeSeekBar(){
        seekBarWater.progress = PersonalPreference.waterTempMid
        val degreeLow = getString(R.string.tempC, PersonalPreference.waterTempLow)
        val degreeHigh = getString(R.string.tempC, PersonalPreference.waterTempHigh)
        textTempLow.text = degreeLow
        textTempHigh.text = degreeHigh
        seekBarWater.max = PersonalPreference.waterTempHigh

        seekBarWater.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                val degreeMid = getString(R.string.tempC, progress)
                textTempMid.text = degreeMid
                textTempMid.x = seekBarWater.x + value;
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                PersonalPreference.waterTempMid = seek.progress
            }
        })
    }
}
