package com.gcap.main.calculators.PSIA

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.gcap.R
import com.gcap.core.BASE_URL
import com.gcap.core.GlobalStorage.PSIA_rows
import com.gcap.core.analytics.CalculatorIds
import com.gcap.core.analytics.CalculatorSessionTracker
import com.gcap.core.debounced
import com.gcap.core.disableKeyboardFocus
import com.gcap.core.openUrlInBrowser
import com.gcap.core.setOnEnterOrDone
import com.gcap.excel.ExcelDataModel.PSIA_vlookup
import com.google.android.material.snackbar.Snackbar

class PsiaActivity : AppCompatActivity() {
    private val analytics = CalculatorSessionTracker(this, CalculatorIds.PSIA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_psia)

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.disableKeyboardFocus()
        backButton.setOnClickListener {
            finish()
        }

        val psiaText = findViewById<EditText>(R.id.tvPsia)
        val calculate = debounced { calcValue() }
        val goHomeButton = findViewById<Button>(R.id.go_home)
        goHomeButton.disableKeyboardFocus()
        goHomeButton.setOnClickListener { calculate() }

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


        psiaText.setOnEnterOrDone(calculate)
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
        val psiaText = findViewById<EditText>(R.id.tvPsia)
        val psiaValue = psiaText.text.toString()

        if (psiaValue.isEmpty()){
            return
        }

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
            analytics.trackCalculation(true)

        }
        else{
            analytics.trackCalculation(false)
            Snackbar.make(
                findViewById(R.id.main),
                "PSIA value must be in 4.69 to 307.08",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}