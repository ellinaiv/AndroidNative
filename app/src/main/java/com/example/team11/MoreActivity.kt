package com.example.team11

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_filter.*


class MoreActivity : AppCompatActivity() {
    var aboutAppCliked = false
    var aboutAPIClicked = false
    var settingCliked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        makeSeekBar()

        aboutAppTitle.setOnClickListener {
            makeViewsDisappear()
            if (aboutAppCliked) {
                aboutAppCliked = false
            } else {
                aboutAppText.visibility = View.VISIBLE
                aboutAppView.visibility = View.VISIBLE
                aboutAppTitle.background = resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAppCliked = true;
            }
        }

        aboutAPITitle.setOnClickListener {
            Log.d("TAG", "HEI")
            makeViewsDisappear()
            if (aboutAPIClicked) {
                aboutAPIClicked = false
            }else{
                aboutAPIText.visibility = View.VISIBLE
                aboutAPIView.visibility = View.VISIBLE
                aboutAPITitle.background = resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAPIClicked = true
            }
        }

        settingsText.setOnClickListener {
            makeViewsDisappear()
            if (settingCliked) {
                settingCliked = false
            }else{
                seekBarLayout.visibility = View.VISIBLE
                settingsText.background = resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                settingCliked = true
            }
        }
    }

    private fun makeViewsDisappear(){
        Log.d("TAG", "FORSVINNER")
        aboutAppText.visibility = View.GONE
        aboutAPIText.visibility = View.GONE
        seekBarLayout.visibility = View.GONE

        aboutAppTitle.background = resources.getDrawable(R.drawable.about_rectangle, null)
        aboutAPITitle.background = resources.getDrawable(R.drawable.about_rectangle, null)
        settingsText.background = resources.getDrawable(R.drawable.about_rectangle, null)

        aboutAppView.visibility = View.GONE
        aboutAPIView.visibility = View.GONE
    }


    fun progressCalculation(){
        TODO("Regne om verdien til og fra progress")
    }

    private fun makeSeekBar(){
        seek_bar.progress = PersonalPreference.waterTempMid
        val degreeLow = getString(R.string.tempC, PersonalPreference.waterTempLow)
        val degreeHigh = getString(R.string.tempC, PersonalPreference.waterTempHigh)
        textTempLow.text = degreeLow
        textTempHigh.text = degreeHigh
        seek_bar.max = PersonalPreference.waterTempHigh

        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                val value = (progress * (seek.width - 2 * seek.thumbOffset)) / seek.max
                val degreeMid = getString(R.string.tempC, progress)
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

}