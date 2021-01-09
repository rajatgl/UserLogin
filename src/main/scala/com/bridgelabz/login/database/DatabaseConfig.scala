package com.bridgelabz.login.database

import com.bridgelabz.login.models.{ShortUrl, User}
import org.bson.codecs.configuration.{CodecProvider, CodecRegistries, CodecRegistry}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

/**
 * Created on 1/8/2021.
 * Class: DbConfig.scala
 * Author: Rajat G.L.
 */
protected object DatabaseConfig {

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

  val codecProviderForUrl: CodecProvider = Macros.createCodecProvider[ShortUrl]()
  val codecRegistryForUrl: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(codecProviderForUrl),
    DEFAULT_CODEC_REGISTRY
  )

  val databaseForUrl: MongoDatabase = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistryForUrl)
  val collectionNameForUrl: String = "Tokens"
  val collectionForUrl: MongoCollection[ShortUrl] = databaseForUrl.getCollection(collectionNameForUrl)
}
