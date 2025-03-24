package com.example.magicchess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button1: Button = findViewById(R.id.menu)
        val button2: Button = findViewById(R.id.settings)
        val button3: Button = findViewById(R.id.button_about)
        val button4: Button = findViewById(R.id.button_start)

        button1.setOnClickListener {
            val intent = Intent(this, MainScreen::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this, SettingsScreen::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        // Обновленный обработчик для кнопки "Старт"
        button4.setOnClickListener {
            val intent = Intent(this, ClassicChess::class.java)
            intent.putExtra("player", "friend")
            intent.putExtra("mode", "classic")
            intent.putExtra("color", "white") // По умолчанию пусть игрок будет играть белыми
            intent.putExtra("time", "without") // Время не устанавливается
            startActivity(intent)
        }
    }
}
