package com.cfadmin.pro.ui.screens.pages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cfadmin.pro.CfAdminApplication
import com.cfadmin.pro.data.api.dto.D1Database
import com.cfadmin.pro.data.api.dto.KvNamespace
import com.cfadmin.pro.data.api.dto.PagesProject
import com.cfadmin.pro.data.api.dto.R2Bucket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PagesListState(
    val loading: Boolean = false,
    val projects: List<PagesProject> = emptyList(),
    val error: String? = null,
    val accountId: String? = null,
    val accountName: String? = null
)

data class PagesDetailState(
    val loading: Boolean = true,
    val project: PagesProject? = null,
    val d1List: List<D1Database> = emptyList(),
    val r2List: List<R2Bucket> = emptyList(),
    val kvList: List<KvNamespace> = emptyList(),
    val error: String? = null,
    val infoMessage: String? = null,
    val busy: Boolean = false
)

class PagesViewModel(app: Application) : AndroidViewModel(app) {

    private val container = (app as CfAdminApplication).container
    private val auth = container.authRepository
    private val repo = container.pagesRepository

    private val _listState = MutableStateFlow(PagesListState())
    val listState: StateFlow<PagesListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(PagesDetailState())
    val detailState: StateFlow<PagesDetailState> = _detailState.asStateFlow()

    private suspend fun ensureAccount(): String? {
        val cached = auth.getAccountId()
        if (cached != null) return cached
        return try {
            val r = auth.api.listAccounts()
            val first = r.result?.firstOrNull()
            if (first != null) {
                auth.saveAccount(first.id, first.name)
                _listState.update { it.copy(accountId = first.id, accountName = first.name) }
                first.id
            } else {
                _listState.update { it.copy(error = "No hay cuentas disponibles con este token") }
                null
            }
        } catch (e: Exception) {
            _listState.update { it.copy(error = e.message ?: "Error consultando cuentas") }
            null
        }
    }

    fun refreshProjects() {
        viewModelScope.launch {
            _listState.update { it.copy(loading = true, error = null) }
            try {
                val accountId = ensureAccount()
                if (accountId == null) {
                    _listState.update { it.copy(loading = false) }
                    return@launch
                }
                val projects = repo.listProjects(accountId)
                _listState.update {
                    it.copy(
                        loading = false,
                        projects = projects,
                        accountId = accountId,
                        accountName = auth.getAccountName(),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _listState.update {
                    it.copy(loading = false, error = e.message ?: "Error desconocido")
                }
            }
        }
    }

    fun createProject(name: String, branch: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _listState.update { it.copy(loading = true, error = null) }
            try {
                val accountId = ensureAccount() ?: run {
                    _listState.update { it.copy(loading = false) }
                    onDone(false)
                    return@launch
                }
                val created = repo.createProject(accountId, name.trim(), branch.trim())
                _listState.update {
                    it.copy(loading = false, projects = listOf(created) + it.projects)
                }
                onDone(true)
            } catch (e: Exception) {
                _listState.update {
                    it.copy(loading = false, error = e.message ?: "Error creando proyecto")
                }
                onDone(false)
            }
        }
    }

    fun deleteProject(name: String) {
        viewModelScope.launch {
            try {
                val accountId = ensureAccount() ?: return@launch
                repo.deleteProject(accountId, name)
                _listState.update { state ->
                    state.copy(projects = state.projects.filter { it.name != name })
                }
            } catch (e: Exception) {
                _listState.update { it.copy(error = e.message ?: "Error eliminando") }
            }
        }
    }

    fun loadDetail(projectName: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(loading = true, error = null, infoMessage = null) }
            try {
                val accountId = ensureAccount() ?: run {
                    _detailState.update { it.copy(loading = false) }
                    return@launch
                }
                val project = repo.getProject(accountId, projectName)
                val d1 = try { repo.listD1(accountId) } catch (e: Exception) { emptyList() }
                val r2 = try { repo.listR2(accountId) } catch (e: Exception) { emptyList() }
                val kv = try { repo.listKv(accountId) } catch (e: Exception) { emptyList() }
                _detailState.update {
                    it.copy(
                        loading = false,
                        project = project,
                        d1List = d1,
                        r2List = r2,
                        kvList = kv,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(loading = false, error = e.message ?: "Error cargando proyecto")
                }
            }
        }
    }

    fun addD1Binding(projectName: String, dbName: String, bindingName: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _detailState.update { it.copy(busy = true, error = null, infoMessage = null) }
            try {
                val accountId = ensureAccount() ?: run {
                    _detailState.update { it.copy(busy = false) }
                    onDone(false)
                    return@launch
                }
                val db = repo.createD1(accountId, dbName)
                val updated = repo.addBindings(
                    accountId = accountId,
                    projectName = projectName,
                    d1BindingName = bindingName,
                    d1Id = db.uuid,
                    r2BindingName = null,
                    r2BucketName = null,
                    kvBindingName = null,
                    kvId = null
                )
                _detailState.update {
                    it.copy(
                        busy = false,
                        project = updated,
                        infoMessage = "D1 '$dbName' vinculada como '$bindingName'"
                    )
                }
                onDone(true)
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(busy = false, error = e.message ?: "Error vinculando D1")
                }
                onDone(false)
            }
        }
    }

    fun addR2Binding(projectName: String, bucketName: String, bindingName: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _detailState.update { it.copy(busy = true, error = null, infoMessage = null) }
            try {
                val accountId = ensureAccount() ?: run {
                    _detailState.update { it.copy(busy = false) }
                    onDone(false)
                    return@launch
                }
                repo.createR2(accountId, bucketName)
                val updated = repo.addBindings(
                    accountId = accountId,
                    projectName = projectName,
                    d1BindingName = null,
                    d1Id = null,
                    r2BindingName = bindingName,
                    r2BucketName = bucketName,
                    kvBindingName = null,
                    kvId = null
                )
                _detailState.update {
                    it.copy(
                        busy = false,
                        project = updated,
                        infoMessage = "R2 '$bucketName' vinculado como '$bindingName'"
                    )
                }
                onDone(true)
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(busy = false, error = e.message ?: "Error vinculando R2")
                }
                onDone(false)
            }
        }
    }

    fun addKvBinding(projectName: String, title: String, bindingName: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _detailState.update { it.copy(busy = true, error = null, infoMessage = null) }
            try {
                val accountId = ensureAccount() ?: run {
                    _detailState.update { it.copy(busy = false) }
                    onDone(false)
                    return@launch
                }
                val ns = repo.createKv(accountId, title)
                val updated = repo.addBindings(
                    accountId = accountId,
                    projectName = projectName,
                    d1BindingName = null,
                    d1Id = null,
                    r2BindingName = null,
                    r2BucketName = null,
                    kvBindingName = bindingName,
                    kvId = ns.id
                )
                _detailState.update {
                    it.copy(
                        busy = false,
                        project = updated,
                        infoMessage = "KV '$title' vinculado como '$bindingName'"
                    )
                }
                onDone(true)
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(busy = false, error = e.message ?: "Error vinculando KV")
                }
                onDone(false)
            }
        }
    }
}
