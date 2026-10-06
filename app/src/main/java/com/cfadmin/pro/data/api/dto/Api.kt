package com.cfadmin.pro.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CfResponse<T>(
    val success: Boolean = false,
    val errors: List<CfError> = emptyList(),
    val messages: List<CfMessage> = emptyList(),
    val result: T? = null,
    @SerialName("result_info") val resultInfo: ResultInfo? = null
)

@Serializable
data class CfError(
    val code: Int = 0,
    val message: String = ""
)

@Serializable
data class CfMessage(
    val code: Int = 0,
    val message: String = ""
)

@Serializable
data class ResultInfo(
    val page: Int = 1,
    @SerialName("per_page") val perPage: Int = 20,
    val count: Int = 0,
    @SerialName("total_count") val totalCount: Int = 0
)

@Serializable
data class TokenVerifyResult(
    val id: String = "",
    val status: String = ""
)

@Serializable
data class Account(
    val id: String = "",
    val name: String = "",
    val type: String = "standard"
)
