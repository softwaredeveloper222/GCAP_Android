package com.gcap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.graphics.Color
import com.gcap.main.animations.AnimationActivity
import com.gcap.main.calculators.CalculatorsActivity
import android.widget.ImageView
import androidx.core.view.WindowCompat
import com.gcap.core.BASE_URL
import com.gcap.core.openUrlInBrowser
import com.gcap.main.chartsGraphs.ChartsActivity
import com.gcap.main.contactUs.ContactActivity
import com.gcap.main.formulas.FormulaActivity
import com.gcap.main.industryContacts.IndustryActivity
import com.gcap.main.magneticTool.MagneticActivity
import com.gcap.main.valvePositions.ValvesActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)

        val cvCal = findViewById<CardView>(R.id.cvCal)
        val cvValve = findViewById<CardView>(R.id.cvValve)
        val cvFormula = findViewById<CardView>(R.id.cvFormula)
        val cvChart = findViewById<CardView>(R.id.cvChart)
        val cvAnimation = findViewById<CardView>(R.id.cvAnimation)
        val cvMagnetic = findViewById<CardView>(R.id.cvMagnetic)
        val cvIndustry = findViewById<CardView>(R.id.cvIndustry)
        val cvContact = findViewById<CardView>(R.id.cvContact)

        cvCal.setOnClickListener {
            val intent = Intent(this, CalculatorsActivity::class.java)
            startActivity(intent)
        }
        cvValve.setOnClickListener {
            val intent = Intent(this, ValvesActivity::class.java)
            startActivity(intent)
        }

        cvFormula.setOnClickListener {
            val intent = Intent(this, FormulaActivity::class.java)
            startActivity(intent)
        }

        cvChart.setOnClickListener {
            val intent = Intent(this, ChartsActivity::class.java)
            startActivity(intent)
        }

        cvAnimation.setOnClickListener {
            val intent = Intent(this, AnimationActivity::class.java)
            startActivity(intent)
        }

        cvMagnetic.setOnClickListener {
            val intent = Intent(this, MagneticActivity::class.java)
            startActivity(intent)
        }

        cvIndustry.setOnClickListener {
            val intent = Intent(this, IndustryActivity::class.java)
            startActivity(intent)
        }

        cvContact.setOnClickListener {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }
    }
}