package com.cfadmin.pro.ui.screens.pages

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cfadmin.pro.ui.components.Badge
import com.cfadmin.pro.ui.components.CfCard
import com.cfadmin.pro.ui.components.Chip
import com.cfadmin.pro.ui.theme.CfCyan
import com.cfadmin.pro.ui.theme.CfEmerald
import com.cfadmin.pro.ui.theme.CfOrange
import com.cfadmin.pro.ui.theme.CfPurple
import com.cfadmin.pro.ui.theme.CfRed
import com.cfadmin.pro.ui.theme.CfYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagesDetailScreen(
    projectName: String,
    onBack: () -> Unit,
    vm: PagesViewModel = viewModel()
) {
    val state by vm.detailState.collectAsState()
    var showAddD1 by remember { mutableStateOf(false) }
    var showAddR2 by remember { mutableStateOf(false) }
    var showAddKv by remember { mutableStateOf(false) }

    LaunchedEffect(projectName) {
        vm.loadDetail(projectName)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(projectName, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { vm.loadDetail(projectName) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CfOrange)
                    }
                }
                state.error != null && state.project == null -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.error ?: "", color = CfRed)
                    }
                }
                state.project != null -> {
                    val project = state.project!!
                    val prod = project.deploymentConfigs.production

                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (state.infoMessage != null) {
                            Surface(
                                color = CfEmerald.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    state.infoMessage ?: "",
                                    color = CfEmerald,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        if (state.error != null) {
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

                        CfCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Badge("Pages", CfOrange)
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(project.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        project.subdomain,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Chip("id: ${project.id.take(8)}")
                                Chip("rama: ${project.productionBranch}")
                            }
                            if (project.createdOn.isNotBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Creado: ${project.createdOn}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        BindingSection(
                            title = "D1 Databases",
                            subtitle = "${prod.d1Databases.size} binding(s)",
                            accent = CfCyan,
                            onAdd = { showAddD1 = true },
                            busy = state.busy,
                            entries = prod.d1Databases.map { (name, bind) -> name to bind.id }
                        )

                        BindingSection(
                            title = "R2 Buckets",
                            subtitle = "${prod.r2Buckets.size} binding(s)",
                            accent = CfPurple,
                            onAdd = { showAddR2 = true },
                            busy = state.busy,
                            entries = prod.r2Buckets.map { (name, bind) -> name to bind.name }
                        )

                        BindingSection(
                            title = "KV Namespaces",
                            subtitle = "${prod.kvNamespaces.size} binding(s)",
                            accent = CfYellow,
                            onAdd = { showAddKv = true },
                            busy = state.busy,
                            entries = prod.kvNamespaces.map { (name, bind) -> name to bind.id }
                        )
                    }
                }
            }
        }
    }

    if (showAddD1) {
        AddBindingDialog(
            title = "Vincular D1 Database",
            nameLabel = "Nombre de la base de datos",
            bindingLabel = "Nombre del binding (ej. DB)",
            defaultBinding = "DB",
            onDismiss = { showAddD1 = false },
            onConfirm = { name, binding, cb ->
                vm.addD1Binding(projectName, name, binding) { ok ->
                    if (ok) showAddD1 = false
                    cb(ok)
                }
            }
        )
    }
    if (showAddR2) {
        AddBindingDialog(
            title = "Vincular R2 Bucket",
            nameLabel = "Nombre del bucket",
            bindingLabel = "Nombre del binding (ej. BUCKET)",
            defaultBinding = "BUCKET",
            onDismiss = { showAddR2 = false },
            onConfirm = { name, binding, cb ->
                vm.addR2Binding(projectName, name, binding) { ok ->
                    if (ok) showAddR2 = false
                    cb(ok)
                }
            }
        )
    }
    if (showAddKv) {
        AddBindingDialog(
            title = "Vincular KV Namespace",
            nameLabel = "Titulo del namespace",
            bindingLabel = "Nombre del binding (ej. KV)",
            defaultBinding = "KV",
            onDismiss = { showAddKv = false },
            onConfirm = { name, binding, cb ->
                vm.addKvBinding(projectName, name, binding) { ok ->
                    if (ok) showAddKv = false
                    cb(ok)
                }
            }
        )
    }
}

@Composable
private fun BindingSection(
    title: String,
    subtitle: String,
    accent: Color,
    onAdd: () -> Unit,
    busy: Boolean,
    entries: List<Pair<String, String>>
) {
    CfCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onAdd,
                enabled = !busy,
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Anadir")
            }
        }
        if (entries.isEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Sin bindings configurados",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Spacer(Modifier.height(10.dp))
            entries.forEach { (name, value) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = accent,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(120.dp)
                    )
                    Text(
                        value,
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
private fun AddBindingDialog(
    title: String,
    nameLabel: String,
    bindingLabel: String,
    defaultBinding: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, (Boolean) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var binding by remember { mutableStateOf(defaultBinding) }
    var submitting by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!submitting) onDismiss() },
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(nameLabel) },
                    singleLine = true,
                    enabled = !submitting,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = binding,
                    onValueChange = { binding = it },
                    label = { Text(bindingLabel) },
                    singleLine = true,
                    enabled = !submitting,
                    modifier = Modifier.fillMaxWidth()
                )
                if (localError != null) {
                    Text(localError ?: "", color = CfRed, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !submitting && name.isNotBlank() && binding.isNotBlank(),
                onClick = {
                    submitting = true
                    localError = null
                    onConfirm(name.trim(), binding.trim()) { ok ->
                        submitting = false
                        if (!ok) localError = "No se pudo vincular. Revisa permisos."
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
                    Text("Vinculando...", color = CfOrange)
                } else {
                    Text("Vincular", color = CfOrange)
                }
            }
        },
        dismissButton = {
            TextButton(enabled = !submitting, onClick = onDismiss) { Text("Cancelar") }
        }
    )
}