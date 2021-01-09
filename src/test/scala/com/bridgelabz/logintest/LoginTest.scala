package com.bridgelabz.logintest

import akka.http.scaladsl.model.StatusCodes.Conflict
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCode, StatusCodes}
import akka.util.ByteString
import com.bridgelabz.login.Routes
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

class LoginTest extends AnyWordSpec with should.Matchers with ScalatestRouteTest{
  "A Router" should {
    "register successfully" in {
      val jsonRequest = ByteString(
        s"""
           |{
           |    "email":"rajat@gmail.com",
           |    "password":"test"
           |}
              """.stripMargin)
      Post("/register", HttpEntity(MediaTypes.`application/json`,jsonRequest)) ~> Routes.route ~> check
      {
        status shouldBe StatusCodes.OK shouldBe Conflict
      }
    }
    "login should fail" in {
      val jsonRequest = ByteString(
        s"""
           |{
           |    "email":"rakshit@google.com",
           |    "password":"test"
           |}
              """.stripMargin)
      Post("/login", HttpEntity(MediaTypes.`application/json`,jsonRequest)) ~> Routes.route ~> check
      {
        status shouldBe StatusCodes.Unauthorized
      }
    }
  }

}
