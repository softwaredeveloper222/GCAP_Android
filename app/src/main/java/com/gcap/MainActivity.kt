package com.gcap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.gcap.main.animations.AnimationActivity
import com.gcap.main.calculators.CalculatorsActivity
import android.widget.ImageView
import androidx.core.view.WindowCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gcap.core.BASE_URL
import com.gcap.core.notifications.SafetyDaysNotificationStore
import com.gcap.core.openUrlInBrowser
import com.gcap.main.chartsGraphs.ChartsActivity
import com.gcap.main.contactUs.ContactActivity
import com.gcap.main.formulas.FormulaActivity
import com.gcap.main.industryContacts.IndustryActivity
import com.gcap.main.magneticTool.MagneticActivity
import com.gcap.main.safetyDays.SafetyDaysActivity
import com.gcap.main.valvePositions.ValvesActivity

class MainActivity : AppCompatActivity() {

    private lateinit var safetyDaysBadge: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)

        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setColorSchemeColors(Color.parseColor("#2D2F93"))
        swipeRefresh.setOnRefreshListener { refreshSafetyDays(fromPull = true) }

        val cvCal = findViewById<CardView>(R.id.cvCal)
        val cvValve = findViewById<CardView>(R.id.cvValve)
        val cvFormula = findViewById<CardView>(R.id.cvFormula)
        val cvChart = findViewById<CardView>(R.id.cvChart)
        val cvAnimation = findViewById<CardView>(R.id.cvAnimation)
        val cvMagnetic = findViewById<CardView>(R.id.cvMagnetic)
        val cvIndustry = findViewById<CardView>(R.id.cvIndustry)
        val cvSafetyDays = findViewById<CardView>(R.id.cvSafetyDays)
        val cvContact = findViewById<CardView>(R.id.cvContact)
        safetyDaysBadge = findViewById(R.id.safetyDaysBadge)

        cvCal.setOnClickListener {
            startActivity(Intent(this, CalculatorsActivity::class.java))
        }
        cvValve.setOnClickListener {
            startActivity(Intent(this, ValvesActivity::class.java))
        }
        cvFormula.setOnClickListener {
            startActivity(Intent(this, FormulaActivity::class.java))
        }
        cvChart.setOnClickListener {
            startActivity(Intent(this, ChartsActivity::class.java))
        }
        cvAnimation.setOnClickListener {
            startActivity(Intent(this, AnimationActivity::class.java))
        }
        cvMagnetic.setOnClickListener {
            startActivity(Intent(this, MagneticActivity::class.java))
        }
        cvIndustry.setOnClickListener {
            startActivity(Intent(this, IndustryActivity::class.java))
        }
        cvSafetyDays.setOnClickListener {
            startActivity(Intent(this, SafetyDaysActivity::class.java))
        }
        cvContact.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        findViewById<ImageView>(R.id.logo).setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }

        refreshSafetyDaysBadge()
        refreshSafetyDays(fromPull = false)
    }

    override fun onResume() {
        super.onResume()
        refreshSafetyDaysBadge()
    }

    private fun refreshSafetyDays(fromPull: Boolean) {
        if (fromPull) {
            swipeRefresh.isRefreshing = true
        }
        SafetyDaysNotificationStore.refresh(this) { _, _ ->
            runOnUiThread {
                refreshSafetyDaysBadge()
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun refreshSafetyDaysBadge() {
        safetyDaysBadge.visibility =
            if (SafetyDaysNotificationStore.hasUnreadUpdate(this)) View.VISIBLE else View.GONE
    }
}
