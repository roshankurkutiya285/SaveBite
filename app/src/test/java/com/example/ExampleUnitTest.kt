package com.example

import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import com.example.util.JwtManager
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
}

