package com.example.util

import android.content.Context
import android.util.Base64
import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Standard JWT Payload Claims.
 */
data class JwtPayload(
    val sub: String,       // User ID
    val email: String,
    val name: String,
    val role: UserRole,
    val iss: String = "savebite.in",
    val iat: Long,         // Issued at (seconds)
    val exp: Long          // Expiration time (seconds)
) {
    fun isExpired(): Boolean {
        val nowSec = System.currentTimeMillis() / 1000
        return nowSec >= exp
    }
}

data class JwtToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long,
    val tokenType: String = "Bearer",
    val payload: JwtPayload
)

/**
 * Cryptographic JSON Web Token (JWT) generator and validator using HMAC-SHA256 (HS256).
 */
object JwtManager {

    // Cryptographic secret key for signing SaveBite tokens
    private const val JWT_SECRET = "savebite_jwt_secret_key_prod_2026_india_secure_auth_token_signature"
    private const val ALGORITHM = "HmacSHA256"
    private const val ISSUER = "savebite.in"
    private const val ACCESS_TOKEN_VALIDITY_SECONDS = 30L * 24L * 60L * 60L // 30 days
    private const val REFRESH_TOKEN_VALIDITY_SECONDS = 90L * 24L * 60L * 60L // 90 days

    /**
     * Generates a signed JWT access and refresh token pair for a user.
     */
    fun generateToken(user: UserEntity): JwtToken {
        val nowSec = System.currentTimeMillis() / 1000
        val expSec = nowSec + ACCESS_TOKEN_VALIDITY_SECONDS

        val payload = JwtPayload(
            sub = user.id,
            email = user.email,
            name = user.name,
            role = user.role,
            iss = ISSUER,
            iat = nowSec,
            exp = expSec
        )

        val headerJson = JSONObject().apply {
            put("alg", "HS256")
            put("typ", "JWT")
        }.toString()

        val payloadJson = JSONObject().apply {
            put("sub", payload.sub)
            put("email", payload.email)
            put("name", payload.name)
            put("role", payload.role.name)
            put("iss", payload.iss)
            put("iat", payload.iat)
            put("exp", payload.exp)
        }.toString()

        val encodedHeader = base64UrlEncode(headerJson.toByteArray(StandardCharsets.UTF_8))
        val encodedPayload = base64UrlEncode(payloadJson.toByteArray(StandardCharsets.UTF_8))
        val dataToSign = "$encodedHeader.$encodedPayload"
        val signature = signHmacSha256(dataToSign, JWT_SECRET)
        val accessToken = "$dataToSign.$signature"

        // Generate Refresh Token
        val refreshPayloadJson = JSONObject().apply {
            put("sub", user.id)
            put("type", "refresh")
            put("iat", nowSec)
            put("exp", nowSec + REFRESH_TOKEN_VALIDITY_SECONDS)
        }.toString()
        val encodedRefreshPayload = base64UrlEncode(refreshPayloadJson.toByteArray(StandardCharsets.UTF_8))
        val refreshDataToSign = "$encodedHeader.$encodedRefreshPayload"
        val refreshSignature = signHmacSha256(refreshDataToSign, JWT_SECRET)
        val refreshToken = "$refreshDataToSign.$refreshSignature"

        return JwtToken(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresInSeconds = ACCESS_TOKEN_VALIDITY_SECONDS,
            tokenType = "Bearer",
            payload = payload
        )
    }

    /**
     * Verifies the cryptographic signature and expiration of a JWT token.
     */
    fun verifyToken(token: String): Result<JwtPayload> {
        val parts = token.trim().split(".")
        if (parts.size != 3) {
            return Result.failure(IllegalArgumentException("Invalid JWT token format. Must contain 3 parts."))
        }

        val encodedHeader = parts[0]
        val encodedPayload = parts[1]
        val signature = parts[2]

        // Verify cryptographic signature
        val dataToSign = "$encodedHeader.$encodedPayload"
        val expectedSignature = signHmacSha256(dataToSign, JWT_SECRET)
        if (signature != expectedSignature) {
            return Result.failure(SecurityException("Invalid JWT signature. Token may have been altered."))
        }

        // Decode payload
        return try {
            val payloadBytes = base64UrlDecode(encodedPayload)
            val json = JSONObject(String(payloadBytes, StandardCharsets.UTF_8))

            val exp = json.optLong("exp", 0L)
            val nowSec = System.currentTimeMillis() / 1000
            if (exp > 0 && nowSec > exp) {
                return Result.failure(SecurityException("JWT token has expired."))
            }

            val roleStr = json.optString("role", UserRole.CUSTOMER.name)
            val role = try {
                UserRole.valueOf(roleStr)
            } catch (e: Exception) {
                UserRole.CUSTOMER
            }

            val payload = JwtPayload(
                sub = json.getString("sub"),
                email = json.optString("email", ""),
                name = json.optString("name", "User"),
                role = role,
                iss = json.optString("iss", ISSUER),
                iat = json.optLong("iat", nowSec),
                exp = exp
            )
            Result.success(payload)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Failed to parse JWT claims: ${e.message}", e))
        }
    }

    private fun signHmacSha256(data: String, secret: String): String {
        val mac = Mac.getInstance(ALGORITHM)
        val secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), ALGORITHM)
        mac.init(secretKey)
        val signedBytes = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return base64UrlEncode(signedBytes)
    }

    private fun base64UrlEncode(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun base64UrlDecode(str: String): ByteArray {
        return Base64.decode(str, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}

/**
 * Manages JWT Token persistence on device using SharedPreferences.
 */
class JwtSessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("savebite_jwt_auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: JwtToken) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, token.accessToken)
            .putString(KEY_REFRESH_TOKEN, token.refreshToken)
            .putLong(KEY_EXPIRES_AT, token.payload.exp)
            .apply()
    }

    fun getAccessToken(): String? {
        val token = prefs.getString(KEY_ACCESS_TOKEN, null) ?: return null
        // Validate if still valid
        val verification = JwtManager.verifyToken(token)
        return if (verification.isSuccess) token else null
    }

    fun getVerifiedPayload(): JwtPayload? {
        val token = prefs.getString(KEY_ACCESS_TOKEN, null) ?: return null
        return JwtManager.verifyToken(token).getOrNull()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "jwt_access_token"
        private const val KEY_REFRESH_TOKEN = "jwt_refresh_token"
        private const val KEY_EXPIRES_AT = "jwt_expires_at"
    }
}
