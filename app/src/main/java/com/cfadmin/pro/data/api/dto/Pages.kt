package com.cfadmin.pro.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagesProject(
    val id: String = "",
    val name: String = "",
    val subdomain: String = "",
    val domains: List<String> = emptyList(),
    @SerialName("production_branch") val productionBranch: String = "main",
    @SerialName("created_on") val createdOn: String = "",
    @SerialName("deployment_configs") val deploymentConfigs: DeploymentConfigs = DeploymentConfigs(),
    @SerialName("latest_deployment") val latestDeployment: LatestDeployment? = null,
    @SerialName("canonical_deployment") val canonicalDeployment: LatestDeployment? = null
)

@Serializable
data class DeploymentConfigs(
    val production: EnvironmentConfig = EnvironmentConfig(),
    val preview: EnvironmentConfig = EnvironmentConfig()
)

@Serializable
data class EnvironmentConfig(
    @SerialName("d1_databases") val d1Databases: Map<String, D1Binding> = emptyMap(),
    @SerialName("r2_buckets") val r2Buckets: Map<String, R2Binding> = emptyMap(),
    @SerialName("kv_namespaces") val kvNamespaces: Map<String, KvBinding> = emptyMap(),
    @SerialName("compatibility_date") val compatibilityDate: String? = null,
    @SerialName("compatibility_flags") val compatibilityFlags: List<String> = emptyList()
)

@Serializable
data class D1Binding(val id: String = "")

@Serializable
data class R2Binding(val name: String = "")

@Serializable
data class KvBinding(val id: String = "")

@Serializable
data class LatestDeployment(
    val id: String = "",
    val url: String = "",
    val environment: String = "",
    @SerialName("created_on") val createdOn: String = ""
)

@Serializable
data class CreatePagesProjectRequest(
    val name: String,
    @SerialName("production_branch") val productionBranch: String = "main"
)

@Serializable
data class UpdatePagesProjectRequest(
    @SerialName("deployment_configs") val deploymentConfigs: DeploymentConfigs
)
