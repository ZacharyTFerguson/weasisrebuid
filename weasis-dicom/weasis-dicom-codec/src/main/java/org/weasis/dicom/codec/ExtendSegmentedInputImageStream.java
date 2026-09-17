/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import java.io.File;
import java.util.Arrays;

/** Encapsulated (JPEG/J2K/RLE) fragment offsets into a Part-10 file. */
public class ExtendSegmentedInputImageStream {
  private final File file;
  private final long[] segmentPositions;
  private final int[] segmentLengths;

  public ExtendSegmentedInputImageStream(File file, long[] segmentPositions, int[] segmentLengths) {
    this.file = file;
    this.segmentPositions = segmentPositions == null ? new long[0] : segmentPositions.clone();
    this.segmentLengths = segmentLengths == null ? new int[0] : segmentLengths.clone();
  }

  public File getFile() {
    return file;
  }

  public long[] getSegmentPositions() {
    return Arrays.copyOf(segmentPositions, segmentPositions.length);
  }

  public int[] getSegmentLengths() {
    return Arrays.copyOf(segmentLengths, segmentLengths.length);
  }

  public int getSegmentCount() {
    return Math.min(segmentPositions.length, segmentLengths.length);
  }
}
