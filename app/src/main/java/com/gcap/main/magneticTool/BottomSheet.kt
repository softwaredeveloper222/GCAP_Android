package com.gcap.main.magneticTool

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gcap.R
import android.widget.SeekBar
import android.widget.TextView

class BottomSheet(context: Context, private val onValueChaged:(Int) -> Unit) : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.bottom_sheet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var value = 400

        val valueText = findViewById<TextView>(R.id.info)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        seekBar.max = 2000
        seekBar.progress = value
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                value = progress
                onValueChaged(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })



        val minusButton = findViewById<TextView>(R.id.minus)
        val plusButton = findViewById<TextView>(R.id.plus)
        val doneButton = findViewById<TextView>(R.id.done)

        minusButton.setOnClickListener {
            if (value == 1){
                return@setOnClickListener
            }
            value--
            seekBar.progress = value

            onValueChaged(value)
        }

        plusButton.setOnClickListener {
            if (value == 2000){
                return@setOnClickListener
            }
            value++
            seekBar.progress = value

            onValueChaged(value)
        }

        doneButton.setOnClickListener {
            finish()
        }
    }
}