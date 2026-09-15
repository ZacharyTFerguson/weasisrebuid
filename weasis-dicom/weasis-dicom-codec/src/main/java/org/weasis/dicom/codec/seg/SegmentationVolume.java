/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.seg;

import java.awt.geom.Area;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SegmentationVolume {
  private final Map<Integer, LazyContourLoader> segments = new ConcurrentHashMap<>();
  private int rows;
  private int columns;
  private int frames;

  public SegmentationVolume() {}

  public SegmentationVolume(int rows, int columns, int frames) {
    this.rows = rows;
    this.columns = columns;
    this.frames = frames;
  }

  public void putSegment(int number, LazyContourLoader loader) {
    if (loader != null) {
      segments.put(number, loader);
    }
  }

  public List<Area> contours(int segmentNumber, int frame) {
    LazyContourLoader loader = segments.get(segmentNumber);
    return loader == null ? List.of() : loader.getContours(frame);
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public int getFrames() {
    return frames;
  }
}
