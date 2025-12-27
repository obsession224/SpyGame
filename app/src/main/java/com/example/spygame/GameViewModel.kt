package com.example.spygame

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("SpyGamePrefs", Context.MODE_PRIVATE)

    var playerNames = mutableStateListOf<String>()
        private set

    var spyIndex by mutableStateOf<Int?>(null)
        private set

    var location by mutableStateOf<String?>(null)
        private set

    var currentRevealIndex by mutableStateOf(0)
        private set

    var revealedFlags = mutableStateListOf<Boolean>()
        private set

    var specialRound by mutableStateOf(SpecialRound.None)
        private set

    var voteChoice by mutableStateOf<Int?>(null)
        private set

    var resultText by mutableStateOf("")
        private set

    var spyWasFound by mutableStateOf(false)
        private set

    private var usedLocations = mutableSetOf<String>()
    private var customLocations = mutableListOf<String>()

    init {
        loadSavedData()
    }

    /** Загружаем сохранённых игроков, использованные и пользовательские локации */
    private fun loadSavedData() {
        val savedNames = prefs.getStringSet("playerNames", null)
        if (!savedNames.isNullOrEmpty()) {
            playerNames.addAll(savedNames)
        } else {
            playerNames.addAll(listOf("", "", ""))
        }

        usedLocations = prefs.getStringSet("usedLocations", emptySet())!!.toMutableSet()
        customLocations = prefs.getStringSet("customLocations", emptySet())!!.toMutableList()
    }

    /** Сохраняем игроков, использованные и пользовательские локации */
    private fun saveData() {
        prefs.edit()
            .putStringSet("playerNames", playerNames.toSet())
            .putStringSet("usedLocations", usedLocations)
            .putStringSet("customLocations", customLocations.toSet())
            .apply()
    }

    /** Загружает локации из assets/locations.txt */
    private fun loadLocations(): List<String> {
        return try {
            getApplication<Application>().assets.open("locations.txt").bufferedReader().useLines { lines ->
                lines.filter { it.isNotBlank() }.toList()
            }
        } catch (e: Exception) {
            listOf("Локация 1", "Локация 2") // если файл не найден
        }
    }

    // -------------------- Методы для диалога локаций --------------------

    fun getAllLocations(): List<String> {
        return (loadLocations() + customLocations).distinct()
    }

    fun isLocationUsed(loc: String): Boolean {
        return usedLocations.contains(loc)
    }

    fun includeLocation(loc: String) {
        usedLocations.remove(loc)
        saveData()
    }

    fun excludeLocation(loc: String) {
        usedLocations.add(loc)
        saveData()
    }

    fun addCustomLocation(loc: String) {
        if (loc.isNotBlank() && !customLocations.contains(loc)) {
            customLocations.add(loc)
            includeLocation(loc) // новая локация доступна по умолчанию
            saveData()
        }
    }

    // -------------------- Игровая логика --------------------

    fun prepareGame() {
        val names = playerNames.map { it.trim() }.filter { it.isNotEmpty() }
        playerNames.clear()
        if (names.size < 3) {
            for (i in 0..2) playerNames.add(if (i < names.size) names[i] else "Игрок ${i + 1}")
        } else {
            playerNames.addAll(names)
        }

        val allLocations = getAllLocations()
        val availableLocations = allLocations.filterNot { usedLocations.contains(it) }

        location = if (availableLocations.isEmpty()) {
            usedLocations.clear()
            allLocations.random()
        } else {
            availableLocations.random()
        }

        usedLocations.add(location!!)
        saveData()

        currentRevealIndex = 0
        revealedFlags.clear()
        revealedFlags.addAll(List(playerNames.size) { false })
        voteChoice = null
        resultText = ""
        spyWasFound = false

        val specialRoundHappens = Random.nextInt(100) < 10
        if (specialRoundHappens) {
            specialRound = if (Random.nextBoolean()) SpecialRound.AllSpies else SpecialRound.NoSpy
            spyIndex = null
        } else {
            specialRound = SpecialRound.None
            spyIndex = Random.nextInt(playerNames.size)
        }
    }

    fun restartGame() = prepareGame()

    fun addPlayer() {
        if (playerNames.size < 20) {
            playerNames.add("")
            revealedFlags.add(false)
            saveData()
        }
    }

    fun removePlayer(index: Int) {
        if (playerNames.size <= 3) return
        playerNames.removeAt(index)
        if (revealedFlags.size > index) revealedFlags.removeAt(index)
        if (currentRevealIndex >= playerNames.size) currentRevealIndex = playerNames.size - 1
        else if (currentRevealIndex > index) currentRevealIndex--
        saveData()
    }

    fun setPlayerName(index: Int, name: String) {
        playerNames[index] = name
        saveData()
    }

    fun revealCurrent() {
        revealedFlags[currentRevealIndex] = true
    }

    fun nextReveal(onFinished: () -> Unit) {
        if (currentRevealIndex < playerNames.size - 1) currentRevealIndex++
        else onFinished()
    }

    fun submitVoteChoice(choice: Int?) {
        voteChoice = choice
    }

    fun evaluateVotes() {
        val spy = spyIndex
        spyWasFound = when (specialRound) {
            SpecialRound.AllSpies -> voteChoice == -2
            SpecialRound.NoSpy -> voteChoice == -3
            SpecialRound.None -> voteChoice != null && voteChoice == spy
        }
        resultText = if (spyWasFound) "ПОБЕДА! ШПИОН ВЫЧИСЛЕН." else "ШПИОН ОСТАЛСЯ НЕ РАСКРЫТ!"
    }

    fun resetUsedLocations() {
        usedLocations.clear()
        saveData()
    }

    fun resetAll() {
        prefs.edit().clear().apply()
        playerNames.clear()
        playerNames.addAll(listOf("", "", ""))
        spyIndex = null
        location = null
        currentRevealIndex = 0
        revealedFlags.clear()
        revealedFlags.addAll(List(playerNames.size) { false })
        specialRound = SpecialRound.None
        voteChoice = null
        resultText = ""
        spyWasFound = false
        usedLocations.clear()
        customLocations.clear()
    }
}

enum class SpecialRound { None, AllSpies, NoSpy }
