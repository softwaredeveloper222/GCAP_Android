package com.gcap.main.valvePositions

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.gcap.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.gcap.core.BASE_URL

import com.gcap.core.models.ValveItem
import com.gcap.core.api.ApiService
import com.gcap.core.openUrlInBrowser
import com.squareup.picasso.Picasso
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class ValvesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ValvesAdapter

    private lateinit var loadingOverlay: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_valves)

        loadingOverlay = findViewById(R.id.loadingOverlay)
        loadingOverlay.setOnTouchListener { _, _ -> true }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
        api.getValves().enqueue(object : Callback<List<ValveItem>> {
            override fun onResponse(
                call: Call<List<ValveItem>>,
                response: Response<List<ValveItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { valves ->
                        adapter = ValvesAdapter(valves){ item ->
                            print(item.list[0].image)
                            showImagePopup("https://gcapcoolworks.com/" + item.list[0].image)
                        }
                        recyclerView.adapter = adapter
                    }
                } else {
//                    Toast.makeText(this@ValvesActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ValveItem>>, t: Throwable) {
                showLoading(false)
//                Toast.makeText(this@ValvesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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
        val duration = 300L // fade duration in ms
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