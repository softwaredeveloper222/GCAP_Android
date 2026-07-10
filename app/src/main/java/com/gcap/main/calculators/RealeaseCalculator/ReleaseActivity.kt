package com.gcap.main.calculators.RealeaseCalculator

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
import com.gcap.core.GlobalStorage.PSIG_rows
import com.gcap.core.analytics.CalculatorIds
import com.gcap.core.analytics.CalculatorSessionTracker
import com.gcap.core.openUrlInBrowser
import com.gcap.excel.ExcelDataModel.PSIF_vlookup
import com.gcap.excel.ExcelDataModel.PSIG_vlookup
import com.gcap.excel.ExcelDataModel.formatValue
import kotlin.math.sqrt

class ReleaseActivity : AppCompatActivity() {
    private val analytics = CalculatorSessionTracker(this, CalculatorIds.RELEASE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_release)

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val goHomeButton1 = findViewById<Button>(R.id.go_home1)
        val goHomeButton2 = findViewById<Button>(R.id.go_home2)
        val goHomeButton3 = findViewById<Button>(R.id.go_home3)
        val goHomeButton4 = findViewById<Button>(R.id.go_home4)
        val goHomeButton5 = findViewById<Button>(R.id.go_home5)
        goHomeButton1.setOnClickListener {
            firstFunc()
//            finish()
        }
        goHomeButton2.setOnClickListener {
            secondFunc()
//            finish()
        }
        goHomeButton3.setOnClickListener {
            thirdFunc()
//            finish()
        }
        goHomeButton4.setOnClickListener {
            forthFunc()
//            finish()
        }
        goHomeButton5.setOnClickListener {
            fifthFunc()
//            finish()
        }

