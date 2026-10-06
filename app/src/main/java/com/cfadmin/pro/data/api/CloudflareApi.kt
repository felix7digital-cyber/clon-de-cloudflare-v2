package com.cfadmin.pro.data.api

import com.cfadmin.pro.data.api.dto.Account
import com.cfadmin.pro.data.api.dto.CfResponse
import com.cfadmin.pro.data.api.dto.CreateD1Request
import com.cfadmin.pro.data.api.dto.CreateKvRequest
import com.cfadmin.pro.data.api.dto.CreatePagesProjectRequest
import com.cfadmin.pro.data.api.dto.CreateR2Request
import com.cfadmin.pro.data.api.dto.D1Database
import com.cfadmin.pro.data.api.dto.KvNamespace
import com.cfadmin.pro.data.api.dto.PagesProject
import com.cfadmin.pro.data.api.dto.R2BucketsResult
import com.cfadmin.pro.data.api.dto.TokenVerifyResult
import com.cfadmin.pro.data.api.dto.UpdatePagesProjectRequest
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CloudflareApi {

    @GET("user/tokens/verify")
    suspend fun verifyToken(): CfResponse<TokenVerifyResult>

    @GET("accounts")
    suspend fun listAccounts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): CfResponse<List<Account>>

    @GET("accounts/{accountId}/pages/projects")
    suspend fun listPagesProjects(
        @Path("accountId") accountId: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100
    ): CfResponse<List<PagesProject>>

    @GET("accounts/{accountId}/pages/projects/{projectName}")
    suspend fun getPagesProject(
        @Path("accountId") accountId: String,
        @Path("projectName") projectName: String
    ): CfResponse<PagesProject>

    @POST("accounts/{accountId}/pages/projects")
    suspend fun createPagesProject(
        @Path("accountId") accountId: String,
        @Body body: CreatePagesProjectRequest
    ): CfResponse<PagesProject>

    @PATCH("accounts/{accountId}/pages/projects/{projectName}")
    suspend fun updatePagesProject(
        @Path("accountId") accountId: String,
        @Path("projectName") projectName: String,
        @Body body: UpdatePagesProjectRequest
    ): CfResponse<PagesProject>

    @DELETE("accounts/{accountId}/pages/projects/{projectName}")
    suspend fun deletePagesProject(
        @Path("accountId") accountId: String,
        @Path("projectName") projectName: String
    ): CfResponse<JsonElement>

    @GET("accounts/{accountId}/d1/database")
    suspend fun listD1Databases(
        @Path("accountId") accountId: String
    ): CfResponse<List<D1Database>>

    @POST("accounts/{accountId}/d1/database")
    suspend fun createD1Database(
        @Path("accountId") accountId: String,
        @Body body: CreateD1Request
    ): CfResponse<D1Database>

    @GET("accounts/{accountId}/r2/buckets")
    suspend fun listR2Buckets(
        @Path("accountId") accountId: String
    ): CfResponse<R2BucketsResult>

    @POST("accounts/{accountId}/r2/buckets")
    suspend fun createR2Bucket(
        @Path("accountId") accountId: String,
        @Body body: CreateR2Request
    ): CfResponse<JsonElement>

    @GET("accounts/{accountId}/storage/kv/namespaces")
    suspend fun listKvNamespaces(
        @Path("accountId") accountId: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100
    ): CfResponse<List<KvNamespace>>

    @POST("accounts/{accountId}/storage/kv/namespaces")
    suspend fun createKvNamespace(
        @Path("accountId") accountId: String,
        @Body body: CreateKvRequest
    ): CfResponse<KvNamespace>
}
