package com.gcap.main.calculators

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.gcap.R
import com.gcap.core.BASE_URL
import com.gcap.core.GlobalStorage.PSIA_rows
import com.gcap.core.GlobalStorage.PSIF_rows
import com.gcap.core.GlobalStorage.PSIG_rows
import com.gcap.core.GlobalStorage.Superheat_rows
import com.gcap.core.models.SuperheatExcelRow
import com.gcap.core.openUrlInBrowser
import com.gcap.excel.ExcelDataModel.loadExcel_PSIA
import com.gcap.excel.ExcelDataModel.loadExcel_PSIF
import com.gcap.excel.ExcelDataModel.loadExcel_PSIG
import com.gcap.excel.ExcelDataModel.loadExcel_Superheat
import com.gcap.main.calculators.PSIA.PsiaActivity
import com.gcap.main.calculators.PSIG.PsigActivity
import com.gcap.main.calculators.RealeaseCalculator.ReleaseActivity
import com.gcap.main.calculators.fahrenheit.FahrenheitActivity
import com.gcap.main.calculators.pressureEnthalpy.PressureActivity
import com.gcap.main.calculators.superheatSubcooling.SuperActivity
import kotlinx.coroutines.launch

class CalculatorsActivity : AppCompatActivity() {
    private lateinit var loadingOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_calculators)

        loadingOverlay = findViewById(R.id.loadingOverlay)
        loadingOverlay.setOnTouchListener { _, _ -> true }


        val cv1 = findViewById<CardView>(R.id.cv1)
        val cv2 = findViewById<CardView>(R.id.cv2)
        val cv3 = findViewById<CardView>(R.id.cv3)
        val cv4 = findViewById<CardView>(R.id.cv4)
        val cv5 = findViewById<CardView>(R.id.cv5)
        val cv6 = findViewById<CardView>(R.id.cv6)

        cv1.setOnClickListener {
            val intent = Intent(this, PsigActivity::class.java)
            startActivity(intent)
        }

        cv2.setOnClickListener {
            val intent = Intent(this, PsiaActivity::class.java)
            startActivity(intent)
        }

        cv3.setOnClickListener {
            val intent = Intent(this, FahrenheitActivity::class.java)
            startActivity(intent)
        }

        cv4.setOnClickListener {
            val intent = Intent(this, PressureActivity::class.java)
            startActivity(intent)
        }

        cv5.setOnClickListener {
            val intent = Intent(this, ReleaseActivity::class.java)
            startActivity(intent)
        }

        cv6.setOnClickListener {
            val intent = Intent(this, SuperActivity::class.java)
            startActivity(intent)
        }

        val backButton = findViewById<ImageView>(R.id.back)

        backButton.setOnClickListener {
            finish()
        }

        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }

        lifecycleScope.launch {
            showLoading(true)

            if (PSIG_rows.isEmpty()) {
                loadExcel_PSIG(this@CalculatorsActivity)
            }
            if (PSIA_rows.isEmpty()) {
                loadExcel_PSIA(this@CalculatorsActivity)
            }

            if (PSIF_rows.isEmpty()) {
                loadExcel_PSIF(this@CalculatorsActivity)
            }

            if (Superheat_rows.isEmpty()){
                loadExcel_Superheat(this@CalculatorsActivity)
            }

            showLoading(false)
        }
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