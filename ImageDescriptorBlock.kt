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

internal object ImageDescriptorBlock {
  private const val IMAGE_SEPARATOR = 0x2C
  private const val LOCAL_COLOR_TABLE_FLAG = 1 shl 7
  private const val INTERLACE_FLAG = 1 shl 6
  private const val SORT_FLAG = 1 shl 5
  fun write(
    outputStream: OutputStream, imageLeft: Int, imageTop: Int, imageWidth: Int,
    imageHeight: Int, localColorTable: Boolean, interlace: Boolean, sort: Boolean,
    localColorTableSize: Int
  ) {
    outputStream.write(IMAGE_SEPARATOR)
    Streams.writeShort(outputStream, imageLeft)
    Streams.writeShort(outputStream, imageTop)
    Streams.writeShort(outputStream, imageWidth)
    Streams.writeShort(outputStream, imageHeight)

    // Packed fields.
    outputStream.write(
      (if (localColorTable) LOCAL_COLOR_TABLE_FLAG else 0)
        or (if (interlace) INTERLACE_FLAG else 0)
        or (if (sort) SORT_FLAG else 0)
        or localColorTableSize
    )
  }
}