package com.bridgelabz.login.database

import com.bridgelabz.login.models.User
import org.bson.codecs.configuration.{CodecProvider, CodecRegistries, CodecRegistry}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

/**
 * Created on 1/8/2021.
 * Class: DbConfig.scala
 * Author: Rajat G.L.
 */
object DatabaseConfig {

  val mongoClient: MongoClient = MongoClient()
  val databaseName: String = "UsersLogin"

  val codecProvider: CodecProvider = Macros.createCodecProvider[User]()
  val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(codecProvider),
    DEFAULT_CODEC_REGISTRY
  )

  val database: MongoDatabase = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
  val collectionName: String = "Users"
  val collection: MongoCollection[User] = database.getCollection(collectionName)
}
