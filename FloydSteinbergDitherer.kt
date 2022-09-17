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

class FloydSteinbergDitherer private constructor() : Ditherer {
  override fun dither(image: Image, newColors: Set<Color>): Image {
    val width: Int = image.getWidth()
    val height: Int = image.getHeight()
    val colors: Array<Array<Color?>> = Array(height) { arrayOfNulls(width) }
    for (y in 0 until height) {
      for (x in 0 until width) {
        colors[y][x] = image.getColor(x, y)
      }
    }
    for (y in 0 until height) {
      for (x in 0 until width) {
        val originalColor: Color? = colors[y][x]
        val replacementColor: Color? = originalColor?.getNearestColor(newColors)
        colors[y][x] = replacementColor
        val error: Color = originalColor!!.minus(replacementColor)
        for (component in ERROR_DISTRIBUTION) {
          val siblingX = x + component.deltaX
          val siblingY = y + component.deltaY
          if (siblingX >= 0 && siblingY >= 0 && siblingX < width && siblingY < height) {
            val errorComponent: Color = error.scaled(component.errorFraction)
            colors[siblingY][siblingX] = colors[siblingY][siblingX]!!.plus(errorComponent)
          }
        }
      }
    }
    return Image.fromColors(colors)
  }

  private class ErrorComponent internal constructor(val deltaX: Int, val deltaY: Int, val errorFraction: Double)
  companion object {
    val INSTANCE = FloydSteinbergDitherer()
    private val ERROR_DISTRIBUTION = arrayOf(
      ErrorComponent(1, 0, 7.0 / 16.0),
      ErrorComponent(-1, 1, 3.0 / 16.0),
      ErrorComponent(0, 1, 5.0 / 16.0),
      ErrorComponent(1, 1, 1.0 / 16.0)
    )
  }
}