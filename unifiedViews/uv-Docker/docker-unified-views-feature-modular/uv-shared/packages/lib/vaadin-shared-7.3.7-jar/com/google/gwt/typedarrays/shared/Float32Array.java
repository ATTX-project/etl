/*
 * Copyright 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.typedarrays.shared;

/**
 * A view representing an {@link ArrayBuffer} as 32-bit floats.
 * 
 * {@link "http://www.khronos.org/registry/typedarray/specs/latest/#7"}
 */
public interface Float32Array extends ArrayBufferView {

  final int BYTES_PER_ELEMENT = 4;

  /**
   * The length in elements of this view.
   * 
   * @return non-negative length
   */
  int length();

  /**
   * Retrieve one element of this view.
   * 
   * @param index
   * @return the requested element
   */
  float get(int index);

  /**
   * Set one element in this view.
   * 
   * @param index
   * @param value
   */
  void set(int index, float value);

  /**
   * Set multiple elements in this view from another view, storing starting at 0.
   * 
   * @param array
   */
  void set(Float32Array array);

  /**
   * Set multiple elements in this view from another view, storing starting at the
   * requested offset.
   * 
   * @param array
   * @param offset 
   */
  void set(Float32Array array, int offset);

  /**
   * Set multiple elements in this view from an array, storing starting at 0.
   * 
   * @param array
   */
  void set(float[] array);

  /**
   * Set multiple elements in this view from an array, storing starting at the
   * requested offset.
   * 
   * @param array
   * @param offset 
   */
  void set(float[] array, int offset);

  /**
   * Create a new view from the same array, from {@code offset} to the end of
   * this view. These offset is clamped to legal indices into this view, so it
   * is not an error to specify an invalid index.
   * 
   * @param begin offset into this view if non-negative; if negative, an index
   *        from the end of this view
   * @return a new {@link Float32Array} instance
   */
  Float32Array subarray(int begin);

  /**
   * Create a new view from the same array, from {@code offset} to (but not
   * including) {@code end} in this view.  These indices are clamped to legal
   * indices into this view, so it is not an error to specify invalid indices.
   * 
   * @param begin offset into this view if non-negative; if negative, an index from
   *     the end of this view
   * @param end offset into this view if non-negative; if negative, an index from
   *     the end of this view
   * @return a new {@link Float32Array} instance
   */
  Float32Array subarray(int begin, int end);
}
