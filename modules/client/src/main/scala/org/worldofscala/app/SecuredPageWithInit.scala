package org.worldofscala.app

import com.raquo.laminar.api.L.*

import com.raquo.laminar.nodes.ReactiveHtmlElement

trait SecuredPageWithInit extends SecuredPage:

  def init: Unit

  override def content() = div(
    child <-- session(notlogged) { userToken =>
      div(
        onMountCallback(_ => init),
        securedContent(userToken)
      )
    }
  )
