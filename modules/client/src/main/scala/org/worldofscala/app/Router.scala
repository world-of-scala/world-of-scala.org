package org.worldofscala.app

import com.raquo.laminar.api.L.*

import dev.cheleb.ziotapir.laminar.*

import frontroute.*

import org.scalajs.dom

import org.worldofscala.*
import org.worldofscala.organisation.OrganisationEndpoint
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLDivElement

object Router:
  val uiBase                     = "public"
  def uiRoute(segments: String*) = segments.mkString(s"/$uiBase/", "/", "")
  private val externalUrlBus     = EventBus[String]()
  val writer                     = externalUrlBus
  def apply() =
    mainTag(
      linkHandler,
      routes(
        div(
          styleAttr := "max-width: fit-content;  margin-left: auto;  margin-right: auto;",
          // potentially children

          pathPrefix(uiBase) {
            firstMatch(
              (pathEnd | path("index.html")) {
                val earthVar = Var(Option.empty[ReactiveHtmlElement[HTMLDivElement]])
                div(
                  onMountCallback { ctx =>
                    OrganisationEndpoint.all(()).emitTo(world.Earth.organisationBus) // (ctx.owner)
                    earthVar.set(Some(world.Earth(ctx.owner, div())))
//                    world.Earth.organisationBus.events.foreach(organisation => println(organisation))(ctx.owner)
                  },
                  child.maybe <-- earthVar.signal
                )

              },
              path("signup") {
                signup.SignupPage()
              },
              path("profile") {
                profile.ProfilePage()
              },
              pathPrefix("organisation") {
                path("new") {
                  organisation.CreateOrganisation()
                }
              },
              path("demos" / "scalablytyped") {
                stats.ScalablytypedDemoPage()
              },
              path("about") {
                HomePage()
              }
            )
          },
          noneMatched {
            div("404 Not Found")
          }
        )
      )
    )
  def linkHandler =
    onMountCallback(ctx => externalUrlBus.events.foreach(url => dom.window.location.href = url)(ctx.owner))
