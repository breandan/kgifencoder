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
 * Provides a stable ordering of objects, such that:
 *
 *
 *  * compare(a, b) == 0 iff a == b
 *  * sign(compare(a, b)) == -sign(compare(b, a))
 *
 *
 *
 * Similar to Guava's {code Ordering.arbitrary()}.
 */
internal class ArbitraryComparator private constructor() : Comparator<Any> {
  override fun compare(a: Any, b: Any): Int {
    if (a === b) return 0
    if (a == null) return -1
    if (b == null) return 1
    val identityHashCodeDifference: Int = a.hashCode() - b.hashCode()
    return if (identityHashCodeDifference != 0) {
      identityHashCodeDifference
    } else getObjectId(a) - getObjectId(
      b
    )

    // We have an identityHashCode collision.
  }

  companion object {
    val INSTANCE = ArbitraryComparator()

    /**
     * If we have no other way to order two objects in a stable manner, we will register both in this
     * map and order them according to their associated values. The map's values are just integers
     * corresponding to the order in which objects were added.
     */
    private val objectIds: HashMap<Any, Int> = HashMap()

    /**
     * Get the ID of an object, adding it to the ID map if it isn't already registered.
     */
    private fun getObjectId(any: Any): Int {
        var id: Int? = objectIds[any]
        if (id == null) {
          id = objectIds.size
          objectIds[any] = id
        }
        return id
    }
  }
}