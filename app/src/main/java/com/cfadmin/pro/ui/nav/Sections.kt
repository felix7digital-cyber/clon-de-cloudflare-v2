package com.cfadmin.pro.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.ui.graphics.vector.ImageVector

enum class Section(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    OVERVIEW("overview", "Overview", Icons.Filled.Dashboard),
    PAGES("pages", "Pages", Icons.Filled.Layers),
    DNS("dns", "DNS", Icons.Filled.Dns),
    TOKENS("tokens", "API Tokens", Icons.Filled.Key),
    WORKERS("workers", "Workers", Icons.Filled.Bolt),
    D1("d1", "D1 Database", Icons.Filled.Storage),
    R2("r2", "R2 Storage", Icons.Filled.Archive),
    KV("kv", "KV Namespace", Icons.Outlined.Cloud),
    SPEED("speed", "Speed & Cache", Icons.Filled.Speed),
    SECURITY("security", "Security & WAF", Icons.Filled.Security);

    companion object {
        fun fromRoute(route: String?): Section? =
            values().firstOrNull { it.route == route }
    }
}
