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
 * Uses k-means clustering for color quantization. This tends to yield good results, but convergence
 * can be slow. It is not recommended for large images.
 */
class KMeansQuantizer private constructor() : ColorQuantizer {
  override fun quantize(originalColors: Multiset<Color>, maxColorCount: Int): Set<Color> {
    val clustersByCentroid: MutableMap<Color, Multiset<Color>> = HashMap()
    val centroidsToRecompute: MutableSet<Color> = getInitialCentroids(originalColors, maxColorCount)
    for (centroid in centroidsToRecompute) {
      clustersByCentroid[centroid] = HashMultiset()
    }
    for (color in originalColors.distinctElements) {
      val count: Int = originalColors.count(color)
      clustersByCentroid[color.getNearestColor(centroidsToRecompute)]!!.add(color, count)
    }
    while (centroidsToRecompute.isNotEmpty()) {
      recomputeCentroids(clustersByCentroid, centroidsToRecompute)
      centroidsToRecompute.clear()
      val allCentroids: Set<Color> = clustersByCentroid.keys
      for (centroid in clustersByCentroid.keys) {
        val cluster: Multiset<Color> = clustersByCentroid[centroid]!!
        for (color in ArrayList(cluster.distinctElements)) {
          val newCentroid: Color = color.getNearestColor(allCentroids)
          if (newCentroid !== centroid) {
            val count: Int = cluster.count(color)
            val newCluster: Multiset<Color> = clustersByCentroid[newCentroid]!!
            cluster.remove(color, count)
            newCluster.add(color, count)
            centroidsToRecompute.add(centroid)
            centroidsToRecompute.add(newCentroid)
          }
        }
      }
    }
    return clustersByCentroid.keys
  }

  companion object {
    val INSTANCE = KMeansQuantizer()
    private fun recomputeCentroids(
      clustersByCentroid: MutableMap<Color, Multiset<Color>>,
      centroidsToRecompute: Set<Color>
    ) {
      for (oldCentroid in centroidsToRecompute) {
        val cluster: Multiset<Color> = clustersByCentroid[oldCentroid]!!
        val newCentroid: Color = Color.getCentroid(cluster)
        clustersByCentroid.remove(oldCentroid)
        clustersByCentroid[newCentroid] = cluster
      }
    }

    private fun getInitialCentroids(originalColors: Multiset<Color>, maxColorCount: Int): MutableSet<Color> {
      // We use the Forgy initialization method: choose random colors as initial cluster centroids.
      val colorList: List<Color> = ArrayList(originalColors.distinctElements).shuffled()
      return colorList.take(maxColorCount).toMutableSet()
    }
  }
}