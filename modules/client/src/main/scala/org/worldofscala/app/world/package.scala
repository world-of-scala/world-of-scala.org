package org.worldofscala.app.world

import typings.three.mod.*

type MeshObject3D = typings.three.srcCoreObject3DMod.Object3D[typings.three.srcCoreObject3DMod.Object3DEventMap]

given Conversion[PerspectiveCamera, Camera] = pc => pc.asInstanceOf[Camera]
given Conversion[Line[BufferGeometry[Nothing], LineBasicMaterial, Nothing], MeshObject3D] = l =>
  l.asInstanceOf[MeshObject3D]
