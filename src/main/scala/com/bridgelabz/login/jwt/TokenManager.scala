package com.bridgelabz.login.jwt

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{complete, optionalHeaderValueByName, provide}
import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}

/**
 * Created on 1/8/2021.
 * Class: TokenManager.scala
 * Author: Rajat G.L.
 */
object TokenManager {

  private val secretKey = "a$iq!@oop"
  private val header = JwtHeader("HS256", "JWT")
  private val tokenExpiryPeriodInDays = 1

  /**
   *
   * @param email of user instance to generate a unique token
   * @return token
   */
  def generateToken(email: String): String = {

    val claimSet = JwtClaimsSet(
      Map(
        "email" -> email,
        "expiredAt" -> (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenExpiryPeriodInDays))
      )
    )
    JsonWebToken(header, claimSet, secretKey)
  }

  /**
   *
   * @return authentication status message
   */
  def authenticated: Directive1[Map[String, Any]] = {

    optionalHeaderValueByName("Authorization").flatMap { tokenFromUser =>

      val jwtToken = tokenFromUser.get.split(" ")
      jwtToken(1) match {
        case token if isTokenExpired(token) =>
          complete(StatusCodes.Unauthorized -> "Session expired.")

        case token if JsonWebToken.validate(token, secretKey) =>
          provide(getClaims(token))

        case _ =>  complete(StatusCodes.Unauthorized ->"Invalid Token")
      }
    }
  }

  /**
   *
   * @param token to check if its expired
   * @return boolean result of the same
   */
  private def isTokenExpired(token: String): Boolean =
    getClaims(token).get("expiredAt").exists(_.toLong < System.currentTimeMillis())

  /**
   *
   * @param token to be claimed
   * @return if all tokens claimed, return an empty map else return the tokens/claims remaining
   */
  private def getClaims(token: String): Map[String, String] =
    JsonWebToken.unapply(token) match {
      case Some(value) => value._2.asSimpleMap.getOrElse(Map.empty[String, String])
      case None => Map.empty[String, String]
    }
}
