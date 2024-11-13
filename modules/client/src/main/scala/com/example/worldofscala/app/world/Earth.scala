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

object Earth {

  def apply(div: ReactiveHtmlElement[HTMLDivElement]) =
    val scene  = new Scene();
    val camera = new PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);

    val renderer = new WebGLRenderer(
      WebGLRendererParameters()
        .setAntialias(true)
        .setAlpha(true)
    );
    renderer.setPixelRatio(window.devicePixelRatio)
    renderer.setSize(window.innerWidth * 0.88, window.innerHeight * .88);

    val orbitControl = OrbitControls(camera, renderer.domElement)

    val detail = 100

    val geometry      = new IcosahedronGeometry(1, 100)
    val pointGeometry = new IcosahedronGeometry(1, detail);

    val textureLoader = TextureLoader()

    val colorMap = textureLoader.load("/public/img/8081-earthmap10k.jpg")

    val material = new MeshBasicMaterial(
      MeshBasicMaterialParameters()
        .setColor(0x202020)
        .setWireframe(false)
        .setAlphaToCoverage(true)
    );

    val globeGroup: MeshObject3D = Group()

    val cube: MeshObject3D = new Mesh(geometry, material);

    globeGroup.add(cube)

    val pointMaterial = PointsMaterial(
      PointsMaterialParameters()
        .setColor(0xf0f0f0)
        .setSize(0.02)
        .setMap(colorMap)
    )
    val points: MeshObject3D = Points(pointGeometry, pointMaterial)

    globeGroup.add(points)

    scene.add(
      globeGroup
    )

    camera.position.z = 5;

    val animate: XRFrameRequestCallback = (_, _) => {

      // globeGroup.rotation.x += 0.01;
      globeGroup.rotation.y += 0.002;

      renderer.render(scene, camera);
      orbitControl.update()

    }
    renderer.setAnimationLoop(animate);

    val light = HemisphereLight(0xffffff, 0xf8f8f0, 3)

    scene.add(light)

    renderer.domElement

    div.ref.append(renderer.domElement)

    div

}
