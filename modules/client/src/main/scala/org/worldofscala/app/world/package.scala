package org.worldofscala.app.world

import scalajs.js
import THREE.Sprite
import THREE.Object3D

class PinnerData(
  val id: Int,
  val city: String,
  var tooltip: Sprite
) extends js.Object:
  var pinner: js.UndefOr[Object3D] = js.undefined
