package com.gcap.main.industryContacts

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.gcap.R
import com.gcap.core.models.IndustryItem
import com.squareup.picasso.Picasso

class PartnerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_partner)
        val item = intent.getParcelableExtra<IndustryItem>("industryItem")
        item?.let {
            val imageView = findViewById<ImageView>(R.id.iv)

            Picasso.get()
                .load("https://gcapcoolworks.com/" + item.image)
                .into(imageView)

            findViewById<TextView>(R.id.partner_name).setText(item.cperson)
            findViewById<TextView>(R.id.phone).setText(item.phone)
            findViewById<TextView>(R.id.email).setText(item.email)
            findViewById<TextView>(R.id.website).setText(item.website)
            findViewById<TextView>(R.id.address).setText(item.address)
            findViewById<TextView>(R.id.about).setText(item.about)
        }


        val backButton = findViewById<ImageView>(R.id.back)

        backButton.setOnClickListener {
            finish()
        }

    }
}