package com.bridgelabz.login

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, concat, entity, extractUri, get, handleExceptions, parameters, path, post, redirect}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.bridgelabz.login.database.DatabaseUtils
import com.bridgelabz.login.models.{LoginRequest, LoginRequestJsonSupport, User, UserJsonSupport}
import com.bridgelabz.login.users.UserManager
import com.nimbusds.jose.JWSObject

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}
import akka.http.scaladsl.model._


object Routes extends App with UserJsonSupport with LoginRequestJsonSupport {
  val host = System.getenv("Host")
  val port = System.getenv("Port").toInt

  //maintains a pool of actors
  implicit val system: ActorSystem = ActorSystem("AS")
  //maintains and executes actor system
  implicit val executor: ExecutionContext = system.dispatcher

  val exceptionHandler = ExceptionHandler {
    case _: ArithmeticException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(400, entity = "Number could not be parsed. Is there a text were a number should be?"))
      }
    case _: NullPointerException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(402, entity = "Null value found while parsing the data. Contact the admin."))
      }
    case _: Exception =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(408, entity = "Some error occured. Please try again later."))
      }
  }

  def route: Route = {
    handleExceptions(exceptionHandler) {
      concat(
        post {
          concat(
            path("login") {
              entity(Directives.as[LoginRequest]) { request =>
                val user: User = User(request.email, request.password, verificationComplete = false)
                val userLoginStatus: Int = UserManager.userLogin(user)
                if (userLoginStatus == 200) {
                  complete {
                    HttpResponse(StatusCodes.OK, entity = "Logged in successfully. Happy to serve you!")
                  }
                }
                else if (userLoginStatus == 404) {
                  complete {
                    HttpResponse(StatusCodes.NotFound, entity = "Login failed. Your account does not seem to exist. If you did not register yet, head to: http://localhost:9000/register")
                  }
                }
                else {
                  complete {
                    HttpResponse(StatusCodes.Unauthorized, entity = "Login failed. Your account is not verified. Head to http://localhost:9000/verify for the same.")
                  }
                }
              }
            },
            path("register") {
              entity(Directives.as[LoginRequest]) { request =>
                val user: User = User(request.email, request.password, verificationComplete = false)
                val userRegisterStatus: Int = UserManager.createNewUser(user)
                if (userRegisterStatus == 215) {
                  complete {
                    HttpResponse(StatusCodes.OK, entity = UserManager.sendVerificationEmail(user))
                  }
                }
                else if (userRegisterStatus == 414) {
                  complete {
                    HttpResponse(StatusCodes.BadRequest, entity = "Bad email, try again with a valid entry.")
                  }
                }
                else {
                  complete {
                    HttpResponse(StatusCodes.Conflict, entity = "User registration failed.")
                  }
                }
              }
            }
          )
        },
        get {
          concat(
            path("verify") {
              parameters('token.as[String], 'email.as[String]) { (token, email) =>
                val jwsObject = JWSObject.parse(token)
                val updateUserAsVerified = DatabaseUtils.verifyEmail(email)
                Await.result(updateUserAsVerified, 60.seconds)
                if (jwsObject.getPayload.toJSONObject.get("email").equals(email)) {
                  complete {
                    HttpResponse(StatusCodes.OK, entity = "User successfully verified and registered!")
                  }
                }
                else {
                  complete {
                    HttpResponse(StatusCodes.Unauthorized, entity = "User could not be verified!")
                  }
                }
              }
            },
            path(Directives.IntNumber){int =>
              val longUrl = DatabaseUtils.longUrl(int)
              val urls = Await.result(longUrl, 60.seconds)
              redirect(urls.head.url, StatusCodes.TemporaryRedirect)
            }
          )
        }
      )
    }
  }

  val binder = Http().newServerAt(host, port).bind(route)
  binder.onComplete {
    case Success(serverBinding) => println(println(s"Listening to ${serverBinding.localAddress}"))
    case Failure(error) => println(s"Error : ${error.getMessage}")
  }
}