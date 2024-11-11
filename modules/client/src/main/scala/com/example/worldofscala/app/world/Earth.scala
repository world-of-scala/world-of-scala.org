package com.example.worldofscala.app.world

import org.scalajs.dom.window
//import org.scalajs.dom.document

//import typings.three.mod.*
import typings.webxr.*
import typings.three.mod.*

import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLDivElement

object Earth {

  def apply(div: ReactiveHtmlElement[HTMLDivElement]) =
    val scene  = new Scene();
    val camera = new PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);

    val renderer = new WebGLRenderer();
    renderer.setSize(window.innerWidth, window.innerHeight - 128);
//    document.body.appendChild(renderer.domElement);

    val geometry = new BoxGeometry(1, 1, 1, 1, 1, 1);

    val material = new MeshBasicMaterial() // MeshBasicMaterial.setColor(0x00ff00));

    val cube = new Mesh(geometry, material);

    scene.add(
      cube.asInstanceOf[typings.three.srcCoreObject3DMod.Object3D[typings.three.srcCoreObject3DMod.Object3DEventMap]]
    )

    camera.position.z = 5;

    val animate: XRFrameRequestCallback = (_, _) => {

      cube.rotation.x += 0.01;
      cube.rotation.y += 0.01;

      renderer.render(scene, camera);

    }
    renderer.setAnimationLoop(animate);

    renderer.domElement

    div.ref.append(renderer.domElement)

    div

}
