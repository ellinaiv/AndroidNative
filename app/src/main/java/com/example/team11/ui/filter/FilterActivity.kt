package com.example.team11.ui.filter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team11.R
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        supportActionBar!!.hide()
        buttonBack.setOnClickListener {
            finish()
        }
    }
}
