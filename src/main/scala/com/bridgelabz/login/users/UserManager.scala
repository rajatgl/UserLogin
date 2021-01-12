package com.bridgelabz.login.users

import akka.http.scaladsl.server.Directives.complete
import com.bridgelabz.login.Routes.executor
import com.bridgelabz.login.database.DatabaseUtils
import com.bridgelabz.login.jwt.TokenManager
import com.bridgelabz.login.models.User
import courier.{Envelope, Mailer, Text}
import javax.mail.internet.InternetAddress

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

/**
 * Created on 1/8/2021.
 * Class: UserManager.scala
 * Author: Rajat G.L.
 */
object UserManager {
  /**
   *
   * @param user instance to be logged in
   * @return status code of login operation
   */
  def userLogin(user: User): Int = {

    val users = Await.result(DatabaseUtils.getUsers(user.email), 60.seconds)
    users.foreach(user =>
      if (user.password.equals(user.password)) {
        if(!user.verificationComplete)
          return 400
        return 200
      }
    )
    404
  }

  /**
   *
   * @param user instance to be saved into the database
   * @return status code of createNewUser operation
   */
  def createNewUser(user: User): Int = {
    val statusMessage = DatabaseUtils.saveUser(user)
    statusMessage match {
      case "Saving User Failed" => 410
      case "Validation Failed" => 414
      case _ => 215
    }
  }

  /**
   *
   * @param user to be verified
   * @return status message
   */
  def sendVerificationEmail(user: User): String = {
    val token: String = TokenManager.generateToken(user.email)
    val longUrl = "http://localhost:9000/verify?token=" + token + "&email=" + user.email
    val shortIndex = DatabaseUtils.shortUrl(longUrl)

    val mailer = Mailer("smtp.gmail.com", 587)
      .auth(true)
      .as(System.getenv("SENDER_EMAIL"),System.getenv("SENDER_PASSWORD"))
      .startTls(true)()
    mailer(Envelope.from(new InternetAddress(System.getenv("SENDER_EMAIL")))
      .to(new InternetAddress(user.email))
      .subject("Token")
      .content(Text("Click on this link to verify your email address:  http://localhost:9000/" + shortIndex + ". Happy to serve you!")))
      .onComplete {
        case Success(_) => "Message delivered. Email verified!"
        case Failure(_) => "Failed to verify user!"
      }
    "Verification link sent!"
  }
}
