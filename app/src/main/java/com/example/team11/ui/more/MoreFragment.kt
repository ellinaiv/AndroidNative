package com.example.team11.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.team11.R
import kotlinx.android.synthetic.main.fragment_more.*

class MoreFragment : Fragment() {


    private var aboutAppClicked = false
    private var aboutAPIClicked = false
    private var aboutSettingClicked = false
    private lateinit var viewModel: MoreFragmentViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MoreFragmentViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_more, container, false)

        viewModel.personalPreference!!.observe(viewLifecycleOwner, Observer {personalPreference ->
            switchSetting.isChecked = personalPreference.falseData
        })

        val aboutAppTitle = root.findViewById<TextView>(R.id.textAboutAppTitle)
        val aboutAPITitle = root.findViewById<TextView>(R.id.textAboutAPITitle)
        val textSettingTitle = root.findViewById<TextView>(R.id.textSettingTitle)

        aboutAppTitle.setOnClickListener {
            makeViewsDisappear()
            if (aboutAppClicked) {
                aboutAppClicked = false
            } else {
                aboutAppTitle.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.whiteTextColor))
                textAboutApp.visibility = View.VISIBLE
                aboutAppTitle.background =
                    resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAppClicked = true
                aboutAPIClicked = false
                aboutSettingClicked = false
            }
        }

        aboutAPITitle.setOnClickListener {
            makeViewsDisappear()
            if (aboutAPIClicked) {
                aboutAPIClicked = false
            } else {
                aboutAPITitle.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.whiteTextColor))
                textAboutAPI.visibility = View.VISIBLE
                aboutAPITitle.background =
                    resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAPIClicked = true
                aboutAppClicked = false
                aboutSettingClicked = false
            }
        }

        textSettingTitle.setOnClickListener {
            makeViewsDisappear()
            if(aboutSettingClicked){
                aboutSettingClicked = false
            }else{
                textSettingTitle.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.whiteTextColor))
                textAboutSettings.visibility = View.VISIBLE
                layoutSwitch.visibility = View.VISIBLE
                textSettingTitle.background =
                    resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAPIClicked = false
                aboutAppClicked = false
                aboutSettingClicked = true
            }
        }

        return root
    }

    /**
     * Nullstriller hele activiten (med tanke på viewene).
     */
    private fun makeViewsDisappear(){
        textAboutApp.visibility = View.GONE
        textAboutAPI.visibility = View.GONE
        textAboutSettings.visibility = View.GONE
        layoutSwitch.visibility = View.GONE

        textAboutAppTitle.background = resources.getDrawable(R.drawable.about_rectangle, null)
        textAboutAPITitle.background = resources.getDrawable(R.drawable.about_rectangle, null)
        textSettingTitle.background = resources.getDrawable(R.drawable.about_rectangle, null)

        textAboutAppTitle.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.mainTextColor))
        textAboutAPITitle.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.mainTextColor))
        textSettingTitle.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.mainTextColor))
    }

    override fun onPause() {
        super.onPause()
        viewModel.changeFalseData(switchSetting.isChecked)
    }

}

