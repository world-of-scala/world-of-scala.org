package org.worldofscala.app.world

import THREE.*
import scala.scalajs.js
import scalajs.js.JSConverters.*
import org.scalajs.dom.window
import org.worldofscala.organisation.LatLon

object SceneHelper {

  def newPinner(r: Int, location: LatLon, placeName: String = ""): GLTFResult => Group = obj => {
    val pinnerGroup = new Group()
    println(s"Creating pinner for $placeName at ${location.lat}, ${location.lon}")
    println(s"pinnerGroup: ${pinnerGroup.id}")
    val pinner = obj.scene.jsClone(true)

    println(s"pinner: ${pinner.id}")

    val (x, y, z) = location.xyz(r + 0.02)
    pinner.position.set(x, y, z)
    pinner.lookAt(0, 0, 0)

    // Create tooltip but initially hide it
    if (placeName.nonEmpty) {
      val tooltipSprite = createTextSprite(placeName)
      println("Sprite: " + tooltipSprite.id)

      tooltipSprite.position.set(x * 1.1, y * 1.1, z * 1.1)
      // Hide tooltip initially
      tooltipSprite.visible = false
      tooltipSprite.userData.asInstanceOf[js.Dynamic].isTooltip = true
      pinnerGroup.add(tooltipSprite)

      // Tag the pinner to identify it for raycasting
      pinnerGroup.userData.asInstanceOf[js.Dynamic].isPinner = true
      pinnerGroup.userData.asInstanceOf[js.Dynamic].placeName = placeName

      pinnerGroup.userData = PinnerData(pinnerGroup.id, placeName, tooltipSprite)

      // Store a reference to
      // the tooltip in the pinner for easy access
      pinner.userData.asInstanceOf[js.Dynamic].tooltip = tooltipSprite

    }
    pinnerGroup.add(pinner)
    pinnerGroup.add(drawLine(x, y, z))

    pinnerGroup
  }

  def createTextSprite(text: String): Sprite = {
    // Create canvas for the texture - smaller dimensions for reduced size
    val canvas  = js.Dynamic.newInstance(js.Dynamic.global.OffscreenCanvas)(128, 64)
    val context = canvas.getContext("2d").asInstanceOf[js.Dynamic]

    // Style the text - smaller font size
    context.font = "Bold 12px Arial"
    context.textAlign = "center"

    // No background - removed background fill

    // Draw the text with a slight glow effect for better visibility
    // Add slight shadow for better visibility against different backgrounds
    context.shadowColor = "rgba(0, 0, 0, 0.7)"
    context.shadowBlur = 3
    context.shadowOffsetX = 1
    context.shadowOffsetY = 1

    // Draw text with white color
    context.fillStyle = "white"
    context.fillText(text, 64, 32)

    // Create a sprite with this texture
    val texture = new CanvasTexture(canvas)
    val material = new SpriteMaterial(
      js.Dynamic.literal(
        map = texture,
        sizeAttenuation = false,
        transparent = true
      )
    )

    val sprite = new Sprite(material)
    // Keep the small scale
    sprite.scale.set(0.3, 0.15, 1.0)
    sprite
  }

  def drawLine(
    x: Double,
    y: Double,
    z: Double
  ) = {
    // Create positions array for LineSegmentsGeometry
    val positions = js.Array[Double](
      x,
      y,
      z, // start point
      x * 1.1,
      y * 1.1,
      z * 1.1 // end point
    )

    // Create LineSegmentsGeometry and set positions
    val geometry = new LineSegmentsGeometry()
    geometry.setPositions(positions)

    // Create LineMaterial with desired properties
    val material = new LineMaterial(
      js.Dynamic.literal(
        color = 0x0000ff,
        linewidth = 3, // Line width in pixels
        resolution = new Vector2(window.innerWidth, window.innerHeight)
      )
    )

    // Create LineSegments2 with the geometry and material
    val line = new LineSegments2(geometry, material)
    line.userData.asInstanceOf[js.Dynamic].name = "Line to "
    line
  }

  def points(ps: (Double, Double, Double)*) =
    ps.map(p => new Vector3(p._1, p._2, p._3)).toJSArray
}
