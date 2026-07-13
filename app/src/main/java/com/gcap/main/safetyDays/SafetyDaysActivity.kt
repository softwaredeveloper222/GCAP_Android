package com.gcap.main.safetyDays

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.gcap.R
import com.gcap.core.notifications.SafetyDaysContent
import com.gcap.core.notifications.SafetyDaysNotificationStore
import com.gcap.core.notifications.SafetyDaysPublicResponse
import com.squareup.picasso.Picasso

class SafetyDaysActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var heroImage: ImageView
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var eventNameText: TextView
    private lateinit var dateText: TextView
    private lateinit var dateDetailsContainer: LinearLayout
    private lateinit var bulletsContainer: LinearLayout
    private lateinit var bodyText: TextView
    private lateinit var galleryContainer: LinearLayout
    private lateinit var registerButton: TextView
    private lateinit var hotelsButton: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_safety_days)

        progressBar = findViewById(R.id.progressBar)
        heroImage = findViewById(R.id.heroImage)
        titleText = findViewById(R.id.titleText)
        subtitleText = findViewById(R.id.subtitleText)
        eventNameText = findViewById(R.id.eventNameText)
        dateText = findViewById(R.id.dateText)
        dateDetailsContainer = findViewById(R.id.dateDetailsContainer)
        bulletsContainer = findViewById(R.id.bulletsContainer)
        bodyText = findViewById(R.id.bodyText)
        galleryContainer = findViewById(R.id.galleryContainer)
        registerButton = findViewById(R.id.registerButton)
        hotelsButton = findViewById(R.id.hotelsButton)
        statusText = findViewById(R.id.statusText)

        findViewById<ImageView>(R.id.back).setOnClickListener { finish() }

        SafetyDaysNotificationStore.getCached(this)?.let { bind(it) }

        progressBar.visibility = View.VISIBLE
        SafetyDaysNotificationStore.refresh(this) { payload, fromNetwork ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                if (payload != null) {
                    bind(payload)
                    SafetyDaysNotificationStore.markSeen(this, payload.version)
                    statusText.text = if (fromNetwork) {
                        getString(R.string.safety_days_updated, payload.version)
                    } else {
                        getString(R.string.safety_days_cached, payload.version)
                    }
                } else {
                    statusText.text = getString(R.string.safety_days_load_error)
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun bind(payload: SafetyDaysPublicResponse) {
        bindContent(payload.content)
    }

    private fun bindContent(content: SafetyDaysContent) {
        titleText.text = content.title

        if (!content.heroImageUrl.isNullOrBlank()) {
            heroImage.visibility = View.VISIBLE
            loadImage(content.heroImageUrl, heroImage)
        } else {
            heroImage.visibility = View.GONE
        }

        setOptionalText(subtitleText, content.subtitle)
        setOptionalText(eventNameText, content.eventName)

        val benefitBullets = content.bullets.filterNot {
            it.contains("sponsorship", ignoreCase = true)
        }
        val sponsorshipLines = content.bullets.filter {
            it.contains("sponsorship", ignoreCase = true)
        }

        bulletsContainer.removeAllViews()
        benefitBullets.forEach { bullet ->
            bulletsContainer.addView(bodyParagraph(bullet, bulleted = false))
        }

        setOptionalText(
            dateText,
            content.dateLabel?.let { getString(R.string.safety_days_date, it) },
        )

        // Website shows a bullet list under Date
        val dateDetails = buildList {
            content.location?.takeIf { it.isNotBlank() }?.let { add("@  $it") }
            content.priceAttendee?.takeIf { it.isNotBlank() }?.let { add(it) }
            content.priceExhibitor?.takeIf { it.isNotBlank() }?.let { add(it) }
            addAll(sponsorshipLines)
        }
        dateDetailsContainer.removeAllViews()
        dateDetails.forEach { line ->
            dateDetailsContainer.addView(bodyParagraph(line, bulleted = true))
        }
        dateDetailsContainer.visibility =
            if (dateDetails.isEmpty()) View.GONE else View.VISIBLE

        bindLink(registerButton, content.registerUrl, getString(R.string.safety_days_register))
        bindLink(hotelsButton, content.hotelsUrl, getString(R.string.safety_days_hotels))

        setOptionalText(bodyText, content.bodyHtml)

        galleryContainer.removeAllViews()
        content.images
            .filter { it.url.isNotBlank() }
            .forEach { image ->
                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    ).also { params ->
                        params.topMargin = (12 * resources.displayMetrics.density).toInt()
                    }
                    adjustViewBounds = true
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    contentDescription = image.alt ?: getString(R.string.safety_days)
                }
                galleryContainer.addView(imageView)
                loadImage(image.url, imageView)
            }
    }

    private fun bindLink(view: TextView, url: String?, title: String) {
        val trimmed = url?.trim().orEmpty()
        if (trimmed.isEmpty()) {
            view.visibility = View.GONE
            view.setOnClickListener(null)
            return
        }
        view.visibility = View.VISIBLE
        view.setOnClickListener {
            SafetyDaysWebActivity.start(this, trimmed, title)
        }
    }

    private fun bodyParagraph(text: String, bulleted: Boolean): TextView {
        val pad = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            6f,
            resources.displayMetrics,
        ).toInt()
        return TextView(this).apply {
            this.text = if (bulleted) "•  $text" else text
            setTextColor(Color.parseColor("#FF727272"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setLineSpacing(0f, 1.35f)
            typeface = Typeface.SANS_SERIF
            setPadding(0, pad, 0, pad)
        }
    }

    private fun loadImage(url: String, target: ImageView) {
        val maxWidth = resources.displayMetrics.widthPixels.coerceAtLeast(1)
        Picasso.get()
            .load(url)
            .resize(maxWidth, 0)
            .onlyScaleDown()
            .into(target)
    }

    private fun setOptionalText(view: TextView, value: String?) {
        if (value.isNullOrBlank()) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            view.text = value
        }
    }
}
