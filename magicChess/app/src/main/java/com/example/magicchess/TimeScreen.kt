package com.example.magicchess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TimeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_time_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_back_time: Button = findViewById(R.id.backTime)
        val button_without: Button = findViewById(R.id.without)
        val button_5: Button = findViewById(R.id.time5)
        val button_10: Button = findViewById(R.id.time10)
        val button_30: Button = findViewById(R.id.time30)
        val button_60: Button = findViewById(R.id.time60)

        val selected_type = intent.getStringExtra("type")

        val selected_type_activity: AppCompatActivity = when (selected_type) {
            "magic" -> MagicChess()
            "classic" -> ClassicChess()
            "crazy" -> CrazyChess()
            else -> ClassicChess()
        }

        button_without.setOnClickListener {
            navigateToNextScreen(selected_type_activity, "without")
        }

        button_5.setOnClickListener {
            navigateToNextScreen(selected_type_activity, "5")
        }

        button_10.setOnClickListener {
            navigateToNextScreen(selected_type_activity, "10")
        }

        button_30.setOnClickListener {
            navigateToNextScreen(selected_type_activity, "30")
        }

        button_60.setOnClickListener {
            navigateToNextScreen(selected_type_activity, "60")
        }

        button_back_time.setOnClickListener {
            finish()
        }


    }

    private fun navigateToNextScreen(type: AppCompatActivity, time: String) {
        val intent = Intent(this, type::class.java)
        intent.putExtra("time", time)
        startActivity(intent)
    }

}