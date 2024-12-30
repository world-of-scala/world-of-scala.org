package org.worldofscala.organisation

import com.raquo.laminar.api.L.*

import dev.cheleb.ziotapir.laminar.*
import org.worldofscala.earth.MeshEndpoint

import java.io.*

import org.worldofscala.app.session

import scala.scalajs.js.typedarray.*

object CreateMesh:

  val name                                       = Var("")
  val fileVar: Var[Option[org.scalajs.dom.File]] = Var(None)

  def apply() =
    div(
      form(
        method := "POST",
        action := "http://localhost:8080/api/mesh",
        h1("Create Mesh"),
        div(
          input(
            nameAttr    := "name",
            placeholder := "Name",
            onChange.mapToValue --> name
          )
        ),
        div(
          input(
            nameAttr    := "data",
            placeholder := "Data",
            `type`      := "file",
            onChange.mapToFiles --> { f =>
              f match {
                case Seq(file) =>
                  println(file.name)
                  fileVar.set(Some(file))
                case _ =>
              }

            }
          )
        ),
        div(
          styleAttr := "max-width: fit-content; margin:1em auto",
          button(
            "Create",
            onClick --> { ev =>
              ev.preventDefault()
              fileVar.now() match {
                case Some(file) =>
                  file.arrayBuffer().`then` { buffer =>
                    val in = new ByteArrayInputStream(new Int8Array(buffer).toArray)
                    MeshEndpoint.streamCreate(name.now(), in).runJs
                  }
                case None =>
              }
            }
          )
        )
      )
    )
