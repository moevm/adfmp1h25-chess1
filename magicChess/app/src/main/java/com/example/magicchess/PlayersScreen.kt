package com.example.magicchess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PlayersScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_players_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_online: Button = findViewById(R.id.playOnline)
        val button_friend: Button = findViewById(R.id.playFriend)
        val button_comp: Button = findViewById(R.id.playComp)
        val button_back_player: Button = findViewById(R.id.backPlayers)

        button_online.setOnClickListener {
            val intent = Intent(this, ColorScreen::class.java)
            intent.putExtra("player", "online")
            startActivity(intent)
        }

        button_friend.setOnClickListener {
            val intent = Intent(this, ColorScreen::class.java)
            intent.putExtra("player", "friend")
            startActivity(intent)
        }

        button_comp.setOnClickListener {
            val intent = Intent(this, ColorScreen::class.java)
            intent.putExtra("player", "comp")
            startActivity(intent)
        }

        button_back_player.setOnClickListener {
            finish()
        }
    }
}