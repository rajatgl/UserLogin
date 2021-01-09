package com.bridgelabz.login.models

import com.bridgelabz.login.Routes.{StringJsonFormat, jsonFormat2}
import spray.json.RootJsonFormat

trait LoginRequestJsonSupport {
  implicit val loginFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest)
}
