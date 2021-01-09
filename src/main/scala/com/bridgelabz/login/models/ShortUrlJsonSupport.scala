package com.bridgelabz.login.models

import com.bridgelabz.login.Routes.{IntJsonFormat, StringJsonFormat, jsonFormat2}
import spray.json.RootJsonFormat

trait ShortUrlJsonSupport {
  implicit val urlFormat: RootJsonFormat[ShortUrl] = jsonFormat2(ShortUrl)
}
