package org.worldofscala.app

import com.raquo.laminar.api.L.*

import org.scalajs.dom.HTMLDivElement
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.worldofscala.app
import org.worldofscala.auth.UserToken

trait SecuredPage:

  export app.session

  def notlogged = h1("Please log in to view your profile")

  def securedContent(userToken: UserToken): ReactiveHtmlElement[HTMLDivElement]

  def content() =
    div(
      child <-- session(notlogged) { userToken =>
        securedContent(userToken)
      }
    )

  def apply(): ReactiveHtmlElement[HTMLDivElement] =
    content()
