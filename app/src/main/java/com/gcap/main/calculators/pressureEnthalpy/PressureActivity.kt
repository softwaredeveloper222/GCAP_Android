package com.gcap.main.calculators.pressureEnthalpy

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.gcap.R
import com.gcap.core.BASE_URL
import com.gcap.core.GlobalStorage
import com.gcap.core.GlobalStorage.PSIF_rows
import com.gcap.core.GlobalStorage.wb
import com.gcap.core.normalizeCellRef
import com.gcap.core.openUrlInBrowser
import com.gcap.core.vlookup
import com.gcap.excel.ExcelDataModel.PSIF_vlookup
import com.gcap.excel.ExcelDataModel.formatValue
import com.gcap.excel.ExcelDataModel.loadPressureExcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PressureActivity : AppCompatActivity() {
    private lateinit var loadingOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_pressure)

        loadingOverlay = findViewById(R.id.loadingOverlay)
        loadingOverlay.setOnTouchListener { _, _ -> true }

        initText()
        lifecycleScope.launch {
            showLoading(true)

            if (GlobalStorage.wb == null) {
                loadPressureExcel(this@PressureActivity)
            }

            showLoading(false)
        }

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
            initText()
        }

        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }

        val evaporator = findViewById<EditText>(R.id.evaporator)
        val condensing = findViewById<EditText>(R.id.condensing)
        val psig = findViewById<EditText>(R.id.psig)
        val compressor = findViewById<EditText>(R.id.compressor)

        evaporator.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                calcValue()
                true
            } else {
                false
            }
        }
        condensing.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                calcValue()
                true
            } else {
                false
            }
        }

        psig.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                calcValue()
                true
            } else {
                false
            }
        }

        compressor.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                calcValue()
                true
            } else {
                false
            }
        }

    }

    fun calcValue() {
        val evaporator = findViewById<EditText>(R.id.evaporator)
        val condensing = findViewById<EditText>(R.id.condensing)
        val psig = findViewById<EditText>(R.id.psig)
        val compressor = findViewById<EditText>(R.id.compressor)

        val input_first = evaporator.text.toString()
        val input_second = condensing.text.toString()
        val input_third = psig.text.toString()
        val input_forth = compressor.text.toString()

        lifecycleScope.launch {
            showLoading(true)
            val results = withContext(Dispatchers.IO) {
// Column_1
                val A_1 = formatValue(input_first) ?: ""
                val B_1 = formatValue(input_second) ?: ""
                val C_1 = formatValue(input_second) ?: ""
                val E_1 = formatValue(input_first) ?: ""
                val F_1 = formatValue(input_second) ?: ""
                val G_1 = formatValue(input_second) ?: ""

                val G_3 = formatValue(input_third) ?: ""

// Column_2
                val A_2 = formatValue(PSIF_vlookup(A_1, PSIF_rows, 3) ?: "") ?: ""
                val B_2 = formatValue(PSIF_vlookup(B_1, PSIF_rows, 3) ?: "") ?: ""
                val C_2 = formatValue(PSIF_vlookup(C_1, PSIF_rows, 3) ?: "") ?: ""
                val D_2 = A_2
                val E_2 = formatValue(PSIF_vlookup(E_1, PSIF_rows, 3) ?: "") ?: ""
                val F_2 = formatValue(PSIF_vlookup(F_1, PSIF_rows, 3) ?: "") ?: ""
                val G_2 = formatValue(PSIF_vlookup(G_3, PSIF_rows, 2) ?: "") ?: ""

// Column_3
                val A_3 = formatValue(PSIF_vlookup(A_1, PSIF_rows, 2) ?: "") ?: ""
                val B_3 = formatValue(PSIF_vlookup(B_1, PSIF_rows, 2) ?: "") ?: ""
                val C_3 = formatValue(PSIF_vlookup(C_1, PSIF_rows, 2) ?: "") ?: ""
                val D_3 = A_3
                val E_3 = formatValue(PSIF_vlookup(E_1, PSIF_rows, 2) ?: "") ?: ""
                val F_3 = formatValue(PSIF_vlookup(F_1, PSIF_rows, 2) ?: "") ?: ""

// Column_4
                val A_4 = formatValue(PSIF_vlookup(A_1, PSIF_rows, 8) ?: "") ?: ""
                val B_4 = A_4
                val C_4 = formatValue(PSIF_vlookup(C_1, PSIF_rows, 9) ?: "") ?: ""
                var D_4 = "" // recalc
                val E_4 = formatValue(PSIF_vlookup(E_1, PSIF_rows, 9) ?: "") ?: ""
                val F_4 = formatValue(PSIF_vlookup(F_1, PSIF_rows, 8) ?: "") ?: ""
                val G_4 = F_4

// Column_5
                val res1 = normalizeCellRef(vlookup(C_2, "Sheet3", "F1", "I182", 3, wb) ?: "") ?: ""
                val res2 = normalizeCellRef(vlookup(C_2, "Sheet3", "F1", "I182", 4, wb) ?: "") ?: ""
                val res3 = vlookup(input_forth, "Sheet2", res1, res2, 6, wb) ?: ""
                val C_5 = formatValue(res3) ?: ""
                val D_5 = C_5
                val E_5 = formatValue(PSIF_vlookup(E_1, PSIF_rows, 10) ?: "") ?: ""

                val rest_cell1 =
                    normalizeCellRef(vlookup(A_2, "Sheet3", "A1", "D82", 3, wb) ?: "") ?: ""
                val rest_cell2 =
                    normalizeCellRef(vlookup(A_2, "Sheet3", "A1", "D82", 4, wb) ?: "") ?: ""
                val rest_d_1 = vlookup(D_5, "Sheet2", rest_cell1, rest_cell2, 2, wb) ?: ""
                val D_1 = formatValue(rest_d_1)

                val rest_d_4 =
                    formatValue(vlookup(D_5, "Sheet2", rest_cell1, rest_cell2, 6, wb) ?: "") ?: ""
                D_4 = rest_d_4

// Below Table
                val a = C_4.toDoubleOrNull() ?: 0.0
                val b = B_4.toDoubleOrNull() ?: 0.0
                val Row_1 = formatValue((a - b).toString())

                val Row_2 = formatValue(D_1)

                val result = 200.0 / (a - b)
                val Row_3 = formatValue(result.toString())

                val Row_4 = formatValue(PSIF_vlookup(C_1, PSIF_rows, 5) ?: "") ?: ""

                val d = D_4.toDoubleOrNull() ?: 0.0
                val Row_5 = formatValue(((d - a) / 42.44 * result).toString())

                val e = Row_4.toDoubleOrNull() ?: 0.0
                val Row_6 = formatValue((result * e).toString())

                val g = A_2.toDoubleOrNull() ?: 0.0
                val h = C_2.toDoubleOrNull() ?: 0.0
                val Row_7 = formatValue((g / h).toString())

                val i = F_4.toDoubleOrNull() ?: 0.0
                val Row_8 = formatValue((a - i).toString())

                val Row_9 = formatValue((b - i).toString())


//setData
                findViewById<TextView>(R.id.fa).text = A_1
                findViewById<TextView>(R.id.fb).text = B_1
                findViewById<TextView>(R.id.fc).text = C_1
                findViewById<TextView>(R.id.fd).text = D_1
                findViewById<TextView>(R.id.fe).text = E_1
                findViewById<TextView>(R.id.ff).text = F_1
                findViewById<TextView>(R.id.fg).text = F_1

                findViewById<TextView>(R.id.psiaa).text = A_2
                findViewById<TextView>(R.id.psiab).text = B_2
                findViewById<TextView>(R.id.psiac).text = C_2
                findViewById<TextView>(R.id.psiad).text = D_2
                findViewById<TextView>(R.id.psiae).text = E_2
                findViewById<TextView>(R.id.psiaf).text = F_2
                findViewById<TextView>(R.id.psiag).text = G_2

                findViewById<TextView>(R.id.psiga).text = A_3
                findViewById<TextView>(R.id.psigb).text = B_3
                findViewById<TextView>(R.id.psigc).text = C_3
                findViewById<TextView>(R.id.psigd).text = D_3
                findViewById<TextView>(R.id.psige).text = E_3
                findViewById<TextView>(R.id.psigf).text = F_3
                findViewById<TextView>(R.id.psigg).text = G_3

                findViewById<TextView>(R.id.enthalpya).text = A_4
                findViewById<TextView>(R.id.enthalpyb).text = B_4
                findViewById<TextView>(R.id.enthalpyc).text = C_4
                findViewById<TextView>(R.id.enthalpyd).text = D_4
                findViewById<TextView>(R.id.enthalpye).text = E_4
                findViewById<TextView>(R.id.enthalpyf).text = F_4
                findViewById<TextView>(R.id.enthalpyg).text = G_4

                findViewById<TextView>(R.id.entropyc).text = C_5
                findViewById<TextView>(R.id.entropyd).text = D_5
                findViewById<TextView>(R.id.entropye).text = E_5

                //
                findViewById<TextView>(R.id.info1).text = Row_1
                findViewById<TextView>(R.id.info2).text = Row_2
                findViewById<TextView>(R.id.info3).text = Row_3
                findViewById<TextView>(R.id.info4).text = Row_4
                findViewById<TextView>(R.id.info5).text = Row_5
                findViewById<TextView>(R.id.info6).text = Row_6
                findViewById<TextView>(R.id.info7).text = Row_7
                findViewById<TextView>(R.id.info8).text = Row_8
                findViewById<TextView>(R.id.info9).text = Row_9
            }
            showLoading(false)
        }
    }

    fun initText() {
        findViewById<TextView>(R.id.fa).text = ""
        findViewById<TextView>(R.id.fb).text = ""
        findViewById<TextView>(R.id.fc).text = ""
        findViewById<TextView>(R.id.fd).text = ""
        findViewById<TextView>(R.id.fe).text = ""
        findViewById<TextView>(R.id.ff).text = ""
        findViewById<TextView>(R.id.fg).text = ""

        findViewById<TextView>(R.id.psiaa).text = ""
        findViewById<TextView>(R.id.psiab).text = ""
        findViewById<TextView>(R.id.psiac).text = ""
        findViewById<TextView>(R.id.psiad).text = ""
        findViewById<TextView>(R.id.psiae).text = ""
        findViewById<TextView>(R.id.psiaf).text = ""
        findViewById<TextView>(R.id.psiag).text = ""

        findViewById<TextView>(R.id.psiga).text = ""
        findViewById<TextView>(R.id.psigb).text = ""
        findViewById<TextView>(R.id.psigc).text = ""
        findViewById<TextView>(R.id.psigd).text = ""
        findViewById<TextView>(R.id.psige).text = ""
        findViewById<TextView>(R.id.psigf).text = ""
        findViewById<TextView>(R.id.psigg).text = ""

        findViewById<TextView>(R.id.enthalpya).text = ""
        findViewById<TextView>(R.id.enthalpyb).text = ""
        findViewById<TextView>(R.id.enthalpyc).text = ""
        findViewById<TextView>(R.id.enthalpyd).text = ""
        findViewById<TextView>(R.id.enthalpye).text = ""
        findViewById<TextView>(R.id.enthalpyf).text = ""
        findViewById<TextView>(R.id.enthalpyg).text = ""

        findViewById<TextView>(R.id.entropyc).text = ""
        findViewById<TextView>(R.id.entropyd).text = ""
        findViewById<TextView>(R.id.entropye).text = ""

        //
        findViewById<TextView>(R.id.info1).text = ""
        findViewById<TextView>(R.id.info2).text = ""
        findViewById<TextView>(R.id.info3).text = ""
        findViewById<TextView>(R.id.info4).text = ""
        findViewById<TextView>(R.id.info5).text = ""
        findViewById<TextView>(R.id.info6).text = ""
        findViewById<TextView>(R.id.info7).text = ""
        findViewById<TextView>(R.id.info8).text = ""
        findViewById<TextView>(R.id.info9).text = ""
    }

    private fun showLoading(show: Boolean) {
        val duration = 300L
        if (show) {
            loadingOverlay.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .start()
            }
        } else {
            loadingOverlay.animate()
                .alpha(0f)
                .setDuration(duration)
                .withEndAction { loadingOverlay.visibility = View.GONE }
                .start()
        }
    }
}