package com.example.spygame.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.R
import androidx.compose.material.Text

@Composable
fun SharedHeader(
    modifier: Modifier = Modifier,
    textSizeSp: Int = 42,
    iconSizeDp: Int = 72
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text("Spy", fontSize = textSizeSp.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Image(
            painter = painterResource(id = R.mipmap.logo),
            contentDescription = "logo",
            modifier = Modifier.size(iconSizeDp.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("Game", fontSize = textSizeSp.sp)
    }
}
