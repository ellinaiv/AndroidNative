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
        textTempLow.text = PersonalPreference.waterTempLow.toString()
        textTempHigh.text = PersonalPreference.waterTempHigh.toString()
        seek_bar.max = PersonalPreference.waterTempHigh

        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                progress: Int, fromUser: Boolean) {
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                textTempMid.text = progress.toString() ;
                textTempMid.x = seek_bar.x + value + seek.thumbOffset / 2;
                //textView.setY(100); just added a value set this properly using screen with height aspect ratio , if you do not set it by default it will be there below seek bar

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