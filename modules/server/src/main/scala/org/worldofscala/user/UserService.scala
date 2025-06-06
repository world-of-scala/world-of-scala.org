package org.worldofscala.user

import zio.*

import io.scalaland.chimney.dsl._
import java.time.ZonedDateTime

import org.worldofscala.auth.*

import org.worldofscala.repository.TransactionSupport

import java.sql.SQLException

import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill.Postgres

import org.worldofscala.domain.errors.{InvalidCredentialsException, UserNotFoundException, UserAlreadyExistsException}

import dev.cheleb.ziochimney.*

trait UserService {
  def register(person: NewUser): Task[User]
  def login(email: String, password: String): Task[User]
  def getProfile(userId: UserID): Task[User]
}

class UserServiceLive private (
  userRepository: UserRepository,
  quill: Quill.Postgres[SnakeCase]
) extends UserService
    with TransactionSupport(quill) {

  def register(person: NewUser): Task[User] =
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

object UserServiceLive {
  val layer: RLayer[UserRepository & Postgres[SnakeCase], UserService] =
    ZLayer.derive[UserServiceLive]
}
