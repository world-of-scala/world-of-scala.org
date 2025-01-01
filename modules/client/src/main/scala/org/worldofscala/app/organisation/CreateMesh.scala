package org.worldofscala.organisation

import com.raquo.laminar.api.L.*

import dev.cheleb.ziotapir.laminar.*
import org.worldofscala.earth.MeshEndpoint
import org.scalajs.dom.window

import java.io.*

import org.worldofscala.app.session

import scala.scalajs.js.typedarray.*

import typings.webxr.*
import typings.three.mod.*
import typings.three.srcRenderersWebGLRendererMod.WebGLRendererParameters
import typings.three.examplesJsmAddonsMod.OrbitControls
import dev.cheleb.threescalajs.given
import typings.three.examplesJsmAddonsMod.GLTFLoader
import be.doeraene.webcomponents.ui5.Button

object CreateMesh:

  val name                                       = Var("")
  val fileVar: Var[Option[org.scalajs.dom.File]] = Var(None)

  def apply() =
    div(
      h1("Create Mesh"),
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
    )

  def preview(fileOption: Option[org.scalajs.dom.File]) = fileOption match {
    case Some(file) =>
      val loader = new GLTFLoader()

      val div2   = div()
      val scene  = new Scene();
      val camera = new PerspectiveCamera(3, window.innerWidth / window.innerHeight, 1, 100);

      camera.position.set(0, 0, 1)

      val renderer = new WebGLRenderer(
        WebGLRendererParameters()
          .setAntialias(true)
          .setAlpha(false)
      );

      renderer.setPixelRatio(window.devicePixelRatio)
      renderer.setSize(window.innerWidth * .28, window.innerHeight * .28);

      val orbitControl = OrbitControls(camera, renderer.domElement)

      val animate: XRFrameRequestCallback = (_, _) => {

        scene.rotation.x += 0.01;
        scene.rotation.y += 0.01;

        renderer.render(scene, camera);
        orbitControl.update()

      }

      renderer.setAnimationLoop(animate);

      val light = DirectionalLight(0xffffff, 100)

      light.position.set(5, 5, 5)
      light.lookAt(0, 0, 0)
      scene.add(light)

      div2.ref.append(renderer.domElement)

      file.arrayBuffer().`then` { buffer =>
        loader.parse(
          buffer,
          "",
          (gltf) => {
            println(gltf)
            scene.add(gltf.scene.clone(true))
          }
        )
      }
      Some(div2)
    case None => None

  }
