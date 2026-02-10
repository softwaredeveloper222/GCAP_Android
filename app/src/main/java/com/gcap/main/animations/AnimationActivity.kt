package com.gcap.main.animations

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcap.R
import com.gcap.core.api.ApiService
import com.gcap.core.models.AnimationItem
import com.gcap.main.chartsGraphs.VerticalSpaceItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.core.view.WindowCompat

class AnimationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnimationsAdapter

    private lateinit var loadingOverlay: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_animation)

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
    }

    private fun fetchValves() {
        showLoading(true)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gcapcoolworks.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.getAnimations().enqueue(object : Callback<List<AnimationItem>> {
            override fun onResponse(
                call: Call<List<AnimationItem>>,
                response: Response<List<AnimationItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { animations ->
                        adapter = AnimationsAdapter(animations) { item ->
                            println(item.image)
                            val dialog =
                                VideoPlayerDialog("https://gcapcoolworks.com/" + item.image)
                            dialog.show(supportFragmentManager, "fullscreenVideo")

                        }
                        recyclerView.adapter = adapter
                    }
                } else {
//                    Toast.makeText(this@AnimationActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AnimationItem>>, t: Throwable) {
                showLoading(false)
//                Toast.makeText(this@AnimationActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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