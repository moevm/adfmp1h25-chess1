package com.example.magicchess

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainScreen : AppCompatActivity() {

    private lateinit var playerSpinner: Spinner
    private lateinit var modeSpinner: Spinner
    private lateinit var colorSpinner: Spinner
    private lateinit var timeSpinner: Spinner
    private lateinit var startButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_screen)

        // Настройка отступов для системных элементов (если нужно)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация элементов
        playerSpinner = findViewById(R.id.spinnerPlayer)
        modeSpinner = findViewById(R.id.spinnerMode)
        colorSpinner = findViewById(R.id.spinnerColor)
        timeSpinner = findViewById(R.id.spinnerTime)
        startButton = findViewById(R.id.buttonStart)
        backButton = findViewById(R.id.buttonBack)

        // Заполнение списков значениями (варианты можно менять по необходимости)
        setupSpinner(playerSpinner, listOf("online", "friend", "comp"))
        setupSpinner(modeSpinner, listOf("classic", "crazy", "magic"))
        setupSpinner(colorSpinner, listOf("white", "black", "random"))
        setupSpinner(timeSpinner, listOf("without", "5", "10", "30", "60"))

        startButton.setOnClickListener {
            val player = playerSpinner.selectedItem.toString()
            val mode = modeSpinner.selectedItem.toString()
            val colorChoice = colorSpinner.selectedItem.toString()
            val time = timeSpinner.selectedItem.toString()
            val opponentName = if (player == "online") generateRandomNickname() else null

            // Если выбран random, выбираем случайно белый или черный
            val color = if (colorChoice == "random") {
                if (Random.nextBoolean()) "white" else "black"
            } else {
                colorChoice
            }

            // Выбираем целевую активность по режиму
            val targetActivity = when (mode.toLowerCase()) {
                "magic" -> MagicChess::class.java
                "crazy" -> CrazyChess::class.java
                else -> ClassicChess::class.java
            }

            // Передаем все выбранные параметры через Intent
            val intent = Intent(this, targetActivity)
            intent.putExtra("player", player)
            intent.putExtra("mode", mode)
            intent.putExtra("color", color)
            intent.putExtra("time", time)
            intent.putExtra("opponentName", opponentName)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    private fun generateRandomNickname(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..8)
            .map { chars.random() }
            .joinToString("")
    }
}
