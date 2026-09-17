/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

/** ECG page layout (2-lead rhythm, 4-lead, or 12-lead default). */
public enum Format {
  TWO(2, 1),
  FOUR(2, 2),
  DEFAULT(3, 4);

  private final int rows;
  private final int columns;

  Format(int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
  }

  public int rows() {
    return rows;
  }

  public int columns() {
    return columns;
  }

  public int leadCount() {
    return rows * columns;
  }

  public static Format forChannelCount(int channels) {
    if (channels <= 2) {
      return TWO;
    }
    if (channels <= 4) {
      return FOUR;
    }
    return DEFAULT;
  }
}
