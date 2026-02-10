package com.gcap.main.contactUs

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.gcap.R
import com.gcap.core.BASE_URL
import com.gcap.core.api.ApiService
import com.gcap.core.models.ContactInfoItem
import com.gcap.core.models.ContactUsResponse
import com.gcap.core.openUrlInBrowser
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor


class ContactActivity : AppCompatActivity() {
    private lateinit var loadingOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_contact)

        loadingOverlay = findViewById(R.id.loadingOverlay)
        loadingOverlay.setOnTouchListener { _, _ -> true }

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        fetchValves { contactInfo ->
            findViewById<TextView>(R.id.phone).text = contactInfo?.phone
            findViewById<TextView>(R.id.tvemail).text = contactInfo?.email
            findViewById<TextView>(R.id.address).text = contactInfo?.address
            findViewById<TextView>(R.id.website).text = contactInfo?.website
        }


        val sendButton = findViewById<Button>(R.id.send)

        sendButton.setOnClickListener {
            val nameText = findViewById<EditText>(R.id.name).text
            val emailText = findViewById<EditText>(R.id.email).text
            val messageText = findViewById<EditText>(R.id.message).text

            if (nameText.isEmpty()) {
                Snackbar.make(
                    findViewById(R.id.main),
                    "Please enter your name",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (emailText.isEmpty()) {
                Snackbar.make(
                    findViewById(R.id.main),
                    "Please enter your email",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (messageText.isEmpty()) {
                Snackbar.make(
                    findViewById(R.id.main),
                    "Please enter your message",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            showLoading(true)

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://gcapcoolworks.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            val api = retrofit.create(ApiService::class.java)

            val name = nameText.toString()
            val email = emailText.toString()
            val message = messageText.toString()

            api.submitContactUs(
                name = name,
                email = email,
                message = message
            ).enqueue(object : Callback<ContactUsResponse> {
                override fun onResponse(
                    call: Call<ContactUsResponse>,
                    response: Response<ContactUsResponse>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        Snackbar.make(
                            findViewById(R.id.main),
                            "Your message has been sent to us.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        Snackbar.make(
                            findViewById(R.id.main),
                            "Failed to load data",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ContactUsResponse>, t: Throwable) {
                    showLoading(false)
                    Snackbar.make(
                        findViewById(R.id.main),
                        "Error: ${t.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })

        }

        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }
    }

    private fun fetchValves(callback: (ContactInfoItem?) -> Unit) {
        showLoading(true)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gcapcoolworks.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.getContactInfo().enqueue(object : Callback<ContactInfoItem> {
            override fun onResponse(
                call: Call<ContactInfoItem>,
                response: Response<ContactInfoItem>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { chart ->
                        callback(response.body())
                    }
                } else {
//                    Toast.makeText(this@ContactActivity, "Failed to load data", Toast.LENGTH_SHORT)
//                        .show()
                }
            }

            override fun onFailure(call: Call<ContactInfoItem>, t: Throwable) {
                showLoading(false)
//                Toast.makeText(this@ContactActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
//                    .show()
            }
        })
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

