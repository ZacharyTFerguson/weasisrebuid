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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class LabelMapContourLoader implements LazyContourLoader {
  private final int rows;
  private final int columns;
  private final byte[][] frames;
  private final int label;

  public LabelMapContourLoader(byte[][] frames, int rows, int columns, int label) {
    this.frames = frames == null ? new byte[0][] : frames;
    this.rows = rows;
    this.columns = columns;
    this.label = label;
  }

  @Override
  public List<Area> getContours(int frame) {
    if (frame < 0 || frame >= frames.length || frames[frame] == null) {
      return List.of();
    }
    byte[] pix = frames[frame];
    List<Area> areas = new ArrayList<>();
    for (int y = 0; y < rows; y++) {
      for (int x = 0; x < columns; x++) {
        int i = y * columns + x;
        if (i < pix.length && (pix[i] & 0xFF) == label) {
          areas.add(new Area(new Rectangle2D.Double(x, y, 1, 1)));
        }
      }
    }
    return areas;
  }
}
