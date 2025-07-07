package org.worldofscala.app.world

import org.scalajs.dom.window

import THREE.*

import com.raquo.laminar.api.L.*

import dev.cheleb.ziotapir.laminar.*

import dev.cheleb.zthreesjs.*

import scala.scalajs.js.Math.PI

import org.worldofscala.organisation.Organisation
import org.worldofscala.organisation.OrganisationEndpoint
import zio.*
import org.worldofscala.organisation.LatLon
import dev.cheleb.ziotapir.SameOriginBackendClientLive

import org.worldofscala.app.world.SceneHelper.*

object Earth {

  val R = 1

  def apply() =

    val eartthDiv = div()

    val scene  = new Scene();
    val camera = new PerspectiveCamera(30, window.innerWidth / window.innerHeight, 1, 100);

    camera.position.set(0, 0, 5)

    val renderer = WebGLRenderer(
      antialias = true,
      alpha = false
    )

    renderer.setPixelRatio(window.devicePixelRatio)
    renderer.setSize(window.innerWidth * .88, window.innerHeight * .88);

    val orbitControl = OrbitControls(camera, renderer.domElement)

    val detail = 300

    val geometry      = new IcosahedronGeometry(R - 0.05, 10)
    val pointGeometry = new IcosahedronGeometry(R, detail);

    val textureLoader = TextureLoader()

    val colorMap = textureLoader.load("/public/img/8081-earthmap10k.jpg")

    val material = MeshBasicMaterial(
      color = 0x555555,
      wireframe = true
    )

    val globeGroup = Group()

    val earth = new Mesh(geometry, material);

    globeGroup.add(earth)

    val pointMaterial = PointsMaterial(
      color = 0xf0f0f0,
      size = 0.02,
      map = colorMap
    )
    val points = Points(pointGeometry, pointMaterial)

    globeGroup.add(points)

    def addObj(obj: GLTFResult, location: LatLon) =
      val pinner    = obj.scene.jsClone(true)
      val (x, y, z) = location.xyz(R + 0.02)
      pinner.position.set(x, y, z)
      pinner.lookAt(0, 0, 0)
      globeGroup.add(pinner)
      globeGroup.add(drawLine(x, y, z))

    val loader = new GLTFLoader()

    loader.load(
      "/public/res/scala.glb",
      (obj) => {
        addObj(obj, LatLon(46.5188, 6.5593)) // Lauzane
      }
    )

    eartthDiv.amend(
      onMountCallback { _ =>
        OrganisationEndpoint
          .allStream(())
          .jsonlZIO[Organisation] {
            case Right(organisation) =>
              val meshIO: ZIO[Any, Any, GLTFResult] = organisation.meshId match {
                case Some(meshId) =>

                  loader.zload(
                    SameOriginBackendClientLive.url("api", "mesh", meshId.toString())
                  )

                case None =>
                  loader.zload(
                    SameOriginBackendClientLive.url(s"/public/res/pinner.glb")
                  )
              }
              (for {
                obj <- meshIO
                _   <- ZIO.debug(s"Addings ${organisation.name} at ${organisation.location}")
                _   <- ZIO.attempt(addObj(obj, organisation.location))
              } yield ()).ignore

            case Left(errorMessage) =>
              Console.printLine(s"Failed: $errorMessage")
          }
      }
    )

    scene.add(
      globeGroup
    )

    globeGroup.rotation.y = PI / 2
    globeGroup.rotation.x = PI / 4

    val animate: () => Unit = () => {

      // globeGroup.rotation.x += 0.001;
      // globeGroup.rotation.y += 0.0005;

      renderer.render(scene, camera);
      orbitControl.update()

    }
    renderer.setAnimationLoop(animate);

    val light = DirectionalLight(0xffffff, 100)

    light.position.set(5, 5, 5)
    light.lookAt(0, 0, 0)
    scene.add(light)

    eartthDiv.ref.append(renderer.domElement)

    eartthDiv

}
