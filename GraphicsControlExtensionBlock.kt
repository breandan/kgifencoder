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

internal object GraphicsControlExtensionBlock {
  private const val USER_INPUT_FLAG = 1 shl 1
  private const val TRANSPARENT_COLOR_FLAG = 1
  private const val EXTENSION_INTRODUCER = 0x21
  private const val GRAPHICS_CONTROL_LABEL = 0xF9
  private const val GRAPHICS_CONTROL_EXTENSION_BLOCK_SIZE = 4
  private const val BLOCK_TERMINATOR = 0
  fun write(
    outputStream: OutputStream, disposalMethod: DisposalMethod, userInput: Boolean,
    transparentColor: Boolean, delayCentiseconds: Int, transparentColorIndex: Int
  ) {
    outputStream.write(EXTENSION_INTRODUCER)
    outputStream.write(GRAPHICS_CONTROL_LABEL)
    outputStream.write(GRAPHICS_CONTROL_EXTENSION_BLOCK_SIZE)

    // Packed field.
    outputStream.write(
      disposalMethod.ordinal shl 3 or (if (userInput) USER_INPUT_FLAG else 0)
        or if (transparentColor) TRANSPARENT_COLOR_FLAG else 0
    )
    Streams.writeShort(outputStream, delayCentiseconds)
    outputStream.write(transparentColorIndex)
    outputStream.write(BLOCK_TERMINATOR)
  }
}