package com.example.worldofscala.app.world

import typings.three.mod.Camera
import typings.three.mod.PerspectiveCamera

type MeshObject3D = typings.three.srcCoreObject3DMod.Object3D[typings.three.srcCoreObject3DMod.Object3DEventMap]

given Conversion[PerspectiveCamera, Camera] = pc => pc.asInstanceOf[Camera]
