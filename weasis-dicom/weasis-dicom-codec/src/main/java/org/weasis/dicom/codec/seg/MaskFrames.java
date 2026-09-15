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

public class MaskFrames {
  private final int rows;
  private final int columns;
  private final byte[][] frames;

  public MaskFrames(int rows, int columns, byte[][] frames) {
    this.rows = rows;
    this.columns = columns;
    this.frames = frames == null ? new byte[0][] : frames;
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public int size() {
    return frames.length;
  }

  public byte[] getFrame(int index) {
    if (index < 0 || index >= frames.length) {
      return new byte[0];
    }
    return frames[index];
  }
}