        val clearButton1 = findViewById<Button>(R.id.clear1)
        val clearButton2 = findViewById<Button>(R.id.clear2)
        val clearButton3 = findViewById<Button>(R.id.clear3)
        val clearButton4 = findViewById<Button>(R.id.clear4)
        val clearButton5 = findViewById<Button>(R.id.clear5)
        clearButton1.setOnClickListener {
            findViewById<EditText>(R.id.et11).setText("")
            findViewById<EditText>(R.id.et12).setText("")
            findViewById<EditText>(R.id.et13).setText("")
            findViewById<TextView>(R.id.tv11).setText("")
            findViewById<TextView>(R.id.tv12).setText("")
        }
        clearButton2.setOnClickListener {
            findViewById<EditText>(R.id.et21).setText("")
            findViewById<EditText>(R.id.et22).setText("")
            findViewById<EditText>(R.id.et23).setText("")
            findViewById<TextView>(R.id.tv21).setText("")
            findViewById<TextView>(R.id.tv22).setText("")
        }
        clearButton3.setOnClickListener {
            findViewById<EditText>(R.id.et31).setText("")
            findViewById<EditText>(R.id.et32).setText("")
            findViewById<EditText>(R.id.et33).setText("")
            findViewById<TextView>(R.id.tv31).setText("")
            findViewById<TextView>(R.id.tv32).setText("")
        }
        clearButton4.setOnClickListener {
            findViewById<EditText>(R.id.et41).setText("")
            findViewById<EditText>(R.id.et42).setText("")
            findViewById<EditText>(R.id.et43).setText("")
            findViewById<TextView>(R.id.tv41).setText("")
            findViewById<TextView>(R.id.tv42).setText("")
        }
        clearButton5.setOnClickListener {
            findViewById<EditText>(R.id.et51).setText("")
            findViewById<EditText>(R.id.et52).setText("")
            findViewById<EditText>(R.id.et53).setText("")
            findViewById<EditText>(R.id.et54).setText("")
            findViewById<EditText>(R.id.et55).setText("")
            findViewById<TextView>(R.id.tv51).setText("")
            findViewById<TextView>(R.id.tv52).setText("")
            findViewById<TextView>(R.id.tv53).setText("")
        }

        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }

        val et11 = findViewById<EditText>(R.id.et11)
        val et12 = findViewById<EditText>(R.id.et12)
        val et13 = findViewById<EditText>(R.id.et13)

        et11.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                firstFunc()
                true
            } else {
                false
            }
        }

        et12.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                firstFunc()
                true
            } else {
                false
            }
        }

        et13.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                firstFunc()
                true
            } else {
                false
            }
        }

        val et21 = findViewById<EditText>(R.id.et21)
        val et22 = findViewById<EditText>(R.id.et22)
        val et23 = findViewById<EditText>(R.id.et23)

        et21.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                secondFunc()
                true
            } else {
                false
            }
        }

        et22.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                secondFunc()
                true
            } else {
                false
            }
        }

        et23.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                secondFunc()
                true
            } else {
                false
            }
        }

        val et31 = findViewById<EditText>(R.id.et31)
        val et32 = findViewById<EditText>(R.id.et32)
        val et33 = findViewById<EditText>(R.id.et33)

        et31.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                thirdFunc()
                true
            } else {
                false
            }
        }

        et32.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                thirdFunc()
                true
            } else {
                false
            }
        }

        et33.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                thirdFunc()
                true
            } else {
                false
            }
        }

        val et41 = findViewById<EditText>(R.id.et41)
        val et42 = findViewById<EditText>(R.id.et42)
        val et43 = findViewById<EditText>(R.id.et43)

        et41.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                forthFunc()
                true
            } else {
                false
            }
        }

        et42.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                forthFunc()
                true
            } else {
                false
            }
        }

        et43.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                forthFunc()
                true
            } else {
                false
            }
        }

        val et51 = findViewById<EditText>(R.id.et51)
        val et52 = findViewById<EditText>(R.id.et52)
        val et53 = findViewById<EditText>(R.id.et53)
        val et54 = findViewById<EditText>(R.id.et54)
        val et55 = findViewById<EditText>(R.id.et55)

        et51.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                fifthFunc()
                true
            } else {
                false
            }
        }

        et52.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                fifthFunc()
                true
            } else {
                false
            }
        }

        et53.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                fifthFunc()
                true
            } else {
                false
            }
        }
        et54.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                fifthFunc()
                true
            } else {
                false
            }
        }

        et55.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                fifthFunc()
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

    fun firstFunc(){
        val et11 = findViewById<EditText>(R.id.et11)
        val et12 = findViewById<EditText>(R.id.et12)
        val et13 = findViewById<EditText>(R.id.et13)

        val Liquid_realease_Round_opening_Diameter = et11.text.toString()
        val Liquid_realease_Round_opening_Gauge = et12.text.toString()
        val Liquid_realease_Round_opening_Number = et13.text.toString()

        val temperature = PSIG_vlookup(Liquid_realease_Round_opening_Gauge, PSIG_rows, 5) ?: "0"

        val a = Liquid_realease_Round_opening_Diameter.toDoubleOrNull() ?: 0.0
        val b = Liquid_realease_Round_opening_Number.toDoubleOrNull() ?: 0.0
        val c = Liquid_realease_Round_opening_Gauge.toDoubleOrNull() ?: 0.0
        val d = temperature.toDoubleOrNull() ?: 0.0

        val LiquidFlowRate = 26.81 * a * a * kotlin.math.sqrt(c * d)

        val TotalLiquidReleased = LiquidFlowRate * b

        findViewById<TextView>(R.id.tv11).setText(formatValue(LiquidFlowRate.toString()))
        findViewById<TextView>(R.id.tv12).setText(formatValue(TotalLiquidReleased.toString()))
        analytics.trackCalculation(true)
    }

    fun secondFunc(){
        val et21 = findViewById<EditText>(R.id.et21)
        val et22 = findViewById<EditText>(R.id.et22)
        val et23 = findViewById<EditText>(R.id.et23)

        val Liquid_realease_Irregular_release_Area_of_leak = et21.text.toString()
        val Liquid_realease_Irregular_release_Gauge = et22.text.toString()
        val Liquid_realease_Irregular_release_Number = et23.text.toString()

        val temperature = PSIG_vlookup(Liquid_realease_Irregular_release_Gauge, PSIG_rows, 5) ?: "0"

        val a = Liquid_realease_Irregular_release_Area_of_leak.toDoubleOrNull() ?: 0.0
        val b = Liquid_realease_Irregular_release_Number.toDoubleOrNull() ?: 0.0
        val c = Liquid_realease_Irregular_release_Gauge.toDoubleOrNull() ?: 0.0
        val d = temperature.toDoubleOrNull() ?: 0.0

        val LiquidFlowRate = 34.133 * a * sqrt(c * d)

        val TotalLiquidReleased = LiquidFlowRate * b

        findViewById<TextView>(R.id.tv21).setText(formatValue(LiquidFlowRate.toString()))
        findViewById<TextView>(R.id.tv22).setText(formatValue(TotalLiquidReleased.toString()))
        analytics.trackCalculation(true)
    }
    fun thirdFunc(){
        val et31 = findViewById<EditText>(R.id.et31)
        val et32 = findViewById<EditText>(R.id.et32)
        val et33 = findViewById<EditText>(R.id.et33)

        val Gas_realease_Round_opening_Diameter = et31.text.toString()
        val Gas_realease_Round_opening_Gauge = et32.text.toString()
        val Gas_realease_Round_opening_Number = et33.text.toString()

        val temperature = PSIG_vlookup(Gas_realease_Round_opening_Gauge, PSIG_rows, 7) ?: "0"

        val a = Gas_realease_Round_opening_Diameter.toDoubleOrNull() ?: 0.0
        val b = Gas_realease_Round_opening_Number.toDoubleOrNull() ?: 0.0
        val c = Gas_realease_Round_opening_Gauge.toDoubleOrNull() ?: 0.0
        val d = temperature.toDoubleOrNull() ?: 0.0

        var LiquidFlowRate: Double = 0.0

        if (c > 0) {
            LiquidFlowRate = (15.48 * a * a * (c + 14.7) * c) / (sqrt(d + 459) * c)
        }

        val TotalLiquidReleased = LiquidFlowRate * b

        findViewById<TextView>(R.id.tv31).setText(formatValue(LiquidFlowRate.toString()))
        findViewById<TextView>(R.id.tv32).setText(formatValue(TotalLiquidReleased.toString()))
        analytics.trackCalculation(true)
    }

    fun forthFunc(){
        val et41 = findViewById<EditText>(R.id.et41)
        val et42 = findViewById<EditText>(R.id.et42)
        val et43 = findViewById<EditText>(R.id.et43)

        val Gas_realease_Irregular_release_Area_of_leak = et41.text.toString()
        val Gas_realease_Irregular_release_Gauge = et42.text.toString()
        val Gas_realease_Irregular_release_Number = et43.text.toString()

        val temperature = PSIG_vlookup(Gas_realease_Irregular_release_Gauge, PSIG_rows, 7) ?: "0"

        val a = Gas_realease_Irregular_release_Area_of_leak.toDoubleOrNull() ?: 0.0
        val b = Gas_realease_Irregular_release_Number.toDoubleOrNull() ?: 0.0
        val c = Gas_realease_Irregular_release_Gauge.toDoubleOrNull() ?: 0.0
        val d = temperature.toDoubleOrNull() ?: 0.0

        var LiquidFlowRate: Double = 0.0

        if (c > 0.0) {
            LiquidFlowRate = (19.71 * a * (c + 14.66)) / sqrt(d + 459)
        }

        val TotalLiquidReleased = LiquidFlowRate * b

        findViewById<TextView>(R.id.tv41).setText(formatValue(LiquidFlowRate.toString()))
        findViewById<TextView>(R.id.tv42).setText(formatValue(TotalLiquidReleased.toString()))
        analytics.trackCalculation(true)
    }

    fun  fifthFunc(){
        val et51 = findViewById<EditText>(R.id.et51)
        val et52 = findViewById<EditText>(R.id.et52)
        val et53 = findViewById<EditText>(R.id.et53)
        val et54 = findViewById<EditText>(R.id.et53)
        val et55 = findViewById<EditText>(R.id.et53)

        val Room_level_Length_of_room = et51.text.toString()
        val Room_level_Height_of_room = et52.text.toString()
        val Room_level_Width_of_room_1 = et53.text.toString()
        val Room_level_Width_of_room_2 = et54.text.toString()
        val Room_level_Width_of_room_3 = et55.text.toString()

        val temperature = PSIF_vlookup(Room_level_Width_of_room_2, PSIF_rows, 7) ?: "0"

        val a = Room_level_Length_of_room.toDoubleOrNull() ?: 0.0
        val b = Room_level_Height_of_room.toDoubleOrNull() ?: 0.0
        val c = Room_level_Width_of_room_1.toDoubleOrNull() ?: 0.0
        val d = Room_level_Width_of_room_2.toDoubleOrNull() ?: 0.0
        val e = Room_level_Width_of_room_3.toDoubleOrNull() ?: 0.0
        val f = temperature.toDoubleOrNull() ?: 0.0

        val result = a * b * c

        val result1 = result * f * (e / 1000000)

        findViewById<TextView>(R.id.tv51).setText(formatValue(result.toString()))
        findViewById<TextView>(R.id.tv52).setText(formatValue(temperature.toString()))
        findViewById<TextView>(R.id.tv53).setText(formatValue(result1.toString()))
        analytics.trackCalculation(true)
    }
}
