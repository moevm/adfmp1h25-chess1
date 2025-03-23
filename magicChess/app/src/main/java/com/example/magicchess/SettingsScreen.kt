package com.example.magicchess

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsScreen : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings_screen)

        sharedPreferences = getSharedPreferences("PlayerPreferences", Context.MODE_PRIVATE)

        // Установить имя игрока
        val playerName = sharedPreferences.getString("player_name", "Player 1")
        val playerNameEditText: EditText = findViewById(R.id.playerNameEditText)
        playerNameEditText.setText(playerName)

        // Обработчик кнопки "Назад"
        val button_back_set: Button = findViewById(R.id.backSet)
        button_back_set.setOnClickListener {
            finish()
        }

        // Обработчик кнопки "Смена имени"
        val button_changeName: Button = findViewById(R.id.changeName)
        button_changeName.setOnClickListener {
            val newName = playerNameEditText.text.toString()
            if (newName.isNotEmpty()) {
                savePlayerName(newName)
            }
        }
    }

    // Сохранение имени игрока в SharedPreferences
    private fun savePlayerName(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString("player_name", name)
        editor.apply()
    }
}
