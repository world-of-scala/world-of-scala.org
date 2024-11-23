package org.worldofscala.organisation

import be.doeraene.webcomponents.ui5.*
//import be.doeraene.webcomponents.ui5.configkeys.ToastPlacement

import com.raquo.laminar.api.L.*

import dev.cheleb.scalamigen.*
//import dev.cheleb.ziolaminartapir.*

import org.worldofscala.app.given

object CreateOrganisation:

  given Defaultable[LatLon] with
    def default = LatLon(0.0, 0.0)

  def apply() =
    val organisationVar = Var(
      Organisation("", None)
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

            // OrganisationEndpoint
            //   .create(OrganisationVar.now())
            //   .emitTo(organisationBus, errorBus)

            // scalafmt:on

          }
        )
      )
      // renderToast(organisationBus, errorBus)
    )
