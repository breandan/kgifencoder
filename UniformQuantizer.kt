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

import kotlin.math.pow

/**
 * Divides the color space into uniform segments, ignoring the color profile of the original image.
 * This is very fast but tends to yield poor results, so it should only be used in situations where
 * image quality is unimportant.
 */
class UniformQuantizer private constructor() : ColorQuantizer {
  override fun quantize(originalColors: Multiset<Color>, maxColorCount: Int): Set<Color> {
    val baseSegments: Int = maxColorCount.toDouble().pow(1.0 / 3.0).toInt()
    var redSegments = baseSegments
    var greenSegments = baseSegments

    // See if we can add an extra segment to one or two channels.
    if (redSegments * (greenSegments + 1) * baseSegments <= maxColorCount) ++greenSegments
    if ((redSegments + 1) * greenSegments * baseSegments <= maxColorCount) ++redSegments
    val colors: MutableSet<Color> = HashSet()
    for (redSegment in 0 until redSegments) {
      for (greenSegment in 0 until greenSegments) {
        for (blueSegment in 0 until baseSegments) {
          val r = redSegment / (redSegments - 1.0)
          val g = greenSegment / (greenSegments - 1.0)
          val b = blueSegment / (baseSegments - 1.0)
          colors.add(Color(r, g, b))
        }
      }
    }
    return colors
  }
}