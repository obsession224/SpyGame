package com.example.spygame

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SetupScreen(vm: GameViewModel, onStart: () -> Unit) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showLocationsDialog by remember { mutableStateOf(false) }
    var newLocationText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок с логотипом
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val textSize = 48.sp
            val iconHeight = 86.dp
            Text(text = "Spy", fontSize = textSize)
            Spacer(modifier = Modifier.width(0.dp))
            Image(
                painter = painterResource(id = R.mipmap.logo),
                contentDescription = "App Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(iconHeight)
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Game", fontSize = textSize)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Введите имена игроков:", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(vm.playerNames) { idx, _ ->
                var isFocused by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = vm.playerNames[idx],
                        onValueChange = { vm.setPlayerName(idx, it) },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { focusState -> isFocused = focusState.isFocused },
                        placeholder = { Text("Имя игрока") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = if (isFocused) Color.White else Color.Gray,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            backgroundColor = MaterialTheme.colors.surface,
                            cursorColor = Color.White,
                            textColor = Color.White,
                            placeholderColor = Color.White.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions.Default
                    )

                    if (vm.playerNames.size > 3) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { vm.removePlayer(idx) },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF757575),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("Удалить")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FloatingActionButton(
                        onClick = { vm.addPlayer() },
                        backgroundColor = Color(0xFFBDBDBD),
                        contentColor = Color.Black
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопка "Локации"
        Button(
            onClick = { showLocationsDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF757575),
                contentColor = Color.White
            )
        ) {
            Text("ЛОКАЦИИ")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка "Сбросить список локаций"
        Button(
            onClick = { showConfirmDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF757575),
                contentColor = Color.White
            )
        ) {
            Text("СБРОСИТЬ СПИСОК ЛОКАЦИЙ")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFBDBDBD),
                contentColor = Color.Black
            )
        ) {
            Text("ДАЛЕЕ")
        }
    }

    // Диалог подтверждения сброса
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Подтвердите сброс") },
            text = { Text("Вы уверены, что хотите сбросить список уже выпавших локаций?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetUsedLocations()
                    showConfirmDialog = false
                }) { Text("ДА") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("НЕТ") }
            }
        )
    }

    // Диалог редактирования локаций
    if (showLocationsDialog) {
        AlertDialog(
            onDismissRequest = { showLocationsDialog = false },
            title = { Text("Редактировать локации") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val locations = remember { mutableStateListOf<String>().apply { addAll(vm.getAllLocations()) } }

                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(locations) { loc ->
                            var included by remember { mutableStateOf(!vm.isLocationUsed(loc)) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = included,
                                    onCheckedChange = {
                                        included = it
                                        if (it) vm.includeLocation(loc)
                                        else vm.excludeLocation(loc)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(loc)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = newLocationText,
                            onValueChange = { newLocationText = it },
                            placeholder = { Text("Локация") },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions.Default
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newLocationText.isNotBlank()) {
                                    vm.addCustomLocation(newLocationText)
                                    locations.add(newLocationText) // сразу отображаем в списке
                                    newLocationText = ""
                                }
                            },
                            modifier = Modifier.height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFBDBDBD),
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Добавить")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationsDialog = false }) {
                    Text("ГОТОВО")
                }
            }
        )
    }
}
