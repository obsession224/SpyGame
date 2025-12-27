package com.example.spygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue


@Composable
fun ResultScreen(vm: GameViewModel, onPlayAgain: () -> Unit) {
    val spyName = vm.spyIndex?.let { vm.playerNames.getOrNull(it) } ?: "—"

    // Локальное состояние, чтобы зафиксировать ресурс карточки
    val resultImageRes by remember {
        mutableStateOf(
            if (vm.resultText.contains("Победа", true)) R.mipmap.result_victory
            else R.mipmap.result_defeat
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "РЕЗУЛЬТАТ",
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = resultImageRes),
                contentDescription = "Result",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Шпион: $spyName", style = MaterialTheme.typography.body1)
        if (vm.specialRound != SpecialRound.None) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Спец. раунд: ${
                    when (vm.specialRound) {
                        SpecialRound.AllSpies -> "Все шпионы"
                        SpecialRound.NoSpy -> "Шпиона нет"
                        else -> ""
                    }
                }",
                style = MaterialTheme.typography.body2
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                vm.restartGame()
                onPlayAgain()
            },
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFBDBDBD),
                contentColor = Color.Black
            )
        ) {
            Text("ИГРАТЬ СНОВА")
        }
    }
}


