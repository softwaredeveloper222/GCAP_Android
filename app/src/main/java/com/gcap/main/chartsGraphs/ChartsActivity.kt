package com.gcap.main.chartsGraphs

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcap.R
import com.gcap.core.BASE_URL
import com.gcap.core.api.ApiService
import com.gcap.core.models.ChartItem
import com.gcap.core.openUrlInBrowser
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChartsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChartsAdapter
    private lateinit var loadingOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_charts)

        loadingOverlay = findViewById(R.id.loadingOverlay)
        loadingOverlay.setOnTouchListener { _, _ -> true }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.addItemDecoration(
            VerticalSpaceItemDecoration(15)
        )

        fetchValves()

        val backButton = findViewById<ImageView>(R.id.back)

        backButton.setOnClickListener {
            finish()
        }

        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }
    }

    private fun fetchValves() {
        showLoading(true)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gcapcoolworks.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.getCharts().enqueue(object : Callback<List<ChartItem>> {
            override fun onResponse(
                call: Call<List<ChartItem>>,
                response: Response<List<ChartItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { chart ->
                        adapter = ChartsAdapter(chart) { item ->
                            print(item.image)
                            if (item.image.endsWith(".pdf", ignoreCase = true)) {
                                val pdfUrl = "https://gcapcoolworks.com/" + item.image

                                // Open the PDF in a browser
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(pdfUrl)
                                startActivity(intent)
                            } else {
                                showImagePopup("https://gcapcoolworks.com/" + item.image)
                            }
                        }
                        recyclerView.adapter = adapter
                    }
                } else {
//                    Toast.makeText(this@ChartsActivity, "Failed to load data", Toast.LENGTH_SHORT)
//                        .show()
                }
            }

            override fun onFailure(call: Call<List<ChartItem>>, t: Throwable) {
                showLoading(false)
//                Toast.makeText(this@ChartsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
//                    .show()
            }
        })
    }

    private fun showImagePopup(imageUrl: String) {
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.popup_image)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        val popupImageView = dialog.findViewById<ImageView>(R.id.popup_imageView)
        val closeButton = dialog.findViewById<TextView>(R.id.close_popup)
        val progressBar = dialog.findViewById<FrameLayout>(R.id.loadingOverlay)
        progressBar.visibility = View.VISIBLE
        closeButton.visibility = View.GONE
        Picasso.get()
            .load(imageUrl)
            .into(popupImageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    progressBar.visibility = View.GONE
                    closeButton.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    progressBar.visibility = View.GONE
                    closeButton.visibility = View.VISIBLE
                    popupImageView.setImageResource(R.drawable.logo)
                }
            })

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

