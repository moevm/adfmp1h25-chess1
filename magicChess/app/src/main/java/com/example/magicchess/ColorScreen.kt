package com.example.magicchess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ColorScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_color_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_white_color: Button = findViewById(R.id.white)
        val button_black_color: Button = findViewById(R.id.black)
        val button_random_color: Button = findViewById(R.id.random)
        val button_back_color: Button = findViewById(R.id.backColor)

        button_white_color.setOnClickListener {
            val intent = Intent(this, ModeScreen::class.java)
            intent.putExtra("color", "white")
            startActivity(intent)
        }

        button_black_color.setOnClickListener {
            val intent = Intent(this, ModeScreen::class.java)
            intent.putExtra("color", "black")
            startActivity(intent)
        }

        button_random_color.setOnClickListener {
            val intent = Intent(this, ModeScreen::class.java)
            intent.putExtra("color", "random")
            startActivity(intent)
        }

        button_back_color.setOnClickListener {
            finish()
        }
    }
}