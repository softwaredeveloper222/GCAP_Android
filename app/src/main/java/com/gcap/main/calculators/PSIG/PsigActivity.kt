package com.gcap.main.calculators.PSIG

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
import com.gcap.core.GlobalStorage.PSIG_rows
import com.gcap.core.analytics.CalculatorIds
import com.gcap.core.analytics.CalculatorSessionTracker
import com.gcap.core.openUrlInBrowser
import com.gcap.excel.ExcelDataModel.PSIG_vlookup
import com.google.android.material.snackbar.Snackbar

class PsigActivity : AppCompatActivity() {
    private val analytics = CalculatorSessionTracker(this, CalculatorIds.PSIG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_psig)

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val psigText = findViewById<EditText>(R.id.psig)
        val goHomeButton = findViewById<Button>(R.id.go_home)
        goHomeButton.setOnClickListener {
            calcValue()
//            finish()
        }

        val clearButton = findViewById<Button>(R.id.clear)
        clearButton.setOnClickListener {
            findViewById<EditText>(R.id.psig).setText("")
            findViewById<TextView>(R.id.tvPsia).setText("")
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


        psigText.setOnKeyListener { v, keyCode, event ->
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

    fun calcValue(){
        val psigText = findViewById<EditText>(R.id.psig)

        val psigValue = psigText.text.toString()

        if (psigValue.isEmpty()){
            return
        }

        if (psigValue.toDouble() >= -20.4 && psigValue.toDouble() <= 293.1){
            val psia = PSIG_vlookup(psigValue, PSIG_rows, 2)
            val cvLiquid = PSIG_vlookup(psigValue, PSIG_rows, 3)
            val cvVapor = PSIG_vlookup(psigValue, PSIG_rows, 4)
            val densityLiquid = PSIG_vlookup(psigValue, PSIG_rows, 5)
            val densityVapor = PSIG_vlookup(psigValue, PSIG_rows, 6)
            val temperature = PSIG_vlookup(psigValue, PSIG_rows, 7)

            findViewById<TextView>(R.id.tvPsia).setText(psia)
            findViewById<TextView>(R.id.tvCvLiquid).setText(cvLiquid)
            findViewById<TextView>(R.id.tvCvVaper).setText(cvVapor)
            findViewById<TextView>(R.id.tvDenLiquid).setText(densityLiquid)
            findViewById<TextView>(R.id.tvDenVaper).setText(densityVapor)
            findViewById<TextView>(R.id.tvF).setText(temperature)
            analytics.trackCalculation(true)

        }
        else{
            analytics.trackCalculation(false)
            Snackbar.make(
                findViewById(R.id.main),
                "PSIG value must be in -20.4 to 293.1",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}