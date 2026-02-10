package com.gcap.main.calculators.PSIA

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
import com.gcap.core.GlobalStorage.PSIA_rows
import com.gcap.core.openUrlInBrowser
import com.gcap.excel.ExcelDataModel.PSIA_vlookup
import com.google.android.material.snackbar.Snackbar

class PsiaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_psia)

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val goHomeButton = findViewById<Button>(R.id.go_home)
        goHomeButton.setOnClickListener {
            finish()
        }

        val clearButton = findViewById<Button>(R.id.clear)
        clearButton.setOnClickListener {
            findViewById<EditText>(R.id.tvPsia).setText("")
            findViewById<TextView>(R.id.psig).setText("")
            findViewById<TextView>(R.id.tvCvLiquid).setText("")
            findViewById<TextView>(R.id.tvCvVaper).setText("")
            findViewById<TextView>(R.id.tvDenLiquid).setText("")
            findViewById<TextView>(R.id.tvDenVaper).setText("")
            findViewById<TextView>(R.id.tvF).setText("")
        }

        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }

        val psiaText = findViewById<EditText>(R.id.tvPsia)
        psiaText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val psiaValue = psiaText.text.toString()

                if (psiaValue.toDouble() >= 4.69 && psiaValue.toDouble() <= 307.08){
                    val psig = PSIA_vlookup(psiaValue, PSIA_rows, 2)
                    val cvLiquid = PSIA_vlookup(psiaValue, PSIA_rows, 3)
                    val cvVapor = PSIA_vlookup(psiaValue, PSIA_rows, 4)
                    val densityLiquid = PSIA_vlookup(psiaValue, PSIA_rows, 5)
                    val densityVapor = PSIA_vlookup(psiaValue, PSIA_rows, 6)
                    val temperature = PSIA_vlookup(psiaValue, PSIA_rows, 7)

                    findViewById<TextView>(R.id.psig).setText(psig)
                    findViewById<TextView>(R.id.tvCvLiquid).setText(cvLiquid)
                    findViewById<TextView>(R.id.tvCvVaper).setText(cvVapor)
                    findViewById<TextView>(R.id.tvDenLiquid).setText(densityLiquid)
                    findViewById<TextView>(R.id.tvDenVaper).setText(densityVapor)
                    findViewById<TextView>(R.id.tvF).setText(temperature)

                }
                else{
                    Snackbar.make(
                        findViewById(R.id.main),
                        "PSIA value must be in 4.69 to 307.08",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                true
            } else {
                false
            }
        }
    }
}