package com.bridgelabz.login.database

import com.bridgelabz.login.models.User
import org.mongodb.scala.model.Filters.equal

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

/**
 * Created on 1/8/2021.
 * Class: DatabaseUtils.scala
 * Author: Rajat G.L.
 */
object DatabaseUtils {
  /**
   *
   * @param user to be inserted into the database
   * @return status message of the insertion operation
   */
  def saveUser(user:User): String = {
    val emailRegex = "^[a-zA-Z0-9+-._]+@[a-zA-Z0-9.-]+$"
    if(user.email.matches(emailRegex)){
      val ifUserExists: Boolean = checkIfExists(user.email)
      if(ifUserExists)
      {
        "Saving User Failed"
      }
      else
      {
        val future = DatabaseConfig.collection.insertOne(user).toFuture()
        Await.result(future,10.seconds)
        "Saved User Successfully"
      }
    }
    else {
      "Validation Failed"
    }
  }

  /**
   *
   * @param email to be checked for existence within the database
   * @return boolean result of check operation
   */
  def checkIfExists(email : String): Boolean = {
    val data = Await.result(getUsers,10.seconds)
    data.foreach(user => if(user.email.equalsIgnoreCase(email)) return true)
    false
  }

  /**
   *
   * @return all user instances in the database
   */
  def getUsers: Future[Seq[User]] = {
    DatabaseConfig.collection.find().toFuture()
  }

  /**
   *
   * @param email to filter out the users associated with the given email
   * @return user instances in the database associated with the given email
   */
  def getUsers(email : String): Future[Seq[User]] = {
    DatabaseConfig.collection.find(equal("email",email)).toFuture()
  }
}
