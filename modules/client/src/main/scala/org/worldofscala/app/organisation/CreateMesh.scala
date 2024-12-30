package org.worldofscala.organisation

import com.raquo.laminar.api.L.*
import org.scalajs.dom.FormData
import dev.cheleb.ziotapir.laminar.*
import org.worldofscala.earth.MeshEndpoint

import org.scalajs.dom.Blob
import java.io.*

import org.worldofscala.app.session

import scala.scalajs.js.typedarray.Uint8Array
import org.scalajs.dom.ReadableStreamReader

object CreateMesh:

  val bb = new FormData

  def apply() =
    div(
      form(
        method := "POST",
        action := "http://localhost:8080/api/mesh",
        h1("Create Mesh"),
        div(
          input(
            nameAttr    := "name",
            placeholder := "Name"
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
                  bb.delete("data")
                  bb.append("data", file, file.name)
                  println(file.name)
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
              bb.get("data") match {
                case blob: Blob =>
                  val baos = new ByteArrayOutputStream()

                  val reader = blob.stream().getReader()

                  def read(reader: ReadableStreamReader[Uint8Array]): Unit = reader.read().`then` { chunk =>
                    chunk.done match {
                      case true => ()
                      case false =>
                        chunk.value.foreach(s => baos.write(s.toInt))
                        read(reader)
                    }
                  }
                  read(reader)
                  val in = new ByteArrayInputStream(baos.toByteArray())
                  if (true)
                    MeshEndpoint.streamCreate("name", in).runJs

                  println("Blobyy")
                case _ =>
                  println("No file")
//                  MeshEndpoint.create(NewMesh("name", blob.))
              }
//              MeshEndpoint.create(NewMesh("name", bb.))
            }
          )
        )
      )
    )
