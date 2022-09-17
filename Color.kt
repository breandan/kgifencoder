/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.hypergraph.kaliningraph.image.gif

import kotlin.math.sqrt

/**
 * An RGB representation of a color, which stores each component as a double in the range [0, 1].
 * Values outside of [0, 1] are permitted though, as this is convenient e.g. for representing color
 * deltas.
 */
class Color(private val red: Double, private val green: Double, private val blue: Double) {
  fun getComponent(index: Int): Double {
    return when (index) {
      0 -> red
      1 -> green
      2 -> blue
      else -> throw IllegalArgumentException("Unexpected component index: $index")
    }
  }

  fun scaled(s: Double): Color {
    return Color(s * red, s * green, s * blue)
  }

  operator fun plus(that: Color): Color {
    return Color(red + that.red, green + that.green, blue + that.blue)
  }

  operator fun minus(that: Color?): Color {
    return Color(red - that!!.red, green - that.green, blue - that.blue)
  }

  fun getEuclideanDistanceTo(that: Color?): Double {
    val d = this.minus(that)
    val sumOfSquares = d.red * d.red + d.green * d.green + d.blue * d.blue
    return sqrt(sumOfSquares)
  }

  /**
   * Find this color's nearest neighbor, based on Euclidean distance, among some set of colors.
   */
  fun getNearestColor(colors: Collection<Color>): Color {
    var nearestCentroid: Color? = null
    var nearestCentroidDistance = Double.POSITIVE_INFINITY
    for (color in colors) {
      val distance = getEuclideanDistanceTo(color)
      if (distance < nearestCentroidDistance) {
        nearestCentroid = color
        nearestCentroidDistance = distance
      }
    }
    return nearestCentroid!!
  }

  val rgbInt: Int
    get() {
      val redComponent = (red * 255).toInt()
      val greenComponent = (green * 255).toInt()
      val blueComponent = (blue * 255).toInt()
      return redComponent shl 16 or (greenComponent shl 8) or blueComponent
    }

  override fun equals(o: Any?): Boolean {
    if (o !is Color) return false
    val that = o
    return red == that.red && green == that.green && blue == that.blue
  }

  override fun hashCode(): Int {
    var result: Int
    var temp: Long
    temp = red.toRawBits()
    result = (temp xor (temp ushr 32)).toInt()
    temp = green.toRawBits()
    result = 31 * result + (temp xor (temp ushr 32)).toInt()
    temp = blue.toRawBits()
    result = 31 * result + (temp xor (temp ushr 32)).toInt()
    return result
  }

  override fun toString(): String = "Color[$red, $green, $blue]"

  companion object {
    val BLACK = Color(0.0, 0.0, 0.0)
    val WHITE = Color(1.0, 1.0, 1.0)
    val RED = Color(1.0, 0.0, 0.0)
    val GREEN = Color(0.0, 1.0, 0.0)
    val BLUE = Color(0.0, 0.0, 1.0)
    fun fromRgbInt(rgb: Int): Color {
      val redComponent = rgb ushr 16 and 0xFF
      val greenComponent = rgb ushr 8 and 0xFF
      val blueComponent = rgb and 0xFF
      return Color(redComponent / 255.0, greenComponent / 255.0, blueComponent / 255.0)
    }

    fun getCentroid(colors: Multiset<Color>): Color {
      var sum = BLACK
      for (color in colors.distinctElements) {
        val weight: Int = colors.count(color)
        sum = sum.plus(color.scaled(weight.toDouble()))
      }
      return sum.scaled(1.0 / colors.size)
    }
  }
}