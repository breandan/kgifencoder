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

class ImageOptions
/**
 * Create a new [ImageOptions] with all the defaults.
 */
{
  var left = 0
  var top = 0
  var quantizer: ColorQuantizer = MedianCutQuantizer.INSTANCE
  var ditherer: Ditherer = FloydSteinbergDitherer.INSTANCE
  var disposalMethod: DisposalMethod = DisposalMethod.UNSPECIFIED
  var delayCentiseconds = 0
  fun setLeft(left: Int): ImageOptions {
    this.left = left
    return this
  }

  fun setTop(top: Int): ImageOptions {
    this.top = top
    return this
  }

  fun setColorQuantizer(quantizer: ColorQuantizer): ImageOptions {
    this.quantizer = quantizer
    return this
  }

  fun setDitherer(ditherer: Ditherer): ImageOptions {
    this.ditherer = ditherer
    return this
  }

  fun setDisposalMethod(disposalMethod: DisposalMethod): ImageOptions {
    this.disposalMethod = disposalMethod
    return this
  }
}