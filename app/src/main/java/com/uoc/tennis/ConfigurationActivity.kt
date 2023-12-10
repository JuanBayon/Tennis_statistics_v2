package com.uoc.tennis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.uoc.tennis.databinding.ActivityConfigurationBinding

class ConfigurationActivity : Activity() {
    private var optionName: String = MainActivity.NONE
    private var option: String = MainActivity.NONE
    private var firstText: Button? = null
    private var secondText: Button? = null
    private var thirdText: Button? = null
    private var exitButton: Button? = null
    private var _binding: ActivityConfigurationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        firstText = findViewById(R.id.first)
        secondText = findViewById(R.id.second)
        thirdText = findViewById(R.id.third)
        exitButton = findViewById(R.id.exitConfiguration)

        val intent = intent

        optionName = intent.getStringExtra(MainActivity.OPTION_NAME)!!
        firstText!!.text = intent.getStringExtra(MainActivity.FIRST_LABEL)
        secondText!!.text = intent.getStringExtra(MainActivity.SECOND_LABEL)

        val text = intent.getStringExtra(MainActivity.THIRD_LABEL)
        if (text == null) {
            thirdText!!.visibility = View.GONE
        } else {
            thirdText!!.text = text
        }

        firstText!!.setOnClickListener {
            option = intent.getStringExtra(MainActivity.FIRST_OPTION)!!
            exit()
        }

        secondText!!.setOnClickListener {
            option = intent.getStringExtra(MainActivity.SECOND_OPTION)!!
            exit()
        }

        thirdText!!.setOnClickListener {
            option = intent.getStringExtra(MainActivity.THIRD_OPTION)!!
            exit()
        }

        exitButton!!.setOnClickListener {
            exit()
        }
    }

    private fun exit () {
        val intent = Intent().apply {
            putExtra(MainActivity.OPTION, option)
            putExtra(MainActivity.OPTION_NAME, optionName)
        }
        this.setResult(RESULT_OK, intent)
        finish()
    }
}