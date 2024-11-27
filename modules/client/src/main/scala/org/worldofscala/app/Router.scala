package org.worldofscala.app

import com.raquo.laminar.api.L.*

import dev.cheleb.ziotapir.laminar.*

import frontroute.*

import org.scalajs.dom

import org.worldofscala.*
import org.worldofscala.organisation.OrganisationEndpoint
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLDivElement

import org.worldofscala.organisation.Organisation

import zio.ZIO

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
                    OrganisationEndpoint
                      .allStream(())
                      .jsonl[Organisation, Unit](organisation =>
                        ZIO.attempt(world.Earth.organisationBus.emit(organisation))
                      )

                    earthVar.set(Some(world.Earth(ctx.owner, div())))

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
