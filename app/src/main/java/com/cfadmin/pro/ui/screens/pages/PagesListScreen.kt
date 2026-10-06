package com.cfadmin.pro.ui.screens.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cfadmin.pro.data.api.dto.PagesProject
import com.cfadmin.pro.ui.components.Badge
import com.cfadmin.pro.ui.components.CfCard
import com.cfadmin.pro.ui.components.Chip
import com.cfadmin.pro.ui.components.EmptyState
import com.cfadmin.pro.ui.components.SectionHeader
import com.cfadmin.pro.ui.theme.CfEmerald
import com.cfadmin.pro.ui.theme.CfOrange
import com.cfadmin.pro.ui.theme.CfRed

@Composable
fun PagesListScreen(
    onOpenProject: (String) -> Unit,
    vm: PagesViewModel = viewModel()
) {
    val state by vm.listState.collectAsState()
    var showCreate by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (state.projects.isEmpty() && !state.loading) {
            vm.refreshProjects()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CfCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionHeader(
                    "Cloudflare Pages",
                    state.accountName?.let { "Cuenta: $it" } ?: "Proyectos desplegados"
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { vm.refreshProjects() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                }
                Button(
                    onClick = { showCreate = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CfOrange)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Nuevo")
                }
            }
        }

        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            Surface(
                color = CfRed.copy(alpha = 0.1f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    state.error ?: "",
                    color = CfRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            state.loading && state.projects.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CfOrange)
                }
            }
            state.projects.isEmpty() -> {
                EmptyState("No tienes proyectos Pages todavia. Crea el primero con el boton Nuevo.")
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.projects, key = { it.id.ifBlank { it.name } }) { project ->
                        PageProjectCard(
                            project = project,
                            onOpen = { onOpenProject(project.name) },
                            onDelete = { deleteTarget = project.name }
                        )
                    }
                }
            }
        }
    }

    if (showCreate) {
        CreateProjectDialog(
            onDismiss = { showCreate = false },
            onCreate = { name, branch, cb ->
                vm.createProject(name, branch) { ok ->
                    if (ok) showCreate = false
                    cb(ok)
                }
            }
        )
    }

    deleteTarget?.let { name ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Eliminar proyecto") },
            text = { Text("Eliminar '$name'? Esta accion no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteProject(name)
                    deleteTarget = null
                }) { Text("Eliminar", color = CfRed) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun PageProjectCard(
    project: PagesProject,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val prod = project.deploymentConfigs.production
    val d1Count = prod.d1Databases.size
    val r2Count = prod.r2Buckets.size
    val kvCount = prod.kvNamespaces.size

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable { onOpen() }
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Badge("Pages", CfOrange)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(project.name, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        project.subdomain.ifBlank { "${project.name}.pages.dev" },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Chip("rama: ${project.productionBranch}")
                if (d1Count > 0) Chip("D1: $d1Count")
                if (r2Count > 0) Chip("R2: $r2Count")
                if (kvCount > 0) Chip("KV: $kvCount")
                if (d1Count + r2Count + kvCount == 0) {
                    Chip("sin bindings")
                }
            }

            project.latestDeployment?.let { dep ->
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge("Deployed", CfEmerald)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        dep.url.ifBlank { dep.id.take(12) },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, (Boolean) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("main") }
    var submitting by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!submitting) onDismiss() },
        title = { Text("Nuevo proyecto Pages") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre (minusculas, numeros, guiones)") },
                    singleLine = true,
                    enabled = !submitting,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = branch,
                    onValueChange = { branch = it },
                    label = { Text("Rama de produccion") },
                    singleLine = true,
                    enabled = !submitting,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )
                if (localError != null) {
                    Text(localError ?: "", color = CfRed, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !submitting && name.isNotBlank(),
                onClick = {
                    val clean = name.trim().lowercase().replace(Regex("[^a-z0-9-]"), "-")
                    if (clean.isBlank()) {
                        localError = "Nombre invalido"
                        return@TextButton
                    }
                    submitting = true
                    localError = null
                    onCreate(clean, branch.trim().ifBlank { "main" }) { ok ->
                        submitting = false
                        if (!ok) localError = "No se pudo crear. Revisa los permisos del token."
                    }
                }
            ) {
                if (submitting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp),
                        color = CfOrange
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Creando...", color = CfOrange)
                } else {
                    Text("Crear", color = CfOrange)
                }
            }
        },
        dismissButton = {
            TextButton(enabled = !submitting, onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
