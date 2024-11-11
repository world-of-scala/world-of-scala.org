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
import com.example.worldofscala.repositories.PetRepository
import com.example.worldofscala.PetEntity
import com.example.worldofscala.CatEntity
import com.example.worldofscala.DogEntity

import java.sql.SQLException

import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill.Postgres

import io.scalaland.chimney.Transformer
trait PersonService {
  def register(person: Person): Task[User]
  def login(email: String, password: String): Task[User]
  def getProfile(userId: UserID, withPet: Boolean): Task[(User, Option[Pet])]
}

class PersonServiceLive private (
  userRepository: UserRepository,
  petRepository: PetRepository,
  jwtService: JWTService,
  quill: Quill.Postgres[SnakeCase]
) extends PersonService
    with TransactionSupport(quill) {

  def register(person: Person): Task[User] =
    if person.age < 18 then ZIO.fail(TooYoungException(person.age))
    else
      tx(
        for {
          _ <- ZIO.logDebug(s"Registering user: $person")
          newPetEntity = person.pet.fold(PetEntity.apply, PetEntity.apply)
          petEntity <- petRepository.create(newPetEntity)
          user <- userRepository
                    .create(
                      NewUserEntity(
                        None,
                        name = person.name,
                        email = person.email,
                        hashedPassword = Hasher.generatedHash(person.password.toString),
                        petType = Some(person.pet.fold(_ => PetType.Cat, _ => PetType.Dog)),
                        petId = Some(petEntity.id),
                        age = person.age,
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

  override def getProfile(userId: UserID, withPet: Boolean): Task[(User, Option[Pet])] =
    for
      userEntity <- userRepository
                      .findByEmail(userId.email)
                      .someOrFail(UserNotFoundException(userId.email))
      user = userEntity.into[User].transform
      pet <- maybePet(userEntity, withPet)
    yield (user, pet)

  private def maybePet(userEntity: UserEntity, withPet: Boolean): Task[Option[Pet]] =
    if withPet then
      (userEntity.petType, userEntity.petId) match {
        case (Some(petType), Some(petId)) =>
          petType match
            case PetType.Cat =>
              petRepository
                .getCatById(petId)
                .mapOption[Cat]

            case PetType.Dog =>
              petRepository
                .getDogById(petId)
                .mapOption[Dog]

        case _ => ZIO.none
      }
    else ZIO.none

}

object PersonServiceLive {
  val layer: RLayer[UserRepository & PetRepository & JWTService & Postgres[SnakeCase], PersonService] =
    ZLayer.derive[PersonServiceLive]
}
