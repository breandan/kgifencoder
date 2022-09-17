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

class HashMultiset<E> : Multiset<E> {
  private val elementCounts: MutableMap<E, Count> = HashMap<E, Count>()
  override var size = 0
  override fun clear() {
    TODO("Not yet implemented")
  }

  override fun addAll(elements: Collection<E>): Boolean {
    TODO("Not yet implemented")
  }

  override fun isEmpty(): Boolean {
    TODO("Not yet implemented")
  }

  override fun containsAll(elements: Collection<E>): Boolean {
    TODO("Not yet implemented")
  }

  override fun contains(element: E): Boolean {
    TODO("Not yet implemented")
  }

  constructor() {}
  constructor(source: Collection<E>?) {
    addAll(source!!)
  }

  override fun add(element: E, n: Int) {
    val count = elementCounts[element]
    if (count != null) {
      count.value += n
    } else {
      elementCounts[element] = Count(n)
    }
    size += n
  }

  override fun add(element: E): Boolean {
    add(element, 1)
    return true
  }

  override fun remove(element: E, n: Int): Int {
    val count = elementCounts[element] ?: return 0
    if (n < count.value) {
      count.value -= n
      size -= n
      return n
    }
    elementCounts.remove(element)
    size -= count.value
    return count.value
  }

  override fun remove(element: E): Boolean {
    return remove(element, 1) > 0
  }

  override operator fun iterator(): MutableIterator<E> {
    return HashMultisetIterator()
  }

  override fun retainAll(elements: Collection<E>): Boolean {
    TODO("Not yet implemented")
  }

  override fun removeAll(elements: Collection<E>): Boolean {
    TODO("Not yet implemented")
  }

  override fun count(element: E): Int {
    val countOrNull = elementCounts[element]
    return countOrNull?.value ?: 0
  }

  override val distinctElements: Set<E>
    get() = elementCounts.keys

  private inner class HashMultisetIterator() : MutableIterator<E> {
    val distinctElementIterator: Iterator<Map.Entry<E, Count>>
    var currentElement: E? = null
    var currentCount: Int
    var currentElementRemoved = false

    init {
      distinctElementIterator = elementCounts.entries.iterator()
      currentCount = 0
    }

    override fun hasNext(): Boolean {
      return currentCount > 0 || distinctElementIterator.hasNext()
    }

    override fun next(): E {
      if (!hasNext()) {
        throw Exception("iterator has been exhausted")
      }
      if (currentCount == 0) {
        val (key, value) = distinctElementIterator.next()
        currentElement = key
        currentCount = value.value
      }
      --currentCount
      currentElementRemoved = false
      return currentElement!!
    }

    override fun remove() {
      if (currentElement == null) {
        throw Exception("next() has not been called")
      }
      if (currentElementRemoved) {
        throw Exception("remove() already called for current element")
      }
      this@HashMultiset.remove(currentElement)
    }
  }

  private class Count internal constructor(var value: Int)
}