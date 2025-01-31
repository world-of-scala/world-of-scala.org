package dev.cheleb.threescalajs

import typings.three.mod.*
import scalajs.js.JSConverters.*

type MeshObject3D = typings.three.srcCoreObject3DMod.Object3D[typings.three.srcCoreObject3DMod.Object3DEventMap]

given Conversion[PerspectiveCamera, Camera] = pc => pc.asInstanceOf[Camera]
given Conversion[Line[BufferGeometry[Nothing], LineBasicMaterial, Nothing], MeshObject3D] = l =>
  l.asInstanceOf[MeshObject3D]

given Conversion[
  scala.scalajs.js.Array[typings.three.srcMathVector3Mod.Vector3],
  scala.scalajs.js.Array[typings.three.srcMathVector2Mod.Vector2 | typings.three.srcMathVector3Mod.Vector3]
] = v =>
  v.asInstanceOf[
    scala.scalajs.js.Array[typings.three.srcMathVector2Mod.Vector2 | typings.three.srcMathVector3Mod.Vector3]
  ]

def points(ps: (Double, Double, Double)*) =
  ps.map(p => new typings.three.srcMathVector3Mod.Vector3(p._1, p._2, p._3)).toJSArray
