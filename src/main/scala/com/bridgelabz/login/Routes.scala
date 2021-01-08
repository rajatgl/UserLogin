package com.bridgelabz.login

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives.{complete, concat, extractUri, get, handleExceptions, path}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.bridgelabz.login.models.UserJsonSupport

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Routes extends App with UserJsonSupport {
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
        get {
          path("/") {
            complete("Site under development.")
          }
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