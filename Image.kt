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

/**
 * An immutable grid of pixel colors.
 */
class Image private constructor(val colors: Array<Array<Color>>) {
  fun getColor(x: Int, y: Int): Color = colors[y][x]

  fun getColor(index: Int): Color = colors[index / getWidth()][index % getWidth()]

  fun getColors(): Multiset<Color> {
    val colorCounts: Multiset<Color> = HashMultiset()
    for (i in 0 until getNumPixels()) {
      val color: Color = getColor(i)
      colorCounts.add(color)
    }
    return colorCounts
  }

  fun getWidth(): Int = colors[0].size

  fun getHeight(): Int = colors.size

  fun getNumPixels(): Int = getWidth() * getHeight()

  companion object {
    fun fromColors(colors: Array<Array<Color?>>): Image = Image(colors as Array<Array<Color>>)

    fun fromRgb(rgb: Array<IntArray>): Image {
      val height = rgb.size
      val width = rgb[0].size
      val colors: Array<Array<Color?>> = Array(height) { arrayOfNulls(width) }
      for (y in 0 until height) {
        if (rgb[y].size != width) {
          throw Exception("rows lengths do not match in RGB array")
        }
        for (x in 0 until width) {
          colors[y][x] = Color.fromRgbInt(rgb[y][x])
        }
      }
      return Image(colors as Array<Array<Color>>)
    }

    fun fromRgb(rgb: IntArray, width: Int): Image {
      if (rgb.size % width != 0) {
        throw Exception("the given width does not divide the number of pixels")
      }
      val height = rgb.size / width
      val colors: Array<Array<Color?>> = Array(height) { arrayOfNulls(width) }
      for (y in 0 until height) {
        for (x in 0 until width) {
          colors[y][x] = Color.fromRgbInt(rgb[y * width + x])
        }
      }
      return Image(colors as Array<Array<Color>>)
    }
  }
}