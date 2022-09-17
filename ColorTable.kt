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

import kotlin.math.max

internal class ColorTable private constructor(indexToColor: Map<Int, Color>, colorToIndex: Map<Color, Int>) {
  private val indexToColor: Map<Int, Color>
  private val colorToIndex: Map<Color, Int>

  init {
    this.indexToColor = indexToColor
    this.colorToIndex = colorToIndex
  }

  fun paddedSize(): Int {
    // The padded size needs to be at least 2, because it's impossible to encode a size of 1 in the
    // image descriptor block, which uses a 2^(n+1) representation.
    return max(GifMath.roundUpToPowerOfTwo(unpaddedSize()), 2)
  }

  private fun unpaddedSize(): Int {
    return colorToIndex.size
  }

  fun write(outputStream: OutputStream) {
    for (i in 0 until unpaddedSize()) {
      Streams.writeRgb(outputStream, indexToColor[i]!!.rgbInt)
    }
    for (i in unpaddedSize() until paddedSize()) {
      Streams.writeRgb(outputStream, 0)
    }
  }

  fun getIndices(image: Image): IntArray {
    val result = IntArray(image.getNumPixels())
    for (i in result.indices) {
      result[i] = colorToIndex[image.getColor(i)]!!
    }
    return result
  }

  companion object {
    fun fromColors(colors: Set<Color>): ColorTable {
      val indexToColor: MutableMap<Int, Color> = HashMap()
      val colorToIndex: MutableMap<Color, Int> = HashMap()
      var index = 0
      for (color in colors) {
        if (!colorToIndex.containsKey(color)) {
          indexToColor[index] = color
          colorToIndex[color] = index
          ++index
        }
      }
      return ColorTable(indexToColor, colorToIndex)
    }
  }
}