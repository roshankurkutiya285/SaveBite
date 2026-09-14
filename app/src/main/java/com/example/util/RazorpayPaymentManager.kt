package com.example.util

import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

enum class RazorpayPaymentMethod(val displayName: String, val iconEmoji: String) {
    UPI("UPI / QR (Google Pay, PhonePe, Paytm)", "⚡"),
    CARD("Debit / Credit Card (Visa, RuPay, MC)", "💳"),
    NETBANKING("NetBanking (SBI, HDFC, ICICI, Axis)", "🏦"),
    WALLET("Wallets (Paytm, PhonePe, Amazon Pay)", "👛")
}

data class RazorpayPaymentOrder(
    val orderId: String,
    val amountPaise: Long,
    val amountRupees: Double,
    val currency: String = "INR",
    val description: String,
    val keyId: String = "rzp_test_SaveBite2026",
    val packageTitle: String,
    val merchantName: String,
    val quantity: Int
)

sealed class RazorpayPaymentResult {
    data class Success(
        val paymentId: String,
        val orderId: String,
        val signature: String,
        val method: RazorpayPaymentMethod,
        val amountRupees: Double,
        val timestamp: Long = System.currentTimeMillis()
    ) : RazorpayPaymentResult()

    data class Failure(
        val errorCode: Int,
        val description: String
    ) : RazorpayPaymentResult()
}

/**
 * Razorpay Payment Gateway integration utility for Indian Rupee surplus food rescue transactions.
 */
object RazorpayPaymentManager {

    const val RAZORPAY_TEST_KEY_ID = "rzp_test_SaveBite2026"
    private const val RAZORPAY_TEST_SECRET = "rzp_secret_savebite_surplus_india_key_2026"

    /**
     * Prepares a Razorpay order entity with amount in paise (1 INR = 100 paise).
     */
    fun createOrder(
        packageTitle: String,
        merchantName: String,
        quantity: Int,
        amountRupees: Double
    ): RazorpayPaymentOrder {
        val randomSuffix = UUID.randomUUID().toString().replace("-", "").take(12)
        val orderId = "order_sb_$randomSuffix"
        val amountPaise = (amountRupees * 100).toLong().coerceAtLeast(100L)

        return RazorpayPaymentOrder(
            orderId = orderId,
            amountPaise = amountPaise,
            amountRupees = amountRupees,
            currency = "INR",
            description = "SaveBite Surplus Rescue: $quantity x $packageTitle from $merchantName",
            keyId = RAZORPAY_TEST_KEY_ID,
            packageTitle = packageTitle,
            merchantName = merchantName,
            quantity = quantity
        )
    }

    /**
     * Simulates authentic Razorpay payment processing and signature generation.
     */
    fun processPayment(
        order: RazorpayPaymentOrder,
        method: RazorpayPaymentMethod,
        simulateSuccess: Boolean = true
    ): RazorpayPaymentResult {
        if (!simulateSuccess) {
            return RazorpayPaymentResult.Failure(
                errorCode = 400,
                description = "Transaction was declined by bank or cancelled by user."
            )
        }

        val randomHex = UUID.randomUUID().toString().replace("-", "").take(14)
        val paymentId = "pay_$randomHex"
        val dataToSign = "${order.orderId}|$paymentId"
        val signature = generateSignature(dataToSign, RAZORPAY_TEST_SECRET)

        return RazorpayPaymentResult.Success(
            paymentId = paymentId,
            orderId = order.orderId,
            signature = signature,
            method = method,
            amountRupees = order.amountRupees
        )
    }

    /**
     * Verifies the Razorpay payment signature according to official Razorpay verification protocol.
     */
    fun verifySignature(orderId: String, paymentId: String, signature: String): Boolean {
        val dataToSign = "$orderId|$paymentId"
        val expected = generateSignature(dataToSign, RAZORPAY_TEST_SECRET)
        return signature == expected
    }

    private fun generateSignature(data: String, secret: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        mac.init(secretKey)
        val hash = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
