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
 * For background, see Appendix F of the
 * [GIF spec](http://www.w3.org/Graphics/GIF/spec-gif89a.txt).
 */
internal class LzwEncoder(colorTableSize: Int) {
  private val minimumCodeSize: Int
  private val outputBits: MutableMap<Int, Boolean> = mutableMapOf()
  private var position = 0
  private var codeTable: MutableMap<List<Int>, Int>? = null
  private var codeSize = 0
  private var indexBuffer: List<Int> = ArrayList()

  /**
   * @param colorTableSize Size of the (padded) color table; must be a power of 2
   */
  init {
    if (!GifMath.isPowerOfTwo(colorTableSize)) {
      throw Exception("Color table size must be a power of 2")
    }
    minimumCodeSize = computeMinimumCodeSize(colorTableSize)
    resetCodeTableAndCodeSize()
  }

  fun getMinimumCodeSize(): Int {
    return minimumCodeSize
  }

  fun encode(indices: IntArray): ByteArray {
    writeCode(codeTable!![CLEAR_CODE]!!)
    for (index in indices) {
      processIndex(index)
    }
    writeCode(codeTable!![indexBuffer]!!)
    writeCode(codeTable!![END_OF_INFO]!!)
    return toBytes()
  }

  private fun processIndex(index: Int) {
    val extendedIndexBuffer = append(indexBuffer, index)
    indexBuffer = if (codeTable!!.containsKey(extendedIndexBuffer)) extendedIndexBuffer
    else {
      writeCode(codeTable!![indexBuffer]!!)
      if (codeTable!!.size == MAX_CODE_TABLE_SIZE) {
        writeCode(codeTable!![CLEAR_CODE]!!)
        resetCodeTableAndCodeSize()
      } else addCodeToTable(extendedIndexBuffer)
      listOf(index)
    }
  }

  /**
   * Write the given code to the output stream.
   */
  private fun writeCode(code: Int) {
    for (shift in 0 until codeSize) {
      val bit = code ushr shift and 1 != 0
      outputBits[position++] = bit
    }
  }

  /**
   * Convert our stream of bits into a byte array, as described in the spec.
   */
  private fun toBytes(): ByteArray {
    val bitCount = position
    val result = ByteArray((bitCount + 7) / 8)
    for (i in 0 until bitCount) {
      val byteIndex = i / 8
      val bitIndex = i % 8
      result[byteIndex] = (result[byteIndex].toInt() or ((if (outputBits[i]!!) 1 else 0) shl bitIndex)).toByte()
    }
    return result
  }

  private fun addCodeToTable(indices: List<Int>) {
    val newCode = codeTable!!.size
    codeTable!![indices] = newCode
    if (newCode == 1 shl codeSize) {
      // The next code won't fit in {@code codeSize} bits, so we need to increment.
      ++codeSize
    }
  }

  private fun resetCodeTableAndCodeSize() {
    codeTable = defaultCodeTable()

    // We add an extra bit because of the special "clear" and "end of info" codes.
    codeSize = minimumCodeSize + 1
  }

  private fun defaultCodeTable(): MutableMap<List<Int>, Int> {
    val codeTable: MutableMap<List<Int>, Int> = HashMap()

    // The spec indicates that CLEAR_CODE must have a value of 2**minimumCodeSize. Thus we reserve
    // the first 2**minimumCodeSize codes for colors, even if our color table is smaller.
    val colorsInCodeTable = 1 shl minimumCodeSize
    for (i in 0 until colorsInCodeTable) {
      codeTable[listOf(i)] = i
    }
    codeTable[CLEAR_CODE] = codeTable.size
    codeTable[END_OF_INFO] = codeTable.size
    return codeTable
  }

  companion object {
    // Dummy values to represent special, GIF-specific instructions.
    private val CLEAR_CODE = listOf(-1)
    private val END_OF_INFO = listOf(-2)

    /**
     * The specification stipulates that code size may not exceed 12 bits.
     */
    private const val MAX_CODE_TABLE_SIZE = 1 shl 12

    /**
     * This computes what the spec refers to as "code size". The actual starting code size will be one
     * bit larger than this, because of the special "clear" and "end of info" codes.
     */
    private fun computeMinimumCodeSize(colorTableSize: Int): Int {
      var size = 2 // LZW has a minimum code size of 2.
      while (colorTableSize > 1 shl size) {
        ++size
      }
      return size
    }

    private fun <T> append(list: List<T>, value: T): List<T> {
      val result: ArrayList<T> = ArrayList<T>(list)
      result.add(value)
      return result
    }
  }
}