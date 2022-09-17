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

import kotlin.jvm.Synchronized

class GifEncoder(outputStream: OutputStream, screenWidth: Int, screenHeight: Int, loopCount: Int) {
  private val outputStream: OutputStream
  private val screenWidth: Int
  private val screenHeight: Int

  /**
   * Start creating a GIF file.
   *
   * @param outputStream the output stream to which the GIF data will be written
   * @param screenWidth the width of the entire graphic
   * @param screenHeight the height of the entire graphic
   * @param loopCount how many times to repeat the animation; use 0 to loop indefinitely
   * @throws IOException if there was a problem writing to the given output stream
   */
  init {
    this.outputStream = outputStream
    this.screenWidth = screenWidth
    this.screenHeight = screenHeight
    HeaderBlock.write(outputStream)
    LogicalScreenDescriptorBlock.write(
      outputStream, screenWidth, screenHeight, false, 1, false, 0,
      0, 0
    )
    NetscapeLoopingExtensionBlock.write(outputStream, loopCount)
  }

  /**
   * Add an image to the GIF file.
   *
   * @param rgbData a grid of pixels in RGB format
   * @param options options to be applied to this image
   * @return this instance for chaining
   * @throws IOException if there was a problem writing to the given output stream
   */
  fun addImage(rgbData: Array<IntArray>, options: ImageOptions): GifEncoder {
    addImage(Image.fromRgb(rgbData), options)
    return this
  }

  /**
   * Add an image to the GIF file.
   *
   * @param rgbData an image buffer in RGB format
   * @param width the number of pixels per row in the pixel array
   * @param options options to be applied to this image
   * @return this instance for chaining
   * @throws IOException if there was a problem writing to the given output stream
   */
  fun addImage(rgbData: IntArray, width: Int, options: ImageOptions): GifEncoder {
    addImage(Image.fromRgb(rgbData, width), options)
    return this
  }

  /**
   * Writes the trailer. This should be called exactly once per GIF file.
   *
   *
   * This method does not close the input stream. We consider it the caller's responsibility to
   * close it at the appropriate time, which often (but not always) will be just after calling this
   * method.
   */
  @Synchronized
  fun finishEncoding() {
    // The trailer block indicates when you've hit the end of the file.
    outputStream.write(0x3B)
  }

  @Synchronized
  fun addImage(image: Image, options: ImageOptions) {
    var image: Image = image
    if (options.left + image.getWidth() > screenWidth
      || options.top + image.getHeight() > screenHeight
    ) {
      throw Exception("Image does not fit in screen.")
    }
    val originalColors: Multiset<Color> = image.getColors()
    var distinctColors: Set<Color> = originalColors.distinctElements
    if (distinctColors.size > MAX_COLOR_COUNT) {
      distinctColors = options.quantizer.quantize(originalColors, MAX_COLOR_COUNT)
      image = options.ditherer.dither(image, distinctColors)
    }
    val colorTable: ColorTable = ColorTable.fromColors(distinctColors)
    val paddedColorCount: Int = colorTable.paddedSize()
    val colorIndices: IntArray = colorTable.getIndices(image)
    GraphicsControlExtensionBlock.write(
      outputStream, options.disposalMethod, false, false,
      options.delayCentiseconds, 0
    )
    ImageDescriptorBlock.write(
      outputStream, options.left, options.top, image.getWidth(),
      image.getHeight(), true, false, false, getColorTableSizeField(paddedColorCount)
    )
    colorTable.write(outputStream)
    val lzwEncoder = LzwEncoder(paddedColorCount)
    val lzwData: ByteArray = lzwEncoder.encode(colorIndices)
    ImageDataBlock.write(outputStream, lzwEncoder.getMinimumCodeSize(), lzwData)
  }

  companion object {
    private const val MAX_COLOR_COUNT = 256

    /**
     * Compute the "size of the color table" field as the spec defines it:
     *
     * <blockquote>this field is used to calculate the number of bytes contained in the Global Color
     * Table. To determine that actual size of the color table, raise 2 to [the value of the field +
     * 1]</blockquote>
     */
    private fun getColorTableSizeField(actualTableSize: Int): Int {
      var size = 0
      while (1 shl size + 1 < actualTableSize) {
        ++size
      }
      return size
    }
  }
}