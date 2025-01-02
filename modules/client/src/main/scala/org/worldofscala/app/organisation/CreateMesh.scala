package org.worldofscala.organisation

import com.raquo.laminar.api.L.*

import dev.cheleb.ziotapir.laminar.*
import org.worldofscala.earth.MeshEndpoint
import org.scalajs.dom.window
import org.scalajs.dom

import java.io.*

import org.worldofscala.app.session

import scala.scalajs.js.typedarray.*

import typings.webxr.*
import typings.three.mod.*
import typings.three.srcRenderersWebGLRendererMod.WebGLRendererParameters
import typings.three.examplesJsmAddonsMod.OrbitControls
import dev.cheleb.threescalajs.given
import typings.three.examplesJsmAddonsMod.GLTFLoader
import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*

import org.worldofscala.earth.Mesh as OrgaMesh

object CreateMesh:

  val name                                       = Var("")
  val fileVar: Var[Option[org.scalajs.dom.File]] = Var(None)
  val thumbnail = Var(
    ""
  )

  def apply() =
    div(
      h1("Meshes"),
      div(
        "Name: ",
        input(
          nameAttr    := "name",
          placeholder := "Name",
          onInput.mapToValue --> name
        )
      ),
      div(
        input(
          `type` := "file",
          accept := ".glb",
          onChange.mapToFiles --> { f =>
            f match {
              case file :: Nil =>
                fileVar.set(Some(file))
              case _ =>
                fileVar.set(None)
            }

          }
        ),
        img(
          src <-- thumbnail.signal,
          width  := "100",
          height := "100"
        ),
        div(
          display <-- fileVar.signal.map(o => if o.isEmpty then "none" else "block"),
          div(
            h2("Preview"),
            child.maybe <-- fileVar.signal.map(preview)
          ),
          div(
            Button(
              "Create",
              disabled <-- name.signal.map(_.isEmpty),
              onClick --> { ev =>
                ev.preventDefault()
                fileVar.now() match {
                  case Some(file) =>
                    file.arrayBuffer().`then` { buffer =>
                      val in  = new ByteArrayInputStream(new Int8Array(buffer).toArray)
                      val in2 = new ByteArrayInputStream(thumbnail.signal.now().getBytes())
                      val ios = for {
                        id <- MeshEndpoint.streamCreate(name.now(), in)
                        _  <- MeshEndpoint.putThumbnail(id, in2)
                      } yield id
                      ios.runJs
                    }
                  case None =>
                }
              }
            )
          )
        ),
        allMeshes()
      )
    )

  def preview(fileOption: Option[org.scalajs.dom.File]) = fileOption match {
    case Some(file) =>
      val loader = new GLTFLoader()
      val renderer = new WebGLRenderer(
        WebGLRendererParameters()
          .setPreserveDrawingBuffer(true)
          .setAntialias(true)
          .setAlpha(false)
      );

      var rotateX = 0.01d
      var rotateY = 0.01d
      val div2 = div(
        CheckBox(
          "Rotate",
          checked := true,
          onChange.mapToChecked --> { checked =>
            if (checked) {
              rotateX = 0.01;
              rotateY = 0.01;
            } else {
              rotateX = 0;
              rotateY = 0;
            }
          }
        )
      )
      val scene  = new Scene();
      val camera = new PerspectiveCamera(1, window.innerWidth / window.innerHeight, 1, 10);

      camera.position.set(1, 1, 2)

      renderer.setPixelRatio(window.devicePixelRatio)
      renderer.setSize(window.innerWidth * .18, window.innerHeight * .18);

      val orbitControl = OrbitControls(camera, renderer.domElement)

      val animate: XRFrameRequestCallback = (_, _) => {

        scene.rotation.x += rotateX;
        scene.rotation.y += rotateY;

        renderer.render(scene, camera);
        orbitControl.update()

      }

      renderer.setAnimationLoop(animate);

      val light        = DirectionalLight(0xffffff, 100)
      val ambientLight = AmbientLight(0xffffff, 100)

      light.position.set(5, 5, 5)
      light.lookAt(0, 0, 0)
      scene.add(light)
      scene.add(ambientLight)

      val canvas = renderer.domElement

      div2.ref.append(canvas)

      div2.amend(
        Button(
          "Snap",
          onClick --> { _ =>
            val dataUrl = canvas.toDataURL("image/png")
            thumbnail.set(dataUrl)
          }
        )
      )

      file.arrayBuffer().`then` { buffer =>
        loader.parse(
          buffer,
          "",
          (gltf) => {
            scene.add(gltf.scene.clone(true))
          }
        )
      }
      Some(div2)
    case None => None
  }

  def allMeshes() =
    val meshes = EventBus[List[(OrgaMesh.Id, String, Option[String])]]()

    div(
      onMountCallback { _ =>
        MeshEndpoint.all(()).emitTo(meshes)
      },
      overflowY := "scroll",
      height    := "400px",
      compat.Table(
        // _.busy <-- busyState,
        _.growing := TableGrowingMode.Scroll,
//        _.events.onLoadMore.mapTo(()) --> loadMoreBus,
        _.slots.columns := compat.Table.column(width := "20rem", span(lineHeight := "1.4rem", "ID")),
        _.slots.columns := compat.Table.column(span(lineHeight := "1.4rem", "Name")),
        _.slots.columns := compat.Table.column(span(lineHeight := "1.4rem", "Preview")),
        children <-- meshes.events.map(
          _.map((id, name, thumbnail) =>
            compat.Table.row(
              dataAttr("card-name") := id.toString,
              _.cell(id.toString()),
              _.cell(name),
              _.cell(
                thumbnail
                  .map(t =>
                    img(
                      src    := t,
                      width  := "64px",
                      height := "48px"
                    )
                  )
                  .getOrElse(span("No preview"))
              )
            )
          )
        )
      )
    )
end CreateMesh
