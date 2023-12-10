package com.uoc.tennis

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.uoc.tennis.databinding.KeyboardActivityBinding


class KeyboardActivity: Activity(), View.OnClickListener {

    private val mTextView: TextView? = null
    private var editText: EditText? = null
    private var del: Button? = null
    private var OK: Button? = null
    private var exit: Button? = null
    private var scroll: RelativeLayout? = null
    private var numbers = "12345678 90 "
    private var buttons = ArrayList<Button>()
    private var textInput: String? = MainActivity.NONE
    private var _binding: KeyboardActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = KeyboardActivityBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        scroll = findViewById<View>(R.id.scroll) as RelativeLayout
        editText = findViewById<View>(R.id.textedit) as EditText
        OK = findViewById<View>(R.id.send) as Button
        OK!!.setOnClickListener {
            textInput = editText!!.text.toString()
            exit(textInput!!)
        }
        setKeyboardCharacters(numbers)
        del = findViewById<View>(R.id.backspace) as Button
        del!!.setOnClickListener {
            if (editText!!.text.toString().isNotEmpty()) {
                editText!!.setText(
                    editText!!.text.toString().substring(0, editText!!.text.length - 1)
                )
            }
        }
        exit = findViewById(R.id.exit)
        exit!!.setOnClickListener {
            exit(MainActivity.NONE)
        }

    }

    private fun exit(text: String) {
        val intent = Intent().apply {
            putExtra(MainActivity.OPTION, text)
            putExtra(MainActivity.OPTION_NAME, MainActivity.AGE)
        }
        this.setResult(RESULT_OK, intent)
        finish()
    }

    override fun onClick(v: View) {
        val button = v as Button
        editText!!.setText(editText!!.text.toString() + "" + button.text)
    }

    private fun setKeyboardCharacters(characters: String) {
        for (i in characters.indices) {
            val b = Button(applicationContext)
            b.id = i
            buttons.add(b)
            val currentButton = buttons[i]
            val r: Resources = resources
            val px =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, r.displayMetrics)
            val lp = RelativeLayout.LayoutParams(px.toInt(), px.toInt())
            if (i > 0) {
                if (i % 4 != 0) {
                    if (i < 4) {
                        lp.addRule(RelativeLayout.BELOW, R.id.textedit)
                    } else {
                        lp.addRule(RelativeLayout.BELOW, buttons[i - 4].id)
                    }
                    if (i == 1) {
                        lp.setMargins(px.toInt(), lp.topMargin, lp.rightMargin, lp.bottomMargin)
                    } else {
                        lp.addRule(RelativeLayout.RIGHT_OF, buttons[i - 1].id)
                    }
                } else {
                    lp.addRule(RelativeLayout.BELOW, buttons[i - 3].id)
                }
            } else {
                lp.addRule(RelativeLayout.BELOW, R.id.textedit)
            }
            currentButton.layoutParams = lp
            currentButton.text = characters[i].toString() + ""
            currentButton.setOnClickListener { v ->
                val button = v as Button
                editText!!.setText(editText!!.text.toString() + "" + button.text)
            }
            scroll!!.addView(currentButton)
        }
    }

}