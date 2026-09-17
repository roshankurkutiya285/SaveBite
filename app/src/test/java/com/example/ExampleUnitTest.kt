package com.example

import com.example.data.model.UserRole
import com.example.util.PasswordHasher
import com.example.util.formatRupees
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testUserRolesDefined() {
    val roles = UserRole.entries
    assertTrue(roles.contains(UserRole.CUSTOMER))
    assertTrue(roles.contains(UserRole.BAKERY))
    assertTrue(roles.contains(UserRole.PICKUP_AGENT))
    assertTrue(roles.contains(UserRole.ADMIN))
    assertTrue(roles.contains(UserRole.NGO))
  }

  @Test
  fun testFormatRupees() {
    val formatted = formatRupees(150.0)
    assertTrue(formatted.contains("150"))
  }

  @Test
  fun testPbkdf2PasswordHashing() {
    val password = "StrongPassword@123"
    val hash = PasswordHasher.hashPassword(password)

    assertTrue("Hash should contain pbkdf2 prefix", hash.startsWith("pbkdf2:10000:"))
    assertTrue("Verification should succeed with correct password", PasswordHasher.verifyPassword(password, hash))
    assertFalse("Verification should fail with wrong password", PasswordHasher.verifyPassword("WrongPass@999", hash))
  }

  @Test
  fun testEmailOtpGenerationAndVerification() {
    val email = "roshankurkutiya285@gmail.com"
    val res = com.example.util.EmailOtpManager.dispatchOtp(email)
    assertTrue("Dispatch should succeed", res.isSuccess)
    val info = res.getOrNull()
    assertNotNull("Info should not be null", info)
    val code = checkNotNull(info?.code)
    assertEquals(6, code.length)

    // Retrieval of active code
    val activeCode = com.example.util.EmailOtpManager.getActiveCodeForEmail(email)
    assertEquals(code, activeCode)

    // Verification with code
    val verifyRes = com.example.util.EmailOtpManager.verifyOtp(email, code)
    assertTrue("Verification should succeed with generated code", verifyRes.isSuccess)
  }
}

