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
import kotlin.math.min
import kotlin.math.sign

/**
 * Implements median cut quantization.
 *
 *
 * The algorithm works as follows:
 *
 *
 *  * Begin with one cluster containing all the original colors.
 *  * Find the cluster containing the greatest spread along a single color component (red, green
 * or blue).
 *  * Find the median of that color component among colors in the cluster.
 *  * Split the cluster into two halves, using that median as a threshold.
 *  * Repeat this process until the desired number of clusters is reached.
 *
 */
class MedianCutQuantizer private constructor() : ColorQuantizer {
  override fun quantize(originalColors: Multiset<Color>, maxColorCount: Int): Set<Color> {
    val clusters: MutableSet<Cluster> = mutableSetOf()
    clusters.add(Cluster(originalColors))
    while (clusters.size < maxColorCount) {
      val clusterWithLargestSpread: Cluster = clusters.minWith(ClusterSpreadComparator())
      clusters.addAll(clusterWithLargestSpread.split())
    }
    val clusterCentroids: MutableSet<Color> = mutableSetOf()
    for (cluster in clusters) {
      clusterCentroids.add(Color.getCentroid(cluster.colors))
    }
    return clusterCentroids
  }

  class Cluster constructor(colors: Multiset<Color>) {
    val colors: Multiset<Color>
    var largestSpread: Double
    var componentWithLargestSpread = 0

    init {
      this.colors = colors
      largestSpread = -1.0
      for (component in 0..2) {
        val componentSpread = getComponentSpread(component)
        if (componentSpread > largestSpread) {
          largestSpread = componentSpread
          componentWithLargestSpread = component
        }
      }
    }

    fun getComponentSpread(component: Int): Double {
      var min = Double.POSITIVE_INFINITY
      var max = Double.NEGATIVE_INFINITY
      for (color in colors) {
        min = min(min, color.getComponent(component))
        max = max(max, color.getComponent(component))
      }
      return max - min
    }

    fun split(): Collection<Cluster> {
      val orderedColors: List<Color> = ArrayList(colors).sortedWith(ColorComponentComparator(componentWithLargestSpread))
      val medianIndex = orderedColors.size / 2
      return listOf(
        Cluster(HashMultiset(orderedColors.subList(0, medianIndex))),
        Cluster(
          HashMultiset(orderedColors.subList(medianIndex, orderedColors.size))
        )
      )
    }
  }

  /**
   * Orders clusters according to their maximum spread, in descending order.
   */
  class ClusterSpreadComparator : Comparator<Cluster> {
    override fun compare(a: Cluster, b: Cluster): Int {
      val spreadDifference = b.largestSpread - a.largestSpread
      return if (spreadDifference == 0.0) {
        ArbitraryComparator.INSTANCE.compare(a, b)
      } else spreadDifference.sign.toInt()
    }
  }

  /**
   * Orders colors according to the value of one particular component, in ascending order.
   */
  internal class ColorComponentComparator(val component: Int) : Comparator<Color> {
    override fun compare(a: Color, b: Color): Int {
      val componentDifference: Double = a.getComponent(component) - b.getComponent(component)
      return if (componentDifference == 0.0) {
        ArbitraryComparator.INSTANCE.compare(a, b)
      } else componentDifference.sign.toInt()
    }
  }

  companion object {
    val INSTANCE = MedianCutQuantizer()
  }
}