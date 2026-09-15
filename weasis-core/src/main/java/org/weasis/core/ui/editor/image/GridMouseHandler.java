/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

/** Map a click in a view grid to a cell index. Not attached to layout mouse handling. */
public class GridMouseHandler {

  public int cellAt(int width, int height, int rows, int cols, int x, int y) {
    if (invalid(width, height, rows, cols)) {
      return -1;
    }
    int col = clamp(x / cellSize(width, cols), cols);
    int row = clamp(y / cellSize(height, rows), rows);
    return row * cols + col;
  }

  boolean invalid(int width, int height, int rows, int cols) {
    return badSize(width, height) || badGrid(rows, cols);
  }

  boolean badSize(int width, int height) {
    return width <= 0 || height <= 0;
  }

  boolean badGrid(int rows, int cols) {
    return rows <= 0 || cols <= 0;
  }

  int cellSize(int span, int count) {
    return Math.max(1, span / count);
  }

  int clamp(int value, int count) {
    if (value < 0) {
      return 0;
    }
    if (value >= count) {
      return count - 1;
    }
    return value;
  }
}
