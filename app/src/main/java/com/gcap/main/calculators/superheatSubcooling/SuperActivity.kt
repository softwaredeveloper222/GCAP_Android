package com.gcap.main.calculators.superheatSubcooling

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.gcap.R
import com.gcap.core.BASE_URL
import com.gcap.core.GlobalStorage.Superheat_rows
import com.gcap.core.analytics.CalculatorIds
import com.gcap.core.analytics.CalculatorSessionTracker
import com.gcap.core.openUrlInBrowser
import com.gcap.excel.ExcelDataModel.Superheat_vlookup

class SuperActivity : AppCompatActivity() {
    private val analytics = CalculatorSessionTracker(this, CalculatorIds.SUPERHEAT_SUBCOOLING)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_super)

        val backButton = findViewById<ImageView>(R.id.back)

        backButton.setOnClickListener {
            finish()
        }

        val goHomeButton = findViewById<Button>(R.id.go_home)
        goHomeButton.setOnClickListener {
            calcValue()
//            finish()
        }
        val clearButton = findViewById<Button>(R.id.clear)
        clearButton.setOnClickListener {
            findViewById<EditText>(R.id.etPsig).setText("")
            findViewById<EditText>(R.id.etF).setText("")
            findViewById<TextView>(R.id.tvSat).setText("")
            findViewById<TextView>(R.id.tvCondition).setText("")
            findViewById<TextView>(R.id.tvDegree).setText("")

            val tvCondition = findViewById<TextView>(R.id.tvCondition)
            tvCondition.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvCondition.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edittext_bg2))
        }
        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }

        val etPsig = findViewById<EditText>(R.id.etPsig)
        val etF = findViewById<EditText>(R.id.etF)

        etPsig.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                calcValue()
                true
            } else {
                false
            }
        }

        etF.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                calcValue()
                true
            } else {
                false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        analytics.onStart()
    }

    override fun onStop() {
        analytics.onStop()
        super.onStop()
    }

    @SuppressLint("ResourceAsColor")
    fun calcValue(){
        val etPsig = findViewById<EditText>(R.id.etPsig)
        val etF = findViewById<EditText>(R.id.etF)

        val tvSat = findViewById<TextView>(R.id.tvSat)
        val tvCondition = findViewById<TextView>(R.id.tvCondition)
        val tvDegree = findViewById<TextView>(R.id.tvDegree)

        val psigText = etPsig.text.toString()
        val fText = etF.text.toString()

        val temperature = Superheat_vlookup(psigText, Superheat_rows, 2) ?: "0"

        val b = fText.toDoubleOrNull() ?: 0.0
        val c = temperature.toDoubleOrNull() ?: 0.0

        var degree_value: Double = 0.0

        var Condition = ""

        if (b > c) {
            degree_value = b - c
            Condition = "SUPERHEATED"
            tvCondition.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edittext_bg_heat))
        }
        else{
            degree_value = c - b
            Condition = "SUBCOLLED"
            tvCondition.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edittext_bg_cool))
        }

        tvSat.setText(c.toString())
        tvDegree.setText(degree_value.toString())

        tvCondition.setTextColor(ContextCompat.getColor(this, R.color.white))
        tvCondition.setText(Condition)
        analytics.trackCalculation(true)
    }
}