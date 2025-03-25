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
import android.view.View
import kotlin.random.Random
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable




class CrazyChess : AppCompatActivity() {

    private val boardSize = 8
    private val boardState = Array(boardSize) { Array<String?>(boardSize) { null } }
    private lateinit var chessBoard: GridLayout
    private var selectedPiece: Pair<Int, Int>? = null
    private var timer: CountDownTimer? = null
    private lateinit var timerView: TextView
    private lateinit var timerView2: TextView
    private lateinit var currentPlayerView: TextView
    private var currentPlayer: String = "white"
    private lateinit var whiteTimer: CountDownTimer
    private lateinit var blackTimer: CountDownTimer
    private var whiteTimeMillis: Long = 0
    private var blackTimeMillis: Long = 0
    private var isWhiteTimerRunning = false
    private var isBlackTimerRunning = false
    private var timeLimit: String? = null
    private var currentPlayerColor: String = "white"
    private var CompType: String = "nocomp"
    private lateinit var pauseButton: Button
    private var isPaused = false
    private var remainingWhiteTimeMillis: Long = 0L
    private var remainingBlackTimeMillis: Long = 0L
    private lateinit var pauseTextView: TextView
    private lateinit var backClassicChess: Button
    private var selectedCell: ImageView? = null
    private var previousCellColor: Int? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic_chess)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backClassicChess = findViewById(R.id.backClassicChess)

        initializeUI()

        chessBoard = findViewById(R.id.chess_board)
        timerView2 = findViewById(R.id.timerView)
        timerView = findViewById(R.id.timerView2)
        currentPlayerView = findViewById(R.id.currentPlayerView)
        pauseTextView = findViewById(R.id.pauseTextView)

        setupChessBoard()
        val playerType = intent.getStringExtra("player") ?: "friend"
        val timeLimit = intent.getStringExtra("time") ?: "without"
        setupTimer(timeLimit)
        val opponentName = intent.getStringExtra("opponentName") ?: "Игрок 2"


        val intentColor = intent.getStringExtra("color") ?: "black"
        currentPlayerColor = when (intentColor) {
            "random" -> if (Random.nextBoolean()) "white" else "black"
            "black" -> "black"
            else -> "white"
        }

        setupInitialPosition(currentPlayerColor)

        if (playerType == "comp" ||  playerType == "online") {
            handleComputerMove()
            CompType = "comp"
        }
        val player1Name: String
        val player2Name: String
        if (playerType == "comp") {
            player1Name = if (currentPlayerColor == "white") "Компьютер" else "Игрок"
            player2Name = if (currentPlayerColor == "white") "Игрок" else "Компьютер"
        } else if (playerType == "online") {
            player1Name = if (currentPlayerColor != "white") "Игрок" else opponentName
            player2Name = if (currentPlayerColor != "white") opponentName else "Игрок"
        } else {
            player1Name = "Игрок 1"
            player2Name = "Игрок 2"
        }
        findViewById<TextView>(R.id.player1_info).text = player1Name
        findViewById<TextView>(R.id.player2_info).text = player2Name
        pauseButton = findViewById(R.id.pauseClassicChess)

        pauseButton.setOnClickListener {
            if (!isPaused) {
                pauseTimers()
                pauseButton.text = "Возобновить"
                isPaused = true
            } else {
                resumeTimers()
                pauseButton.text = "Пауза"
                isPaused = false
            }
        }

        if (timeLimit == "without" || playerType == "online") {
            pauseButton.visibility = View.GONE
        } else {
            pauseButton.setOnClickListener {
                if (!isPaused) {
                    pauseTimers()
                    pauseButton.text = "Возобновить"
                    pauseTextView.visibility = View.VISIBLE
                    isPaused = true
                    disableChessBoard()
                } else {
                    resumeTimers()
                    pauseButton.text = "Пауза"
                    pauseTextView.visibility = View.GONE
                    isPaused = false
                    enableChessBoard()
                }
            }
        }
    }

    private fun initializeUI() {
        chessBoard = findViewById(R.id.chess_board)
        timerView = findViewById(R.id.timerView)
        timerView2 = findViewById(R.id.timerView2)
        currentPlayerView = findViewById(R.id.currentPlayerView)
        pauseButton = findViewById(R.id.pauseClassicChess)
        pauseTextView = findViewById(R.id.pauseTextView)
        backClassicChess = findViewById(R.id.backClassicChess)

        backClassicChess.setOnClickListener { showExitConfirmationDialog() }
    }




    private fun disableChessBoard() {
        val chessBoard = findViewById<GridLayout>(R.id.chess_board)
        for (i in 0 until chessBoard.childCount) {
            chessBoard.getChildAt(i).isClickable = false
        }
    }

    private fun enableChessBoard() {
        val chessBoard = findViewById<GridLayout>(R.id.chess_board)
        for (i in 0 until chessBoard.childCount) {
            chessBoard.getChildAt(i).isClickable = true
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Выход в главное меню")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Да") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Выход в главное меню")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Да") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setOnCancelListener {

                super.onBackPressed()
            }
            .create()
            .show()
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

    private fun pauseTimers() {
        if (currentPlayer == "white" && isWhiteTimerRunning) {
            whiteTimer.cancel()
            remainingWhiteTimeMillis = whiteTimeMillis
            isWhiteTimerRunning = false
        } else if (currentPlayer == "black" && isBlackTimerRunning) {
            blackTimer.cancel()
            remainingBlackTimeMillis = blackTimeMillis
            isBlackTimerRunning = false
        }
    }

    private fun resumeTimers() {
        if (currentPlayer == "white" && !isWhiteTimerRunning) {
            whiteTimer = object : CountDownTimer(remainingWhiteTimeMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    whiteTimeMillis = millisUntilFinished
                    val minutes = millisUntilFinished / 1000 / 60
                    val seconds = millisUntilFinished / 1000 % 60
                    timerView.text = String.format("%02d:%02d", minutes, seconds)
                }

                override fun onFinish() {
                    Toast.makeText(this@CrazyChess, "Время вышло! Побеждают Черные", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            whiteTimer.start()
            isWhiteTimerRunning = true
        } else if (currentPlayer == "black" && !isBlackTimerRunning) {
            blackTimer = object : CountDownTimer(remainingBlackTimeMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    blackTimeMillis = millisUntilFinished
                    val minutes = millisUntilFinished / 1000 / 60
                    val seconds = millisUntilFinished / 1000 % 60
                    timerView2.text = String.format("%02d:%02d", minutes, seconds)
                }

                override fun onFinish() {
                    Toast.makeText(this@CrazyChess, "Время вышло! Побеждают Белые", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            blackTimer.start()
            isBlackTimerRunning = true
        }
    }

    private fun setupInitialPosition(color: String) {
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

        val initialBlackPosition = arrayOf(
            arrayOf("white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_king", "white_pawn", "white_pawn", "white_pawn"),
            arrayOf("white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn"),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf("black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn"),
            arrayOf("black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_king", "black_pawn", "black_pawn", "black_pawn")
        )

        val initialWhitePosition = arrayOf(
            arrayOf("black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_king", "black_pawn", "black_pawn", "black_pawn"),
            arrayOf("black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn"),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf<String?>(null, null, null, null, null, null, null, null),
            arrayOf("white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn"),
            arrayOf("white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_king", "white_pawn", "white_pawn", "white_pawn")
        )

        val finalPosition = when (color) {
            "black" -> initialBlackPosition
            "white" -> initialWhitePosition
            "random" -> if (currentPlayerColor == "white") initialWhitePosition else initialBlackPosition
            else -> initialWhitePosition
        }

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                boardState[row][col] = finalPosition[row][col]
                val cell = chessBoard.getChildAt(row * boardSize + col) as ImageView
                finalPosition[row][col]?.let { piece ->
                    val resource = pieces[piece] ?: R.drawable.empty // Используйте прозрачное изображение или пустой цвет
                    cell.setImageResource(resource)
                }
            }
        }
    }











    private fun onCellClicked(row: Int, col: Int) {
        if (selectedPiece == null) {
            if (boardState[row][col]?.startsWith(currentPlayer) == true) {
                selectedPiece = row to col
                Toast.makeText(this, "Выбрана фигура: $row,$col", Toast.LENGTH_SHORT).show()
                highlightCell(row, col) // Подсвечиваем клетку
            } else {
                Toast.makeText(this, "Сейчас ходит $currentPlayer!", Toast.LENGTH_SHORT).show()
            }
        } else {
            val (fromRow, fromCol) = selectedPiece!!
            val piece = boardState[fromRow][fromCol]
            if (isValidMove(piece, fromRow, fromCol, row, col)) {
                boardState[row][col] = boardState[fromRow][fromCol]
                boardState[fromRow][fromCol] = null
                updateBoard()
                resetHighlightedCell()
                selectedPiece = null
                switchPlayer()
            } else {
                Toast.makeText(this, "Неверный ход", Toast.LENGTH_SHORT).show()
                resetHighlightedCell()
                selectedPiece = null
            }
        }
    }


    private fun highlightCell(row: Int, col: Int) {
        val cell = chessBoard.getChildAt(row * boardSize + col) as ImageView
        resetHighlightedCell()

        previousCellColor = (cell.background as? ColorDrawable)?.color

        selectedCell = cell
        selectedCell?.setBackgroundResource(R.drawable.chessborder)
    }

    private fun resetHighlightedCell() {
        selectedCell?.setBackgroundColor(previousCellColor ?: android.graphics.Color.TRANSPARENT)
        selectedCell = null
        previousCellColor = null
    }

    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == "white") "black" else "white"
        updateCurrentPlayerView()
        startTimerForCurrentPlayer()

        val statusView: TextView = findViewById(R.id.statusView)
        if (isUnderAttackForCurrentKing()) {
            if (isCheckmate(currentPlayer)) {
                statusView.text = "Мат – победа!"
                return
            } else {
                statusView.text = "Шах!"
            }
        } else {
            statusView.text = "Статус: норм"
        }
        if (CompType == "comp" && currentPlayer != currentPlayerColor) {
            makeAIMove()
        }

    }

    private fun isUnderAttackForCurrentKing(): Boolean {
        val kingString = if (currentPlayer == "white") "white_king" else "black_king"
        var kingRow = -1
        var kingCol = -1
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                if (boardState[r][c] == kingString) {
                    kingRow = r
                    kingCol = c
                    break
                }
            }
            if (kingRow != -1) break
        }
        if (kingRow == -1 || kingCol == -1) return true
        return isUnderAttack(kingRow, kingCol, kingString)
    }



    private fun updateCurrentPlayerView() {
        currentPlayerView.text = "Текущий игрок: ${if (currentPlayer == "white") "Белые" else "Черные"}"
    }
    private fun simulateMoveAndCheckKing(
        piece: String?,
        fromRow: Int, fromCol: Int,
        toRow: Int, toCol: Int
    ): Boolean {
        val backupBoard = Array(boardSize) { row ->
            boardState[row].copyOf()
        }

        boardState[toRow][toCol] = boardState[fromRow][fromCol]
        boardState[fromRow][fromCol] = null

        val kingString = if (boardState[toRow][toCol]?.startsWith("white") == true) "white_king" else "black_king"
        var kingRow = -1
        var kingCol = -1
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                if (boardState[r][c] == kingString) {
                    kingRow = r
                    kingCol = c
                    break
                }
            }
            if (kingRow != -1) break
        }

        val kingUnderAttack = if (kingRow == -1 || kingCol == -1) true else isUnderAttack(kingRow, kingCol, kingString)

        for (r in 0 until boardSize) {
            boardState[r] = backupBoard[r]
        }
        return !kingUnderAttack
    }



    private fun isValidMove(piece: String?, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val basicValid = when (piece) {
            "white_pawn", "black_pawn" -> isValidPawnMove(piece, fromRow, fromCol, toRow, toCol)
            "white_rook", "black_rook" -> isValidRookMove(fromRow, fromCol, toRow, toCol)
            "white_knight", "black_knight" -> isValidKnightMove(fromRow, fromCol, toRow, toCol)
            "white_bishop", "black_bishop" -> isValidBishopMove(fromRow, fromCol, toRow, toCol)
            "white_queen", "black_queen" -> isValidQueenMove(fromRow, fromCol, toRow, toCol)
            "white_king", "black_king" -> isValidKingMove(fromRow, fromCol, toRow, toCol)
            else -> false
        }

        if (!basicValid) return false
        return simulateMoveAndCheckKing(piece, fromRow, fromCol, toRow, toCol)
    }

    private fun makeAIMove() {
        val opponentColor = if (currentPlayerColor == "white") "black" else "white"
        val possibleMoves = mutableListOf<Move>()

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val piece = boardState[row][col]
                if (piece != null && piece.startsWith(opponentColor)) {
                    for (toRow in 0 until boardSize) {
                        for (toCol in 0 until boardSize) {
                            if (isValidMove(piece, row, col, toRow, toCol)) {
                                possibleMoves.add(Move(row, col, toRow, toCol))
                            }
                        }
                    }
                }
            }
        }

        if (possibleMoves.isNotEmpty()) {
            val randomMove = possibleMoves.random()
            performMove(randomMove.fromRow, randomMove.fromCol, randomMove.toRow, randomMove.toCol)
        } else {
            Toast.makeText(this, "У ИИ нет доступных ходов", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAvailableMovesForPlayer(player: String): List<List<Int>> {
        val moves = mutableListOf<List<Int>>()
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val piece = boardState[row][col]
                if (piece?.startsWith(player) == true) {
                    for (toRow in 0 until boardSize) {
                        for (toCol in 0 until boardSize) {
                            if (isValidMove(piece, row, col, toRow, toCol)) {
                                moves.add(listOf(row, col, toRow, toCol))
                            }
                        }
                    }
                }
            }
        }
        return moves
    }
    private fun handleComputerMove() {
        if (currentPlayer == "black" && intent.getStringExtra("player") == "comp") {
            val availableMoves = getAvailableMovesForPlayer(currentPlayer)
            if (availableMoves.isNotEmpty()) {
                val (fromRow, fromCol, toRow, toCol) = availableMoves.random()
                boardState[toRow][toCol] = boardState[fromRow][fromCol]
                boardState[fromRow][fromCol] = null
                updateBoard()
                switchPlayer()
            } else {
                Toast.makeText(this, "Нет доступных ходов для ИИ", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun performMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        boardState[toRow][toCol] = boardState[fromRow][fromCol]
        boardState[fromRow][fromCol] = null
        updateBoard()
        switchPlayer()
    }

    data class Move(val fromRow: Int, val fromCol: Int, val toRow: Int, val toCol: Int)



    private fun isValidPawnMove(piece: String?, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {

        val adjustedDirection = when {
            currentPlayerColor == "white" && piece == "white_pawn" -> -1  // Белая пешка движется вверх
            currentPlayerColor == "black" && piece == "black_pawn" -> -1   // Черная пешка движется вниз
            currentPlayerColor == "white" && piece == "black_pawn" -> 1   // Черная пешка для белого игрока движется вниз
            currentPlayerColor == "black" && piece == "white_pawn" -> 1  // Белая пешка для черного игрока движется вверх
            else -> 0
        }

        if (fromCol == toCol && boardState[toRow][toCol] == null && toRow - fromRow == adjustedDirection) {
            return true
        }

        if (fromCol == toCol && boardState[toRow][toCol] == null) {
            if (currentPlayerColor == "white" && piece == "white_pawn" && fromRow == 6 && toRow - fromRow == -2 && boardState[fromRow - 1][fromCol] == null) {
                return true
            }
            if (currentPlayerColor == "white" && piece == "black_pawn" && fromRow == 1 && toRow - fromRow == 2 && boardState[fromRow + 1][fromCol] == null) {
                return true
            }
            if (currentPlayerColor == "black" && piece == "black_pawn" && fromRow == 6 && toRow - fromRow == -2 && boardState[fromRow - 1][fromCol] == null) {
                return true
            }
            if (currentPlayerColor == "black" && piece == "white_pawn" && fromRow == 1 && toRow - fromRow == 2 && boardState[fromRow + 1][fromCol] == null) {
                return true
            }
        }

        // Атака по диагонали
        if (Math.abs(toCol - fromCol) == 1 && toRow - fromRow == adjustedDirection &&
            boardState[toRow][toCol] != null && !isSameColor(piece, boardState[toRow][toCol]!!)) {
            return true
        }

        return false
    }


    private fun isSameColor(piece: String?, targetPiece: String): Boolean {
        return (piece?.startsWith("white") == true && targetPiece.startsWith("white")) ||
                (piece?.startsWith("black") == true && targetPiece.startsWith("black"))
    }

    private fun isValidRookMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        if (fromRow != toRow && fromCol != toCol) return false
        val piece = boardState[fromRow][fromCol]
        val targetPiece = boardState[toRow][toCol]
        if (targetPiece != null && isSameColor(piece, targetPiece)) {
            return false
        }

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
        if (fromRow == toRow || fromCol == toCol) {
            val piece = boardState[fromRow][fromCol]
            val targetPiece = boardState[toRow][toCol]
            if (targetPiece != null && isSameColor(piece, targetPiece)) {
                return false
            }

            return isPathClear(fromRow, fromCol, toRow, toCol)
        }
        if (Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol)) {
            val piece = boardState[fromRow][fromCol]
            val targetPiece = boardState[toRow][toCol]
            if (targetPiece != null && isSameColor(piece, targetPiece)) {
                return false
            }

            // Проверка пути (диагональ)
            return isPathClear(fromRow, fromCol, toRow, toCol)
        }

        return false
    }

    private fun isValidKingMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val rowDiff = Math.abs(fromRow - toRow)
        val colDiff = Math.abs(fromCol - toCol)
        if (rowDiff > 1 || colDiff > 1) {
            return false
        }
        val piece = boardState[fromRow][fromCol]
        val targetPiece = boardState[toRow][toCol]
        if (targetPiece != null && isSameColor(piece, targetPiece)) {
            return false
        }
        if (isUnderAttack(toRow, toCol, piece)) {
            return false
        }

        return true
    }


    private fun isUnderAttack(row: Int, col: Int, piece: String?): Boolean {
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val targetPiece = boardState[r][c]
                if (targetPiece != null && !isSameColor(piece, targetPiece)) {
                    if (canAttack(targetPiece, r, c, row, col)) {
                        return true
                    }
                }
            }
        }
        return false
    }
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

    private fun canPawnAttack(piece: String, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val direction = if (piece == "white_pawn") 1 else -1
        return Math.abs(fromCol - toCol) == 1 && toRow - fromRow == direction &&
                boardState[toRow][toCol] != null && !isSameColor(piece, boardState[toRow][toCol]!!)
    }





    private fun isPathClear(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        if (fromRow == toRow) {
            val start = Math.min(fromCol, toCol) + 1
            val end = Math.max(fromCol, toCol)
            for (col in start until end) {
                if (boardState[fromRow][col] != null) return false
            }
        } else if (fromCol == toCol) {
            val start = Math.min(fromRow, toRow) + 1
            val end = Math.max(fromRow, toRow)
            for (row in start until end) {
                if (boardState[row][fromCol] != null) return false
            }
        } else {
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
    private fun isCheckmate(currentColor: String): Boolean {
        // Перебираем все фигуры текущего цвета и все возможные ходы.
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val piece = boardState[row][col]
                if (piece != null && piece.startsWith(currentColor)) {
                    // Перебираем все клетки доски
                    for (toRow in 0 until boardSize) {
                        for (toCol in 0 until boardSize) {
                            if (isValidMove(piece, row, col, toRow, toCol)) {
                                // Если найдётся хотя бы один допустимый ход,
                                // король не в мате
                                return false
                            }
                        }
                    }
                }
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
                cell.setImageResource(0)
                boardState[row][col]?.let { piece ->
                    cell.setImageResource(pieces[piece]!!)
                }
            }
        }
    }

    private fun setupTimer(timeLimit: String) {
        this.timeLimit = timeLimit

        if (timeLimit == "without") {
            Toast.makeText(this, "Игра без ограничения по времени", Toast.LENGTH_SHORT).show()
            timerView.visibility = View.GONE
            timerView2.visibility = View.GONE
            val totalTimeMillis = 9999999999L
            whiteTimeMillis = totalTimeMillis
            blackTimeMillis = totalTimeMillis

            return
        }

        val totalTimeMillis = timeLimit.toLongOrNull()?.times(60 * 1000)
        if (totalTimeMillis == null || totalTimeMillis <= 0) {
            Toast.makeText(this, "Некорректное время", Toast.LENGTH_SHORT).show()
            return
        }

        if (whiteTimeMillis == 0L) whiteTimeMillis = totalTimeMillis
        if (blackTimeMillis == 0L) blackTimeMillis = totalTimeMillis

        whiteTimer = object : CountDownTimer(whiteTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                whiteTimeMillis = millisUntilFinished
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                timerView.text = timeString
            }

            override fun onFinish() {
                if (timeLimit != "without") {
                    Toast.makeText(this@CrazyChess, "Время вышло! Побеждают Черные", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        blackTimer = object : CountDownTimer(blackTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                blackTimeMillis = millisUntilFinished
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                timerView2.text = timeString
            }

            override fun onFinish() {
                if (timeLimit != "without") {
                    Toast.makeText(this@CrazyChess, "Время вышло! Побеждают Белые", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        startTimerForCurrentPlayer()
    }

    private fun startTimerForCurrentPlayer() {
        if (currentPlayer == "white" && !isWhiteTimerRunning) {
            whiteTimer = object : CountDownTimer(whiteTimeMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    whiteTimeMillis = millisUntilFinished
                    val minutes = millisUntilFinished / 1000 / 60
                    val seconds = millisUntilFinished / 1000 % 60
                    val timeString = String.format("%02d:%02d", minutes, seconds)
                    timerView.text = timeString
                }

                override fun onFinish() {
                    if (timeLimit != "without") {
                        Toast.makeText(
                            this@CrazyChess,
                            "Время вышло! Побеждают Черные",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
            whiteTimer.start()
            isWhiteTimerRunning = true
            if (isBlackTimerRunning) {
                blackTimer.cancel()
                isBlackTimerRunning = false
            }
        } else if (currentPlayer == "black" && !isBlackTimerRunning) {
            blackTimer = object : CountDownTimer(blackTimeMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    blackTimeMillis = millisUntilFinished
                    val minutes = millisUntilFinished / 1000 / 60
                    val seconds = millisUntilFinished / 1000 % 60
                    val timeString = String.format("%02d:%02d", minutes, seconds)
                    timerView2.text = timeString
                }

                override fun onFinish() {
                    if (timeLimit != "without") {
                        Toast.makeText(
                            this@CrazyChess,
                            "Время вышло! Побеждают Белые",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
            blackTimer.start()
            isBlackTimerRunning = true
            if (isWhiteTimerRunning) {
                whiteTimer.cancel()
                isWhiteTimerRunning = false
            }
        }
    }


    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
