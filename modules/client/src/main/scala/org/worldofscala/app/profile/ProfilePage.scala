package org.worldofscala.app.profile

import com.raquo.laminar.api.L.*

import org.worldofscala.app.given
import org.worldofscala.user.*

import dev.cheleb.ziotapir.laminar.*
import org.scalajs.dom.HTMLDivElement
import com.raquo.laminar.nodes.ReactiveHtmlElement

object ProfilePage:

  val userBus = new EventBus[User]

  def apply(): ReactiveHtmlElement[HTMLDivElement] = div(
    child <-- session(h1("Please log in to view your profile")) { _ =>
      div(
        onMountCallback { _ =>
          UserEndpoint.profile(()).emitTo(userBus)
        },
        renderUser
      )
    }
  )

  def renderUser =
    div(
      h1("Profile Page"),
      child <-- userBus.events.map { user =>
        div(
          cls := "srf-form",
          h2("User"),
          div("First name: ", user.firstname),
          div("Last name: ", user.lastname),
          div("Email: ", user.email)
        )
      }
    )
