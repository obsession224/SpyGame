package com.example.spygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private val vm: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    AppNav(vm)
                }
            }
        }
    }
}

@Composable
fun AppNav(vm: GameViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "setup") {
        composable("setup") {
            SetupScreen(vm = vm, onStart = {
                vm.prepareGame()
                navController.navigate("reveal")
            })
        }
        composable("reveal") {
            RevealScreen(vm = vm, onFinished = { navController.navigate("vote") })
        }
        composable("vote") {
            VoteScreen(vm = vm, onCheck = {
                vm.evaluateVotes()
                navController.navigate("result")
            })
        }
        composable("result") {
            ResultScreen(vm = vm, onPlayAgain = {
                vm.restartGame() // <- перезапуск игры без удаления игроков
                navController.navigate("setup") {
                    popUpTo("setup") { inclusive = true }
                }
            })
        }
    }
}
