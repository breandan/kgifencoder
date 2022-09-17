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

internal object NetscapeLoopingExtensionBlock {
  private const val EXTENSION_INTRODUCER = 0x21
  private const val APPLICATION_EXTENSION = 0xFF
  private const val APPLICATION = "NETSCAPE2.0"
  private const val SUB_BLOCK_SIZE = 3
  private const val SUB_BLOCK_ID = 1
  private const val BLOCK_TERMINATOR = 0
  fun write(outputStream: OutputStream, loopCount: Int) {
    outputStream.write(EXTENSION_INTRODUCER)
    outputStream.write(APPLICATION_EXTENSION)
    outputStream.write(APPLICATION.length)
    Streams.writeAsciiString(outputStream, APPLICATION)
    outputStream.write(SUB_BLOCK_SIZE)
    outputStream.write(SUB_BLOCK_ID)
    Streams.writeShort(outputStream, loopCount)
    outputStream.write(BLOCK_TERMINATOR)
  }
}