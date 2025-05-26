package org.worldofscala.app.profile

import com.raquo.laminar.api.L.*

import dev.cheleb.ziotapir.laminar.*

import org.worldofscala.app.SecuredPage
import org.worldofscala.user.*
import org.worldofscala.auth.UserToken

/**
 * ProfilePage is a secured page that displays the user's profile information.
 */
object ProfilePage extends SecuredPage:

  val userBus = new EventBus[User]

  def securedContent(userToken: UserToken) = div(
    onMountCallback { _ =>
      UserEndpoint.profile(()).emitTo(userBus)
    },
    renderUser
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
