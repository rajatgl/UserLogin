package com.bridgelabz.login.users

import com.bridgelabz.login.database.DatabaseUtils
import com.bridgelabz.login.models.User

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
 * Created on 1/8/2021.
 * Class: UserManager.scala
 * Author: Rajat G.L.
 */
object UserManager {
  /**
   *
   * @param user instance to be logged in
   * @return status message of login operation
   */
  def userLogin(user: User): String = {

    val users = Await.result(DatabaseUtils.getUsers(user.email), 60.seconds)
    users.foreach(user =>
      if (user.password.equals(user.password)) {
        if(!user.verificationComplete)
          return "User is not authorized"
        return "Login successful"
      }
    )
    "User not found"
  }
}
