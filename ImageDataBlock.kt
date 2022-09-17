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

import kotlin.math.min

internal object ImageDataBlock {
  fun write(outputStream: OutputStream, minimumCodeSize: Int, lzwData: ByteArray) {
    outputStream.write(minimumCodeSize)
    var index = 0
    while (index < lzwData.size) {
      val subBlockLength: Int = min(lzwData.size - index, 255)
      outputStream.write(subBlockLength)
      outputStream.write(lzwData.take(subBlockLength))
      index += subBlockLength
    }
    outputStream.write(0)
  }
}