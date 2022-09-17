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

internal object Streams {
  fun writeShort(outputStream: OutputStream, n: Int) {
    // Little-endian encoded.
    outputStream.write(n)
    outputStream.write(n ushr 8)
  }

  fun writeRgb(outputStream: OutputStream, rgb: Int) {
    outputStream.write(rgb ushr 16) // r
    outputStream.write(rgb ushr 8) // g
    outputStream.write(rgb) // b
  }

  fun writeAsciiString(outputStream: OutputStream, string: String) {
    for (character in string.toCharArray()) {
      outputStream.write(character.code)
    }
  }
}