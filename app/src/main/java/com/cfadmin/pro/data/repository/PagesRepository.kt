package com.cfadmin.pro.data.repository

import com.cfadmin.pro.data.api.dto.CreateD1Request
import com.cfadmin.pro.data.api.dto.CreateKvRequest
import com.cfadmin.pro.data.api.dto.CreatePagesProjectRequest
import com.cfadmin.pro.data.api.dto.CreateR2Request
import com.cfadmin.pro.data.api.dto.D1Binding
import com.cfadmin.pro.data.api.dto.D1Database
import com.cfadmin.pro.data.api.dto.DeploymentConfigs
import com.cfadmin.pro.data.api.dto.EnvironmentConfig
import com.cfadmin.pro.data.api.dto.KvBinding
import com.cfadmin.pro.data.api.dto.KvNamespace
import com.cfadmin.pro.data.api.dto.PagesProject
import com.cfadmin.pro.data.api.dto.R2Binding
import com.cfadmin.pro.data.api.dto.R2Bucket
import com.cfadmin.pro.data.api.dto.UpdatePagesProjectRequest
import com.cfadmin.pro.data.auth.AuthRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import retrofit2.HttpException

private val errorJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private suspend fun <T> safeCall(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: HttpException) {
        val body = try { e.response()?.errorBody()?.string() } catch (x: Exception) { null }
        val msg = if (body != null) {
            try {
                val parsed = errorJson.decodeFromString<com.cfadmin.pro.data.api.dto.CfResponse<JsonElement>>(body)
                val cfMsg = parsed.errors.firstOrNull()?.message
                if (!cfMsg.isNullOrBlank()) {
                    "CF: $cfMsg (HTTP ${e.code()})"
                } else {
                    "HTTP ${e.code()}: ${body.take(300)}"
                }
            } catch (x: Exception) {
                "HTTP ${e.code()}: ${body.take(300)}"
            }
        } else {
            "HTTP ${e.code()}"
        }
        throw Exception(msg)
    }
}

class PagesRepository(private val auth: AuthRepository) {

    private val api get() = auth.api

    suspend fun listProjects(accountId: String): List<PagesProject> = safeCall {
        val response = api.listPagesProjects(accountId)
        if (!response.success) {
            throw Exception(response.errors.firstOrNull()?.message ?: "Error listando proyectos")
        }
        response.result ?: emptyList()
    }

    suspend fun getProject(accountId: String, name: String): PagesProject = safeCall {
        val response = api.getPagesProject(accountId, name)
        if (!response.success || response.result == null) {
            throw Exception(response.errors.firstOrNull()?.message ?: "Proyecto no encontrado")
        }
        response.result!!
    }

    suspend fun createProject(
        accountId: String,
        name: String,
        productionBranch: String
    ): PagesProject = safeCall {
        val response = api.createPagesProject(
            accountId,
            CreatePagesProjectRequest(name = name, productionBranch = productionBranch)
        )
        if (!response.success || response.result == null) {
            throw Exception(response.errors.firstOrNull()?.message ?: "Error creando proyecto")
        }
        response.result!!
    }

    suspend fun deleteProject(accountId: String, name: String): Unit = safeCall {
        val response = api.deletePagesProject(accountId, name)
        if (!response.success) {
            throw Exception(response.errors.firstOrNull()?.message ?: "Error eliminando proyecto")
        }
    }

    suspend fun listD1(accountId: String): List<D1Database> = safeCall {
        val r = api.listD1Databases(accountId)
        r.result ?: emptyList()
    }

    suspend fun createD1(accountId: String, name: String): D1Database = safeCall {
        val r = api.createD1Database(accountId, CreateD1Request(name))
        if (!r.success || r.result == null) {
            throw Exception(r.errors.firstOrNull()?.message ?: "Error creando D1")
        }
        r.result!!
    }

    suspend fun listR2(accountId: String): List<R2Bucket> = safeCall {
        val r = api.listR2Buckets(accountId)
        r.result?.buckets ?: emptyList()
    }

    suspend fun createR2(accountId: String, name: String): Unit = safeCall {
        val r = api.createR2Bucket(accountId, CreateR2Request(name))
        if (!r.success) {
            throw Exception(r.errors.firstOrNull()?.message ?: "Error creando R2")
        }
    }

    suspend fun listKv(accountId: String): List<KvNamespace> = safeCall {
        val r = api.listKvNamespaces(accountId)
        r.result ?: emptyList()
    }

    suspend fun createKv(accountId: String, title: String): KvNamespace = safeCall {
        val r = api.createKvNamespace(accountId, CreateKvRequest(title))
        if (!r.success || r.result == null) {
            throw Exception(r.errors.firstOrNull()?.message ?: "Error creando KV")
        }
        r.result!!
    }

    suspend fun addBindings(
        accountId: String,
        projectName: String,
        d1BindingName: String?,
        d1Id: String?,
        r2BindingName: String?,
        r2BucketName: String?,
        kvBindingName: String?,
        kvId: String?
    ): PagesProject = safeCall {
        val current = getProject(accountId, projectName)
        val prod = current.deploymentConfigs.production

        val newD1 = prod.d1Databases.toMutableMap()
        val newR2 = prod.r2Buckets.toMutableMap()
        val newKv = prod.kvNamespaces.toMutableMap()

        if (d1BindingName != null && d1Id != null) {
            newD1[d1BindingName] = D1Binding(d1Id)
        }
        if (r2BindingName != null && r2BucketName != null) {
            newR2[r2BindingName] = R2Binding(r2BucketName)
        }
        if (kvBindingName != null && kvId != null) {
            newKv[kvBindingName] = KvBinding(kvId)
        }

        val newEnv = EnvironmentConfig(
            d1Databases = newD1,
            r2Buckets = newR2,
            kvNamespaces = newKv,
            compatibilityDate = prod.compatibilityDate,
            compatibilityFlags = prod.compatibilityFlags
        )

        val newConfigs = DeploymentConfigs(
            production = newEnv,
            preview = current.deploymentConfigs.preview
        )

        val response = api.updatePagesProject(
            accountId,
            projectName,
            UpdatePagesProjectRequest(newConfigs)
        )
        if (!response.success || response.result == null) {
            throw Exception(response.errors.firstOrNull()?.message ?: "Error actualizando bindings")
        }
        response.result!!
    }
}