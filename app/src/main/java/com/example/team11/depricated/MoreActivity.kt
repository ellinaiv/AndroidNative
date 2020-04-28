package com.example.team11.depricated

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.team11.R
import kotlinx.android.synthetic.main.fragment_more.*


class MoreActivity : AppCompatActivity() {
    private var aboutAppClicked = false
    private var aboutAPIClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)

        textAboutAppTitle.setOnClickListener {
            makeViewsDisappear()
            if (aboutAppClicked) {
                aboutAppClicked = false
            } else {
                textAboutApp.visibility = View.VISIBLE
                textAboutAppTitle.background = resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAppClicked = true
            }
        }

        textAboutAPITitle.setOnClickListener {
            makeViewsDisappear()
            if (aboutAPIClicked) {
                aboutAPIClicked = false
            }else{
                textAboutAPI.visibility = View.VISIBLE
                textAboutAPITitle.background = resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAPIClicked = true
            }
        }
    }

    /**
     * Nullstriller hele activiten (med tanke på viewene).
     */
    private fun makeViewsDisappear(){
        textAboutApp.visibility = View.GONE
        textAboutAPI.visibility = View.GONE

        textAboutAppTitle.background = resources.getDrawable(R.drawable.about_rectangle, null)
        textAboutAPITitle.background = resources.getDrawable(R.drawable.about_rectangle, null)


    }


}