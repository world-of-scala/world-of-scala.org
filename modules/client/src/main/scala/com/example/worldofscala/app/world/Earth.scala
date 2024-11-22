package com.example.worldofscala.app.world

import org.scalajs.dom.window
//import org.scalajs.dom.document

//import typings.three.mod.*
import typings.webxr.*
import typings.three.mod.*

import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLDivElement
import typings.three.srcMaterialsMeshBasicMaterialMod.MeshBasicMaterialParameters
import typings.three.srcMaterialsPointsMaterialMod.PointsMaterialParameters
import typings.three.srcRenderersWebGLRendererMod.WebGLRendererParameters
import typings.three.examplesJsmAddonsMod.OrbitControls
import typings.three.examplesJsmAddonsMod.GLTFLoader
import scala.scalajs.js.Math.{PI, cos, sin}
import typings.three.srcMaterialsLineBasicMaterialMod.LineBasicMaterialParameters
import typings.three.examplesJsmLoadersGltfloaderMod.GLTF

object Earth {

  val R = 1

  def apply(div: ReactiveHtmlElement[HTMLDivElement]) =
    val scene  = new Scene();
    val camera = new PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);

    camera.position.set(0, 0, 5)

    val renderer = new WebGLRenderer(
      WebGLRendererParameters()
        .setAntialias(true)
        .setAlpha(false)
    );
    renderer.setPixelRatio(window.devicePixelRatio)
    renderer.setSize(window.innerWidth * 0.88, window.innerHeight * .88);

    val orbitControl = OrbitControls(camera, renderer.domElement)

    val detail = 300

    val geometry      = new IcosahedronGeometry(R - 0.05, 10)
    val pointGeometry = new IcosahedronGeometry(R, detail);

    val textureLoader = TextureLoader()

    val colorMap = textureLoader.load("/public/img/8081-earthmap10k.jpg")

    val material = new MeshBasicMaterial(
      MeshBasicMaterialParameters()
        .setColor(0x202020)
        .setWireframe(false)
        .setAlphaToCoverage(true)
    );

    val globeGroup: MeshObject3D = Group()

    val earth: MeshObject3D = new Mesh(geometry, material);

    globeGroup.add(earth)

    val pointMaterial = PointsMaterial(
      PointsMaterialParameters()
        .setColor(0xf0f0f0)
        .setSize(0.02)
        .setMap(colorMap)
    )
    val points: MeshObject3D = Points(pointGeometry, pointMaterial)

    globeGroup.add(points)

    def addObj(obj: GLTF, lat: Double, lon: Double) =
      val pinner    = obj.scene.clone(true)
      val (x, y, z) = coord(lat, lon)
      pinner.position.set(x, y, z)
      pinner.lookAt(0, 0, 0)
      globeGroup.add(pinner)

    def addObj2(obj: GLTF, lat: Double, lon: Double) =
      val pinner    = obj.scene.clone(true)
      val (x, y, z) = coord(lat, lon)
      pinner.position.set(x, y, z)
      pinner.lookAt(0, 0, 0)
      globeGroup.add(pinner)

    val loader = new GLTFLoader()

    loader.load(
      "/public/res/scala.glb",
      (obj) => {
        addObj2(obj, 46.5188, 6.5593) // Lauzane
      }
    )

    loader.load(
      "/public/res/pinner.glb",
      (obj) => {
        addObj(obj, 43.604997973579614, 3.8660556077984163) // Montpellier
        addObj(obj, 37.7471355990712, -122.38776282253441)  // SF
        addObj(obj, 0, 0)
      }
    )

    globeGroup.add(drawLine())

    scene.add(
      globeGroup
    )

    globeGroup.rotation.y = PI / 2
    globeGroup.rotation.x = PI / 4

    val animate: XRFrameRequestCallback = (_, _) => {

      // globeGroup.rotation.x += 0.001;
      globeGroup.rotation.y += 0.002;

      renderer.render(scene, camera);
      orbitControl.update()

    }
    renderer.setAnimationLoop(animate);

    val light = DirectionalLight(0xffffff, 100)

    light.position.set(5, 5, 5)
    light.lookAt(0, 0, 0)
    scene.add(light)

    renderer.domElement

    div.ref.append(renderer.domElement)

    div

  def drawLine(): typings.three.srcCoreObject3DMod.Object3D[typings.three.srcCoreObject3DMod.Object3DEventMap] = {
    val material = new LineBasicMaterial(LineBasicMaterialParameters().setColor(0x0000ff))
    val points
      : scala.scalajs.js.Array[typings.three.srcMathVector2Mod.Vector2 | typings.three.srcMathVector3Mod.Vector3] =
      scala.scalajs.js.Array[typings.three.srcMathVector2Mod.Vector2 | typings.three.srcMathVector3Mod.Vector3](
        new typings.three.srcMathVector3Mod.Vector3(0, 0, 0),
//        new typings.three.srcMathVector3Mod.Vector3(0, 10, 0),
        new typings.three.srcMathVector3Mod.Vector3(10, 10, 10)
      )
    val geometry = new BufferGeometry().setFromPoints(
      points
    );
    val line: Line[BufferGeometry[Nothing], LineBasicMaterial, Nothing] = new Line(geometry, material);
    line
  }

  def coord(lat: Double, lon: Double) = {
    val latRad = lat * PI / 180.0;
    val lonRad = (-lon + 180) * PI / 180.0;
    val d      = R + 0.02
    (d * cos(latRad) * cos(lonRad), d * sin(latRad), d * cos(latRad) * sin(lonRad));

  }
}
