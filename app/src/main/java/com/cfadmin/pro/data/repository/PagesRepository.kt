import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import retrofit2.HttpException

private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

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
                    "HTTP ${e.code()}: ${body.take(200)}"
                }
            } catch (x: Exception) {
                "HTTP ${e.code()}: ${body.take(200)}"
            }
        } else {
            "HTTP ${e.code()}"
        }
        throw Exception(msg)
    }
}