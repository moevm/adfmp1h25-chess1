package com.example.magicchess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ModeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mode_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_classic_chess: Button = findViewById(R.id.classic)
        val button_crazy_chess: Button = findViewById(R.id.crazy)
        val button_magic_chess: Button = findViewById(R.id.magic)
        val button_back_chess: Button = findViewById(R.id.backChess)

        button_classic_chess.setOnClickListener {
            val intent = Intent(this, TimeScreen::class.java)
            intent.putExtra("type", "classic")
            startActivity(intent)
        }

        button_crazy_chess.setOnClickListener {
            val intent = Intent(this, TimeScreen::class.java)
            intent.putExtra("type", "crazy")
            startActivity(intent)
        }

        button_magic_chess.setOnClickListener {
            val intent = Intent(this, TimeScreen::class.java)
            intent.putExtra("type", "magic")
            startActivity(intent)
        }

        button_back_chess.setOnClickListener {
            finish()
        }
    }
}