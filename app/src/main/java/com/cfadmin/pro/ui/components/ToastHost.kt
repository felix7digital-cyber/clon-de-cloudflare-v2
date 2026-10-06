package com.cfadmin.pro.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class ToastType { Success, Warning, Error, Info }

data class ToastMessage(val id: Long, val text: String, val type: ToastType)

class ToastHostState {
    internal val toasts = mutableStateListOf<ToastMessage>()
    private var counter = 0L

    fun show(text: String, type: ToastType = ToastType.Info) {
        val msg = ToastMessage(id = counter++, text = text, type = type)
        toasts.add(msg)
    }
}

@Composable
fun ToastHost(state: ToastHostState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 96.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        state.toasts.forEach { msg ->
            key(msg.id) {
                LaunchedEffect(msg.id) {
                    delay(3000)
                    state.toasts.remove(msg)
                }
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(tween(200)),
                    exit = fadeOut(tween(200))
                ) {
                    ToastItem(msg)
                }
            }
        }
    }
}

@Composable
private fun ToastItem(msg: ToastMessage) {
    val icon = when (msg.type) {
        ToastType.Success -> Icons.Default.CheckCircle
        ToastType.Warning -> Icons.Default.Warning
        ToastType.Error -> Icons.Default.Error
        ToastType.Info -> Icons.Default.Info
    }
    val tint = when (msg.type) {
        ToastType.Success -> Color(0xFF10B981)
        ToastType.Warning -> Color(0xFFF59E0B)
        ToastType.Error -> Color(0xFFEF4444)
        ToastType.Info -> Color(0xFF60A5FA)
    }
    val borderColor = if (msg.type == ToastType.Info) Color.Transparent else tint.copy(alpha = 0.4f)
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(Color(0xFF1F2937), RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Text(text = msg.text, color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}
