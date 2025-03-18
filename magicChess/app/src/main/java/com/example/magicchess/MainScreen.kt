package com.example.magicchess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_playerChoice: Button = findViewById(R.id.playerСhoice)
        val button_gameMode: Button = findViewById(R.id.gameMode)
        val button_time: Button = findViewById(R.id.time)
        val button_color: Button = findViewById(R.id.color)
        val button_back: Button = findViewById(R.id.back)

        button_playerChoice.setOnClickListener {
            val intent = Intent(this, PlayersScreen::class.java)
            startActivity(intent)
        }

        button_gameMode.setOnClickListener {
            val intent = Intent(this, ModeScreen::class.java)
            startActivity(intent)
        }

        button_time.setOnClickListener {
            val intent = Intent(this, TimeScreen::class.java)
            startActivity(intent)
        }

        button_color.setOnClickListener {
            val intent = Intent(this, ColorScreen::class.java)
            startActivity(intent)
        }

        button_back.setOnClickListener {
            finish()
        }
    }
}