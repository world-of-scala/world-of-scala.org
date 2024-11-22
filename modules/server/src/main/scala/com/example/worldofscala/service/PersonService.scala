package com.example.worldofscala.service

import zio.*

import io.scalaland.chimney.dsl._
import java.time.Instant
import java.time.ZonedDateTime

import com.example.worldofscala.domain.*
import com.example.worldofscala.domain.errors.*
import com.example.worldofscala.login.LoginPassword
import com.example.worldofscala.repositories.UserRepository
import com.example.worldofscala.UserEntity
import com.example.worldofscala.NewUserEntity
import com.example.worldofscala.repositories.TransactionSupport

import java.sql.SQLException

import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill.Postgres

import io.scalaland.chimney.Transformer
trait PersonService {
  def register(person: Person): Task[User]
  def login(email: String, password: String): Task[User]
  def getProfile(userId: UserID): Task[User]
}

class PersonServiceLive private (
  userRepository: UserRepository,
  jwtService: JWTService,
  quill: Quill.Postgres[SnakeCase]
) extends PersonService
    with TransactionSupport(quill) {

  def register(person: Person): Task[User] =
    tx(
      for {
        _ <- ZIO.logDebug(s"Registering user: $person")
        user <- userRepository
                  .create(
                    NewUserEntity(
                      None,
                      firstname = person.firstname,
                      lastname = person.lastname,
                      email = person.email,
                      hashedPassword = Hasher.generatedHash(person.password.toString),
                      creationDate = ZonedDateTime.now()
                    )
                  )
                  .catchSome { case e: SQLException =>
                    ZIO.logError(s"Error code: ${e.getSQLState} while creating user: ${e.getMessage}")
                      *> ZIO.fail(UserAlreadyExistsException())
                  }
                  .mapInto[User]
      } yield user
    )
  override def login(email: String, password: String): Task[User] =
    userRepository
      .findByEmail(email)
      .map {
        _.filter(user => Hasher.validateHash(password, user.hashedPassword))
      }
      .someOrFail(InvalidCredentialsException())
      .mapInto[User]

  override def getProfile(userId: UserID): Task[User] =
    for
      userEntity <- userRepository
                      .findByEmail(userId.email)
                      .someOrFail(UserNotFoundException(userId.email))
      user = userEntity.into[User].transform
    yield user

}

object PersonServiceLive {
  val layer: RLayer[UserRepository & JWTService & Postgres[SnakeCase], PersonService] =
    ZLayer.derive[PersonServiceLive]
}
