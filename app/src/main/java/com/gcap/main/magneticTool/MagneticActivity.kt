package com.gcap.main.magneticTool

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.gcap.R
import com.gcap.core.BASE_URL
import com.gcap.core.openUrlInBrowser
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.suke.widget.SwitchButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

class MagneticActivity : AppCompatActivity() {
    private lateinit var showDeactivateLayout: View
    private lateinit var circleImage: ImageView

    private var magneticValue = 400

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        setContentView(R.layout.activity_magnetic)

        showDeactivateLayout = findViewById(R.id.clAdvanced)
        circleImage = findViewById(R.id.circle)


        val backButton = findViewById<ImageView>(R.id.back)

        backButton.setOnClickListener {
            finish()
        }

        val logoImage = findViewById<ImageView>(R.id.logo)

        logoImage.setOnClickListener {
            openUrlInBrowser(this, BASE_URL)
        }

        val switchButton = findViewById<SwitchButton>(R.id.switch_advance)

        switchButton.setOnCheckedChangeListener { viewModelStore, isChecked ->
            if (isChecked) {
                showDeactivateLayout.visibility = View.VISIBLE
            } else {
                showDeactivateLayout.visibility = View.GONE
            }
        }

        val llThreshold = findViewById<LinearLayout>(R.id.llThreshold)

        val circleImage = findViewById<ImageView>(R.id.circle)

        llThreshold.setOnClickListener {
            showBottomSheet(this, 400) { newValue ->
                val textView = findViewById<TextView>(R.id.tvThreshold)
                textView.text = "Threshold: $newValue µT"

                updateImageRotate(newValue)
            }
        }

        val llActivate = findViewById<LinearLayout>(R.id.llActivate)

        val tvActivate = findViewById<TextView>(R.id.tvActivate)

        val ivActivate = findViewById<ImageView>(R.id.ivActivate)

        llActivate.setOnClickListener {
            val activateText = tvActivate.text.toString()

            if (activateText == "Deactivate") {
                tvActivate.text = "Activate"
                ivActivate.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play))
            } else {
                tvActivate.text = "Deactivate"
                ivActivate.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause))
            }
            updateImageRotate(magneticValue)
        }

        val volume = findViewById<ImageView>(R.id.volume)
        val vibrate = findViewById<ImageView>(R.id.vibrate)

        var isVolume = true
        var isVibrate = true

        volume.setOnClickListener {
            if (isVolume) {
                volume.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.volume_off))
                isVolume = false
            }
            else {
                volume.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.volume_on))
                isVolume = true
            }
        }

        vibrate.setOnClickListener {
            if (isVibrate) {
                vibrate.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vibrate_off))
                isVibrate = false
            }
            else {
                vibrate.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vibrate_on))
                isVibrate = true
            }
        }
    }

    fun updateImageRotate(value: Int) {
        val tvActivate = findViewById<TextView>(R.id.tvActivate)
        val activateText = tvActivate.text.toString()
        if (activateText == "Deactivate") {
            if (value in 1..37) {
                if (!animator.isRunning) {
                    animator.start()
                }
            } else {
                if (animator.isRunning) {
                    animator.cancel()
                    ObjectAnimator.ofFloat(circleImage, "rotation", circleImage.rotation, 360f)
                        .apply {
                            duration = 500
                            interpolator = DecelerateInterpolator()
                            start()
                        }
                }
            }
        } else{
            if (animator.isRunning) {
                animator.cancel()
                ObjectAnimator.ofFloat(circleImage, "rotation", circleImage.rotation, 360f)
                    .apply {
                        duration = 500
                        interpolator = DecelerateInterpolator()
                        start()
                    }
            }
        }

    }

    private val animator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(circleImage, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showBottomSheet(context: Context, initialValue: Int = 400, onValueChanged: (Int) -> Unit) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet, null)

        dialog.setContentView(view)
        dialog.show()

        val valueText = view.findViewById<TextView>(R.id.info)
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        val minusButton = view.findViewById<TextView>(R.id.minus)
        val plusButton = view.findViewById<TextView>(R.id.plus)
        val doneButton = view.findViewById<TextView>(R.id.done)

        seekBar.max = 2000
        seekBar.min = 1
        var value = magneticValue
        seekBar.progress = value
        valueText.text = "DC Threshold: $value µT"

        fun updateValue(newValue: Int) {
            value = newValue
            valueText.text = "DC Threshold: $value µT"
            seekBar.progress = value
            onValueChanged(value)
            magneticValue = value
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                magneticValue = progress
                updateValue(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        minusButton.setOnClickListener {
            if (value > 1) updateValue(value - 1)
        }

        plusButton.setOnClickListener {
            if (value < 2000) updateValue(value + 1)
        }

        doneButton.setOnClickListener {
            dialog.dismiss()
        }


    }
}

