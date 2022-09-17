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

internal object LogicalScreenDescriptorBlock {
  private const val GLOBAL_COLOR_TABLE_FLAG = 1 shl 7
  private const val SORT_FLAG = 1 shl 3
  fun write(
    outputStream: OutputStream, logicalScreenWidth: Int, logicalScreenHeight: Int,
    globalColorTable: Boolean, colorResolution: Int, sort: Boolean, globalColorTableSize: Int,
    backgroundColorIndex: Int, pixelAspectRatio: Int
  ) {
    Streams.writeShort(outputStream, logicalScreenWidth)
    Streams.writeShort(outputStream, logicalScreenHeight)
    outputStream.write(
      (if (globalColorTable) GLOBAL_COLOR_TABLE_FLAG else 0)
        or (colorResolution shl 4)
        or (if (sort) SORT_FLAG else 0)
        or globalColorTableSize
    )
    outputStream.write(backgroundColorIndex)
    outputStream.write(pixelAspectRatio)
  }
}