package com.example.team11.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.team11.R
import kotlinx.android.synthetic.main.fragment_more.*

class MoreFragment : Fragment() {


    var aboutAppClicked = false
    var aboutAPIClicked = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_more, container, false)

        val aboutAppTitle = root.findViewById<TextView>(R.id.textAboutAppTitle)
        val aboutAPITitle = root.findViewById<TextView>(R.id.textAboutAPITitle)

        aboutAppTitle.setOnClickListener {
            makeViewsDisappear()
            if (aboutAppClicked) {
                aboutAppClicked = false
            } else {
                aboutAppTitle.setTextColor(ContextCompat.getColor(this.context!!, R.color.whiteTextColor))
                textAboutApp.visibility = View.VISIBLE
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
                aboutAPITitle.setTextColor(ContextCompat.getColor(this.context!!, R.color.whiteTextColor))
                textAboutAPI.visibility = View.VISIBLE
                aboutAPITitle.background =
                    resources.getDrawable(R.drawable.about_rectangle_clicked, null)
                aboutAPIClicked = true
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

        textAboutAppTitle.background = resources.getDrawable(R.drawable.about_rectangle, null)
        textAboutAPITitle.background = resources.getDrawable(R.drawable.about_rectangle, null)

        textAboutAppTitle.setTextColor(ContextCompat.getColor(this.context!!, R.color.mainTextColor))
        textAboutAPITitle.setTextColor(ContextCompat.getColor(this.context!!, R.color.mainTextColor))
    }

}

