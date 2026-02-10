package com.gcap.main.industryContacts

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcap.R
import com.gcap.core.BASE_URL
import com.gcap.core.api.ApiService
import com.gcap.core.models.IndustryItem
import com.gcap.core.openUrlInBrowser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IndustryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IndustryAdapter

    private lateinit var loadingOverlay: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_industry)

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
        api.getIndustry().enqueue(object : Callback<List<IndustryItem>> {
            override fun onResponse(
                call: Call<List<IndustryItem>>,
                response: Response<List<IndustryItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { industry ->
                        adapter = IndustryAdapter(industry) { item ->
                            val intent = Intent(this@IndustryActivity, PartnerActivity::class.java)
                            intent.putExtra("industryItem", item)
                            startActivity(intent)
                        }
                        recyclerView.adapter = adapter
                    }
                } else {
//                    Toast.makeText(this@IndustryActivity, "Failed to load data", Toast.LENGTH_SHORT)
//                        .show()
                }
            }

            override fun onFailure(call: Call<List<IndustryItem>>, t: Throwable) {
                showLoading(false)
//                Toast.makeText(this@IndustryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
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