package com.example.unscramble.ui

import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords   // ✅ correct import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class GameViewModel(
    private val wordsList: List<String> = allWords.toList()   // ✅ FIXED
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var currentWord: String = ""
    private val usedWords = mutableSetOf<String>()

    var userGuess: String = ""
        private set

    init {
        resetGame()
    }

    private fun pickRandomWord(): String {
        var newWord: String
        do {
            newWord = wordsList[Random.nextInt(wordsList.size)]   // ✅ CHANGED
        } while (usedWords.contains(newWord))
        return newWord
    }

    private fun shuffleWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        return String(tempWord)
    }

    private fun updateCurrentWord() {
        currentWord = pickRandomWord()
        usedWords.add(currentWord)   // ✅ ADD THIS LINE

        val scrambledWord = shuffleWord(currentWord)

        _uiState.value = _uiState.value.copy(
            currentScrambledWord = scrambledWord,
            currentWordCount = _uiState.value.currentWordCount + 1,
            isGuessedWordWrong = false
        )
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score + 20
            updateGameState(updatedScore)
        } else {
            _uiState.value = _uiState.value.copy(
                isGuessedWordWrong = true
            )
        }
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == wordsList.size) {   // ✅ CHANGED
            _uiState.value = _uiState.value.copy(
                isGuessedWordWrong = false,
                score = updatedScore,
                isGameOver = true
            )
        } else {
            updateCurrentWord()
            _uiState.value = _uiState.value.copy(
                isGuessedWordWrong = false,
                score = updatedScore
            )
        }
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState()
        updateCurrentWord()
    }
}