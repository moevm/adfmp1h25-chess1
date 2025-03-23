package com.example.magicchess

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class ClassicChess : AppCompatActivity() {

    private val boardSize = 8
    private val boardState = Array(boardSize) { Array<String?>(boardSize) { null } }
    private lateinit var chessBoard: GridLayout
    private var selectedPiece: Pair<Int, Int>? = null
    private var timer: CountDownTimer? = null
    private lateinit var timerView: TextView
    private lateinit var timerView2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic_chess)

        // Установка отступов для системных элементов
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        chessBoard = findViewById(R.id.chess_board)
        timerView = findViewById(R.id.timerView)
        timerView2 = findViewById(R.id.timerView2)

        setupChessBoard()
        setupInitialPosition()

        findViewById<Button>(R.id.backClassicChess).setOnClickListener {
            finish()
        }

        // Получение времени из Intent
        val timeLimit = intent.getStringExtra("time") ?: "without"
        setupTimer(timeLimit)
    }

    private fun setupChessBoard() {
        chessBoard.rowCount = boardSize
        chessBoard.columnCount = boardSize

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val cell = ImageView(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 100
                        height = 100
                    }
                    setBackgroundColor(
                        if ((row + col) % 2 == 0) android.graphics.Color.LTGRAY
                        else android.graphics.Color.DKGRAY
                    )
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setOnClickListener { onCellClicked(row, col) }
                }
                chessBoard.addView(cell)
            }
        }
    }

    private fun setupInitialPosition() {
        val pieces = mapOf(
            "white_pawn" to R.drawable.white_pawn,
            "black_pawn" to R.drawable.black_pawn,
            "white_rook" to R.drawable.white_rook,
            "black_rook" to R.drawable.black_rook,
            "white_knight" to R.drawable.white_knight,
            "black_knight" to R.drawable.black_knight,
            "white_bishop" to R.drawable.white_bishop,
            "black_bishop" to R.drawable.black_bishop,
            "white_queen" to R.drawable.white_queen,
            "black_queen" to R.drawable.black_queen,
            "white_king" to R.drawable.white_king,
            "black_king" to R.drawable.black_king,
        )

        val initialPosition = arrayOf(
            arrayOf("black_rook", "black_knight", "black_bishop", "black_queen", "black_king", "black_bishop", "black_knight", "black_rook"),
            arrayOf("black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn"),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf("white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn"),
            arrayOf("white_rook", "white_knight", "white_bishop", "white_queen", "white_king", "white_bishop", "white_knight", "white_rook")
        )

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                boardState[row][col] = initialPosition[row][col]
                val cell = chessBoard.getChildAt(row * boardSize + col) as ImageView
                initialPosition[row][col]?.let { piece ->
                    cell.setImageResource(pieces[piece]!!)
                }
            }
        }
    }

    private fun onCellClicked(row: Int, col: Int) {
        if (selectedPiece == null) {
            if (boardState[row][col] != null) {
                selectedPiece = row to col
                Toast.makeText(this, "Выбрана фигура: $row,$col", Toast.LENGTH_SHORT).show()
            }
        } else {
            val (fromRow, fromCol) = selectedPiece!!
            val piece = boardState[fromRow][fromCol]
            if (isValidMove(piece, fromRow, fromCol, row, col)) {
                boardState[row][col] = boardState[fromRow][fromCol]
                boardState[fromRow][fromCol] = null
                updateBoard()
                selectedPiece = null
            } else {
                Toast.makeText(this, "Неверный ход", Toast.LENGTH_SHORT).show()
                selectedPiece = null
            }
        }
    }

    // Проверка на допустимость хода для конкретной фигуры
    private fun isValidMove(piece: String?, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        return when (piece) {
            "white_pawn", "black_pawn" -> isValidPawnMove(piece, fromRow, fromCol, toRow, toCol)
            "white_rook", "black_rook" -> isValidRookMove(fromRow, fromCol, toRow, toCol)
            "white_knight", "black_knight" -> isValidKnightMove(fromRow, fromCol, toRow, toCol) // Используем логику коня
            "white_bishop", "black_bishop" -> isValidBishopMove(fromRow, fromCol, toRow, toCol)
            "white_queen", "black_queen" -> isValidQueenMove(fromRow, fromCol, toRow, toCol)
            "white_king", "black_king" -> isValidKingMove(fromRow, fromCol, toRow, toCol)
            else -> false
        }
    }

    private fun isValidPawnMove(piece: String?, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val direction = if (piece == "white_pawn") -1 else 1 // Направление для белой и черной пешки

        // Ход на одну клетку вперед
        if (fromCol == toCol && boardState[toRow][toCol] == null && toRow - fromRow == direction) {
            return true
        }

        // Ход на две клетки вперед с начальной позиции
        if (fromCol == toCol && boardState[toRow][toCol] == null &&
            ((fromRow == 6 && piece == "white_pawn" && toRow - fromRow == -2) ||
                    (fromRow == 1 && piece == "black_pawn" && toRow - fromRow == 2))) {
            // Проверяем, что между пешкой и целевой клеткой нет других фигур
            val intermediateRow = fromRow + direction
            if (boardState[intermediateRow][fromCol] == null) {
                return true
            }
        }

        // Атака по диагонали
        if (Math.abs(toCol - fromCol) == 1 && toRow - fromRow == direction &&
            boardState[toRow][toCol] != null && !isSameColor(piece, boardState[toRow][toCol]!!)) {
            return true
        }

        return false
    }


    // Метод для проверки того, является ли фигура на целевой клетке того же цвета, что и пешка
    private fun isSameColor(piece: String?, targetPiece: String): Boolean {
        return (piece?.startsWith("white") == true && targetPiece.startsWith("white")) ||
                (piece?.startsWith("black") == true && targetPiece.startsWith("black"))
    }

    // Пример для ладьи
    private fun isValidRookMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        if (fromRow != toRow && fromCol != toCol) return false
        return isPathClear(fromRow, fromCol, toRow, toCol)
    }

    private fun isValidKnightMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val rowDiff = Math.abs(fromRow - toRow)
        val colDiff = Math.abs(fromCol - toCol)

        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            val piece = boardState[fromRow][fromCol]
            val targetPiece = boardState[toRow][toCol]
            if (targetPiece != null && isSameColor(piece, targetPiece)) {
                return false
            }
            return true
        }

        return false
    }



    private fun isValidBishopMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {

        if (Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) return false
        val piece = boardState[fromRow][fromCol]
        val targetPiece = boardState[toRow][toCol]
        if (targetPiece != null && isSameColor(piece, targetPiece)) {
            return false
        }

        return isPathClear(fromRow, fromCol, toRow, toCol)
    }

    private fun isValidQueenMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        // Проверка на движение по горизонтали или вертикали (как у ладьи)
        if (fromRow == toRow || fromCol == toCol) {
            // Проверяем, не стоит ли на целевой клетке фигура того же цвета
            val piece = boardState[fromRow][fromCol]
            val targetPiece = boardState[toRow][toCol]
            if (targetPiece != null && isSameColor(piece, targetPiece)) {
                return false // Если фигура того же цвета, то движение недопустимо
            }

            // Проверка пути (горизонталь или вертикаль)
            return isPathClear(fromRow, fromCol, toRow, toCol)
        }

        // Проверка на движение по диагонали (как у слона)
        if (Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol)) {
            // Проверяем, не стоит ли на целевой клетке фигура того же цвета
            val piece = boardState[fromRow][fromCol]
            val targetPiece = boardState[toRow][toCol]
            if (targetPiece != null && isSameColor(piece, targetPiece)) {
                return false
            }

            // Проверка пути (диагональ)
            return isPathClear(fromRow, fromCol, toRow, toCol)
        }

        return false // Если не горизонталь, не вертикаль, и не диагональ — возвращаем false
    }

    // Проверка на допустимость хода для короля с учетом угрозы
    private fun isValidKingMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        // Король может двигаться только на одну клетку в любом направлении
        val rowDiff = Math.abs(fromRow - toRow)
        val colDiff = Math.abs(fromCol - toCol)

        // Если разница по строкам и/или столбцам больше 1, то это недопустимый ход
        if (rowDiff > 1 || colDiff > 1) {
            return false
        }

        // Проверка, что на целевой клетке нет фигуры того же цвета
        val piece = boardState[fromRow][fromCol]
        val targetPiece = boardState[toRow][toCol]
        if (targetPiece != null && isSameColor(piece, targetPiece)) {
            return false // Если фигура того же цвета, то движение недопустимо
        }

        // Проверка, что целевая клетка не под угрозой
        if (isUnderAttack(toRow, toCol, piece)) {
            return false // Если клетка под атакой, король не может туда пойти
        }

        return true
    }


    private fun isUnderAttack(row: Int, col: Int, piece: String?): Boolean {
        // Проверяем все возможные угрозы от противника
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val targetPiece = boardState[r][c]
                if (targetPiece != null && !isSameColor(piece, targetPiece)) {
                    if (canAttack(targetPiece, r, c, row, col)) {
                        return true // Если фигура противника может атаковать клетку, она под угрозой
                    }
                }
            }
        }
        return false
    }


    // Функция для проверки, может ли фигура атаковать данную клетку
    private fun canAttack(piece: String, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        return when (piece) {
            "white_pawn", "black_pawn" -> canPawnAttack(piece, fromRow, fromCol, toRow, toCol)
            "white_rook", "black_rook" -> isValidRookMove(fromRow, fromCol, toRow, toCol)
            "white_knight", "black_knight" -> isValidKnightMove(fromRow, fromCol, toRow, toCol)
            "white_bishop", "black_bishop" -> isValidBishopMove(fromRow, fromCol, toRow, toCol)
            "white_queen", "black_queen" -> isValidQueenMove(fromRow, fromCol, toRow, toCol)
            "white_king", "black_king" -> isValidKingMove(fromRow, fromCol, toRow, toCol)
            else -> false
        }
    }

    // Проверка атаки пешки
    private fun canPawnAttack(piece: String, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val direction = if (piece == "white_pawn") 1 else -1 // Направление для белой и черной пешки
        return Math.abs(fromCol - toCol) == 1 && toRow - fromRow == direction &&
                boardState[toRow][toCol] != null && !isSameColor(piece, boardState[toRow][toCol]!!)
    }





    private fun isPathClear(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        if (fromRow == toRow) { // Горизонтальное движение (ладья)
            val start = Math.min(fromCol, toCol) + 1
            val end = Math.max(fromCol, toCol)
            for (col in start until end) {
                if (boardState[fromRow][col] != null) return false
            }
        } else if (fromCol == toCol) { // Вертикальное движение (ладья)
            val start = Math.min(fromRow, toRow) + 1
            val end = Math.max(fromRow, toRow)
            for (row in start until end) {
                if (boardState[row][fromCol] != null) return false
            }
        } else { // Диагональ (слон)
            val rowStep = if (toRow > fromRow) 1 else -1
            val colStep = if (toCol > fromCol) 1 else -1
            var row = fromRow + rowStep
            var col = fromCol + colStep
            while (row != toRow && col != toCol) {
                if (boardState[row][col] != null) return false
                row += rowStep
                col += colStep
            }
        }
        return true
    }


    private fun updateBoard() {
        val pieces = mapOf(
            "white_pawn" to R.drawable.white_pawn,
            "black_pawn" to R.drawable.black_pawn,
            "white_rook" to R.drawable.white_rook,
            "black_rook" to R.drawable.black_rook,
            "white_knight" to R.drawable.white_knight,
            "black_knight" to R.drawable.black_knight,
            "white_bishop" to R.drawable.white_bishop,
            "black_bishop" to R.drawable.black_bishop,
            "white_queen" to R.drawable.white_queen,
            "black_queen" to R.drawable.black_queen,
            "white_king" to R.drawable.white_king,
            "black_king" to R.drawable.black_king,
        )

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val cell = chessBoard.getChildAt(row * boardSize + col) as ImageView
                cell.setImageResource(0) // Убираем изображение
                boardState[row][col]?.let { piece ->
                    cell.setImageResource(pieces[piece]!!)
                }
            }
        }
    }

    private fun setupTimer(timeLimit: String) {
        if (timeLimit == "without") {
            Toast.makeText(this, "Игра без ограничения по времени", Toast.LENGTH_SHORT).show()
            return
        }

        val timerDuration = timeLimit.toInt() * 60 * 1000 // Преобразуем минуты в миллисекунды
        timer = object : CountDownTimer(timerDuration.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                Toast.makeText(this@ClassicChess, "Время истекло!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        timer?.start()
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
