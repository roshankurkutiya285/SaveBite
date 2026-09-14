package com.example

import com.example.data.model.UserRole
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
    val roles = UserRole.values()
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
}
