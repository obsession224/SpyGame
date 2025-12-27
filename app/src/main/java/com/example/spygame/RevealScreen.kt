package com.example.spygame

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RevealScreen(vm: GameViewModel, onFinished: () -> Unit) {
    val idx = vm.currentRevealIndex
    val revealed = vm.revealedFlags.getOrNull(idx) ?: false
    val names = vm.playerNames

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Карточка игрока: ${names[idx]}",
            style = MaterialTheme.typography.h6
        )

        Spacer(modifier = Modifier.height(12.dp))

        val scale by animateFloatAsState(
            targetValue = if (revealed) 1.05f else 1f,
            animationSpec = tween(durationMillis = 300)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Фон карточки
                    Image(
                        painter = painterResource(id = R.mipmap.card_back),
                        contentDescription = "Card Back",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Плавное затемнение через alpha
                    val alpha by animateFloatAsState(if (revealed) 0.67f else 0f, tween(300))
                    if (revealed) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = alpha)),
                            contentAlignment = Alignment.Center
                        ) {
                            val isSpy = when (vm.specialRound) {
                                SpecialRound.AllSpies -> true
                                SpecialRound.NoSpy -> false
                                SpecialRound.None -> vm.spyIndex == idx
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isSpy) "ШПИОН" else "НЕ ШПИОН",
                                    style = MaterialTheme.typography.h4.copy(color = Color.White)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isSpy) "Ты — шпион! Старайся не выдать себя."
                                    else "Локация: ${vm.location}",
                                    style = MaterialTheme.typography.h6.copy(color = Color.White)
                                )
                            }
                        }
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val buttonColors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFFBDBDBD),
            contentColor = Color.Black
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = revealed,
                transitionSpec = { fadeIn(tween(250)) with fadeOut(tween(200)) }
            ) { isRevealed ->
                if (!isRevealed) {
                    Button(
                        onClick = { vm.revealCurrent() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = buttonColors
                    ) {
                        Text("ПОКАЗАТЬ РОЛЬ")
                    }
                } else {
                    Button(
                        onClick = {
                            vm.revealedFlags[idx] = false
                            vm.nextReveal(onFinished)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = buttonColors
                    ) {
                        Text("ДАЛЕЕ")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Игрок ${idx + 1} из ${names.size}")
    }
}
