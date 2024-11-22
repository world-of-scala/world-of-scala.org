package com.example.worldofscala.app.profile

import com.raquo.laminar.api.L.*

import com.example.worldofscala.app.given
import com.example.worldofscala.domain.*
import com.example.worldofscala.http.endpoints.PersonEndpoint

import dev.cheleb.ziolaminartapir.*
import org.scalajs.dom.HTMLDivElement
import com.raquo.laminar.nodes.ReactiveHtmlElement

object ProfilePage:

  val userBus = new EventBus[User]

  def apply(): ReactiveHtmlElement[HTMLDivElement] = div(
    child <-- session:
      // If the user is not logged in, show a message
      div(h1("Please log in to view your profile"))
      // If the user is logged in, show the profile page
    (_ =>
      div(
        onMountCallback { _ =>
          PersonEndpoint.profile(()).emitTo(userBus)
        },
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
      )
    )
  )
