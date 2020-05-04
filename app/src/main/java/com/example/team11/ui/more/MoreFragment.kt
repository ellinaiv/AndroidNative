package com.example.team11.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.team11.PersonalPreference
import com.example.team11.R
import kotlinx.android.synthetic.main.fragment_more.*

class MoreFragment : Fragment() {


    var aboutAppClicked = false
    var aboutAPIClicked = false
    var settingClicked = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_more, container, false)

        val aboutAppTitle = root.findViewById<TextView>(R.id.aboutAppTitle)
        val aboutAPITitle = root.findViewById<TextView>(R.id.aboutAPITitle)
        val settingsText = root.findViewById<TextView>(R.id.settingsText)

        aboutAppTitle.setOnClickListener {
            makeViewsDisappear()
            if (aboutAppClicked) {
                aboutAppClicked = false
            } else {
                aboutAppText.visibility = View.VISIBLE
                aboutAppView.visibility = View.VISIBLE
                aboutAppTitle.background =
                    resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAppClicked = true;
            }
        }

        aboutAPITitle.setOnClickListener {
            makeViewsDisappear()
            if (aboutAPIClicked) {
                aboutAPIClicked = false
            } else {
                aboutAPIText.visibility = View.VISIBLE
                aboutAPIView.visibility = View.VISIBLE
                aboutAPITitle.background =
                    resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAPIClicked = true
            }
        }

        settingsText.setOnClickListener {
            makeViewsDisappear()
            if (settingClicked) {
                settingClicked = false
            } else {
                seekBarLayout.visibility = View.VISIBLE
                settingsView.visibility = View.VISIBLE
                settingsText.background =
                    resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                settingClicked = true
            }
        }

        return root
    }

    /**
     * Nullstriller hele activiten (med tanke på viewene).
     */
    private fun makeViewsDisappear(){

        aboutAppText.visibility = View.GONE
        aboutAPIText.visibility = View.GONE
        seekBarLayout.visibility = View.GONE

        aboutAppTitle.background = resources.getDrawable(R.drawable.about_rectangle, null)
        aboutAPITitle.background = resources.getDrawable(R.drawable.about_rectangle, null)
        settingsText.background = resources.getDrawable(R.drawable.about_rectangle, null)

        aboutAppView.visibility = View.GONE
        aboutAPIView.visibility = View.GONE
        settingsView.visibility = View.GONE
    }
}

