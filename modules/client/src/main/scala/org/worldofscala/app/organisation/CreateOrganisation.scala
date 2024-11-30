package org.worldofscala.organisation

import be.doeraene.webcomponents.ui5.*
//import be.doeraene.webcomponents.ui5.configkeys.ToastPlacement

import com.raquo.laminar.api.L.*

import dev.cheleb.scalamigen.*
import dev.cheleb.ziotapir.laminar.*

import org.worldofscala.app.given

object CreateOrganisation:

  given Form[LatLon] = stringFormWithValidation(using
    new Validator[LatLon] {
      override def validate(value: String): Either[String, LatLon] =
        value.split(",") match {
          case Array(lat, lon) =>
            (
              lat.toDoubleOption.toRight("Invalid latitude"),
              lon.toDoubleOption.toRight("Invalid longitude")
            ) match {
              case (Right(lat), Right(lon)) => Right(LatLon(lat, lon))
              case (Left(latError), Left(rightError)) =>
                Left(s"$latError and $rightError")
              case (Left(latError), _) => Left(latError)
              case (_, Left(lonError)) => Left(lonError)
            }
          case _ => Left("Invalid format")
        }
    }
  )
  given Defaultable[LatLon] with
    def default = LatLon(46.5188, 6.5593)

  def apply() =
    val organisationVar = Var(
      NewOrganisation("", None)
    )

    div(
      h1("Create  Organisation"),
      div(
        styleAttr := "float: left;",
        organisationVar.asForm,
        children <-- organisationVar.signal.map {
          _.errorMessages.map(div(_)).toSeq
        }
      ),
      div(
        styleAttr := "max-width: fit-content; margin:1em auto",
        Button(
          "Create",
          disabled <-- organisationVar.signal.map(_.errorMessages.nonEmpty),
          onClick --> { _ =>
            // scalafmt:off

            OrganisationEndpoint
              .create(organisationVar.now())
              .runJs

            // scalafmt:on

          }
        )
      )
//      renderToast(organisationBus, errorBus)
    )

    // def renderToast(userBus: EventBus[Organisation], errorBus: EventBus[Throwable]) =
    //   Seq(
    //     Toast(
    //       cls := "srf-valid",
    //       _.duration  := 2.seconds,
    //       _.placement := ToastPlacement.MiddleCenter,
    //       child <-- userBus.events.map(renderUser),
    //       _.open <-- userBus.events.map(_ => true)
    //     ),
    //     Toast(
    //       cls := "srf-invalid",
    //       _.duration  := 2.seconds,
    //       _.placement := ToastPlacement.MiddleCenter,
    //       child <-- errorBus.events.map(_.getMessage()),
    //       _.open <-- errorBus.events.map(_ => true)
    //     )
    //   )
