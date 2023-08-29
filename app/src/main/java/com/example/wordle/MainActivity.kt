package com.example.wordle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    private lateinit var wordleGame: WordleGame
    private lateinit var hiddenWordTextView: TextView
    private lateinit var guessEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var newGameButton: Button
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hiddenWordTextView = findViewById(R.id.hiddenWordTextView)
        guessEditText = findViewById(R.id.guessEditText)
        submitButton = findViewById(R.id.submitButton)
        newGameButton = findViewById(R.id.newGameButton)
        statusTextView = findViewById(R.id.statusTextView)

        val wordList = resources.getStringArray(R.array.word_list).toList()
        wordleGame = WordleGame(wordList)
        startNewGame()

        submitButton.setOnClickListener {
            val guess = guessEditText.text.toString()
            val result = wordleGame.checkGuess(guess)

            when (result) {
                is WordleResult.Correct -> {
                    statusTextView.text = "Congratulations! You guessed the word!"
                    submitButton.isEnabled = false
                    newGameButton.isEnabled = true
                }
                is WordleResult.Incorrect -> {
                    statusTextView.text = "Correct: ${result.correctPositions}, Incorrect: ${result.incorrectPositions}"
                }
            }

            if (wordleGame.getAttemptsLeft() == 0) {
                statusTextView.text = "Out of attempts. The word was ${wordleGame.getHiddenWordLength()} letters long."
                submitButton.isEnabled = false
                newGameButton.isEnabled = true
            }
        }

        newGameButton.setOnClickListener {
            startNewGame()
        }
    }

    private fun startNewGame() {
        wordleGame.startNewGame()
        hiddenWordTextView.text = "Hidden Word: ${"*".repeat(wordleGame.getHiddenWordLength())}"
        guessEditText.text.clear()
        statusTextView.text = ""
        submitButton.isEnabled = true
        newGameButton.isEnabled = false
    }
}
class WordleGame(private val wordList: List<String>) {
    private val maxAttempts = 6
    private var hiddenWord = ""
    private var attemptsLeft = maxAttempts

    fun startNewGame() {
        hiddenWord = wordList.random().toUpperCase()
        attemptsLeft = maxAttempts
    }

    fun checkGuess(guess: String): WordleResult {
        val guessUpperCase = guess.toUpperCase()

        if (guessUpperCase == hiddenWord) {
            return  WordleResult.Correct
        }

        val correctPositions = hiddenWord.zip(guessUpperCase).count { (hiddenChar, guessedChar) ->
            hiddenChar == guessedChar
        }

        val incorrectPositions = guessUpperCase.count { it in hiddenWord } - correctPositions

        attemptsLeft--

        return WordleResult.Incorrect(correctPositions, incorrectPositions)
    }

    fun getAttemptsLeft(): Int {
        return attemptsLeft
    }

    fun getHiddenWordLength(): Int {
        return hiddenWord.length
    }
}

sealed class WordleResult {
    object Correct : WordleResult()
    data class Incorrect(val correctPositions: Int, val incorrectPositions: Int) : WordleResult()
}

