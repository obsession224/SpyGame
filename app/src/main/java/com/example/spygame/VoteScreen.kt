package com.example.spygame

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun VoteScreen(vm: GameViewModel, onCheck: () -> Unit) {
    val names = vm.playerNames
    var selected by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "ВЫЧИСЛИТЕ ШПИОНА",
            style = MaterialTheme.typography.h6
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(end = 4.dp)
        ) {
            names.forEachIndexed { idx, name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = idx }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected == idx,
                        onClick = { selected = idx },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,       // выбранный
                            unselectedColor = Color.White.copy(alpha = 0.6f) // невыбранный
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = name)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            listOf(
                -2 to "Все шпионы",
                -3 to "Шпиона не обнаружено"
            ).forEach { (value, text) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = value }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected == value,
                        onClick = { selected = value },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.White.copy(alpha = 0.6f)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = text)
                }
            }
        }

        Button(
            onClick = {
                vm.submitVoteChoice(selected)
                onCheck()
            },
            enabled = selected != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFBDBDBD),
                contentColor = Color.Black
            )
        ) {
            Text("ПРОВЕРИТЬ")
        }
    }
}
