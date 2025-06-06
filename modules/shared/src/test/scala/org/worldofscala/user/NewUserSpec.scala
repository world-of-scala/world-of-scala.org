package org.worldofscala.user

import zio.test.*
//import zio.test.Assertion.*

object NewUserSpec extends ZIOSpecDefault {
  override def spec = suite("NewUser email validation")(
    test("accepts valid email") {
      assertTrue(NewUser.isValidEmail("john.doe@example.com"))
    },
    test("rejects email without @") {
      assertTrue(!NewUser.isValidEmail("johndoe.example.com"))
    },
    test("rejects email without domain") {
      assertTrue(!NewUser.isValidEmail("john@"))
    },
    test("rejects email without username") {
      assertTrue(!NewUser.isValidEmail("@example.com"))
    },
    test("accepts email with subdomain") {
      assertTrue(NewUser.isValidEmail("john.doe@mail.example.com"))
    },
    test("rejects email with invalid TLD") {
      assertTrue(!NewUser.isValidEmail("john.doe@example.c"))
    }
  )
}
