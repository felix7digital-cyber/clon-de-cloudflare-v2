package com.cfadmin.pro.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class D1Database(
    val uuid: String = "",
    val name: String = "",
    @SerialName("created_at") val createdAt: String = "",
    val version: String = ""
)

@Serializable
data class CreateD1Request(val name: String)

@Serializable
data class R2BucketsResult(
    val buckets: List<R2Bucket> = emptyList()
)

@Serializable
data class R2Bucket(
    val name: String = "",
    @SerialName("creation_date") val creationDate: String = ""
)

@Serializable
data class CreateR2Request(val name: String)

@Serializable
data class KvNamespace(
    val id: String = "",
    val title: String = "",
    @SerialName("supports_url_encoding") val supportsUrlEncoding: Boolean = true
)

@Serializable
data class CreateKvRequest(val title: String)
