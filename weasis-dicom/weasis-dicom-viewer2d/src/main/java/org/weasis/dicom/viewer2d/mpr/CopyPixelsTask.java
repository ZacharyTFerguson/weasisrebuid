/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

/** Deep-copies reconstructed MPR samples into a derived raw cache. */
public class CopyPixelsTask {

  public double[][] copy(double[][] src) {
    if (src == null) {
      return null;
    }
    double[][] dest = new double[src.length][];
    for (int y = 0; y < src.length; y++) {
      dest[y] = src[y] == null ? null : src[y].clone();
    }
    return dest;
  }

  public void copyTo(double[][] src, RawImageIO dest) {
    if (dest == null) {
      return;
    }
    dest.setSamples(copy(src));
  }
}
