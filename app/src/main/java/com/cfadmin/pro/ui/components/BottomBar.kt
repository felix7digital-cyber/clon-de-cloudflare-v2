"app/src/main/java/com/cfadmin/pro/ui/components/BottomBar.kt": `package com.cfadmin.pro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cfadmin.pro.ui.nav.Section

@Composable
fun AppBottomBar(
    currentRoute: String,
    recent: List<String>,
    pinned: List<String>,
    onNavigate: (String) -> Unit
) {
    var dropdown by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.navigationBarsPadding()
    ) {
        if (dropdown != null) {
            DropdownPanel(
                title = if (dropdown == "recent") "Recientes" else "Fijadas",
                routes = if (dropdown == "recent") recent else pinned,
                emptyMessage = if (dropdown == "recent")
                    "Sin secciones recientes"
                else "Sin secciones fijadas",
                onNavigate = { onNavigate(it); dropdown = null }
            )
        }

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            BottomItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                selected = currentRoute == Section.OVERVIEW.route,
                onClick = { onNavigate(Section.OVERVIEW.route) }
            )
            BottomItem(
                icon = Icons.Default.History,
                label = "Recientes",
                selected = false,
                badge = recent.size.takeIf { it > 0 },
                onClick = { dropdown = if (dropdown == "recent") null else "recent" }
            )
            BottomItem(
                icon = Icons.Default.PushPin,
                label = "Fijadas",
                selected = false,
                badge = pinned.size.takeIf { it > 0 },
                onClick = { dropdown = if (dropdown == "pinned") null else "pinned" }
            )
            BottomItem(
                icon = Icons.Default.Key,
                label = "Tokens",
                selected = currentRoute == Section.TOKENS.route,
                onClick = { onNavigate(Section.TOKENS.route) }
            )
            BottomItem(
                icon = Icons.Default.Search,
                label = "Buscar",
                selected = false,
                onClick = { onNavigate(Section.OVERVIEW.route) }
            )
        }
    }
}

@Composable
private fun RowScope.BottomItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    badge: Int? = null,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Box {
                Icon(icon, contentDescription = label)
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-6).dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            badge.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
    )
}

@Composable
private fun DropdownPanel(
    title: String,
    routes: List<String>,
    emptyMessage: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
            if (routes.isEmpty()) {
                Text(
                    emptyMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            } else {
                routes.forEach { route ->
                    val section = Section.fromRoute(route)
                    if (section != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(route) }
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                section.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(section.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
`,