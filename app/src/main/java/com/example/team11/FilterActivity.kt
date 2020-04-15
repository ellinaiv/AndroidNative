package com.example.team11

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_filter.*


class FilterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        seek_bar.progress = PersonalPreference.waterTempMid
        val degreeLow = "${PersonalPreference.waterTempLow.toString()} ${getString(R.string.degreeCelsius)}"
        val degreeHigh = "${PersonalPreference.waterTempHigh.toString()} ${getString(R.string.degreeCelsius)}"
        textTempLow.text = degreeLow
        textTempHigh.text = degreeHigh
        seek_bar.max = PersonalPreference.waterTempHigh

        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                progress: Int, fromUser: Boolean) {
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                val degreeMid = "$progress ${getString(R.string.degreeCelsius)}"
                textTempMid.text = degreeMid
                textTempMid.x = seek_bar.x + value;
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                PersonalPreference.waterTempMid = seek.progress
            }
        })
    }
    fun progressCalculation(){
        TODO("Regne om verdien til og fra progress")
    }

}