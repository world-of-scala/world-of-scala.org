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
          styleAttr := "float: both;",
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
          h2("Preview"),
          child.maybe <-- fileVar.signal.map(preview)
        ),
        div(
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
