package com.gcap.main.calculators.fahrenheit

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.gcap.R
import com.gcap.core.BASE_URL
import com.gcap.core.GlobalStorage.PSIF_rows
import com.gcap.core.openUrlInBrowser
import com.gcap.excel.ExcelDataModel.PSIF_vlookup
import com.google.android.material.snackbar.Snackbar
import kotlin.toString

class FahrenheitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_fahrenheit)

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
            findViewById<EditText>(R.id.tvF).setText("")
            findViewById<TextView>(R.id.psig).setText("")
            findViewById<TextView>(R.id.tvCvLiquid).setText("")
            findViewById<TextView>(R.id.tvCvVaper).setText("")
            findViewById<TextView>(R.id.tvDenLiquid).setText("")
            findViewById<TextView>(R.id.tvDenVaper).setText("")
            findViewById<TextView>(R.id.tvPsia).setText("")
        }
        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }

        val fText = findViewById<EditText>(R.id.tvF)
        fText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                calcValue()
                true
            } else {
                false
            }
        }
    }
    fun calcValue(){
        val fText = findViewById<EditText>(R.id.tvF)

        val fValue = fText.text.toString()

        if (fValue.isEmpty()){
            return
        }

        if (fValue.toDouble() >= -65 && fValue.toDouble() <= 125){
            val psig = PSIF_vlookup(fValue, PSIF_rows, 2)
            val psia = PSIF_vlookup(fValue, PSIF_rows, 3)
            val cvLiquid = PSIF_vlookup(fValue, PSIF_rows, 4)
            val cvVapor = PSIF_vlookup(fValue, PSIF_rows, 5)
            val densityLiquid = PSIF_vlookup(fValue, PSIF_rows, 6)
            val densityVapor = PSIF_vlookup(fValue, PSIF_rows, 7)

            findViewById<TextView>(R.id.psig).setText(psig)
            findViewById<TextView>(R.id.tvCvLiquid).setText(cvLiquid)
            findViewById<TextView>(R.id.tvCvVaper).setText(cvVapor)
            findViewById<TextView>(R.id.tvDenLiquid).setText(densityLiquid)
            findViewById<TextView>(R.id.tvDenVaper).setText(densityVapor)
            findViewById<TextView>(R.id.tvPsia).setText(psia)

        }
        else{
            Snackbar.make(
                findViewById(R.id.main),
                "°F value must be in -65° to 125°",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}