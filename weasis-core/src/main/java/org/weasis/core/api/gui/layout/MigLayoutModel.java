/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.gui.layout;

public class MigLayoutModel {
  private final LayoutCellManager cells = new LayoutCellManager();
  private int columns = 1;
  private int rows = 1;

  public MigLayoutModel() {}

  public MigLayoutModel(int columns, int rows) {
    this.columns = Math.max(1, columns);
    this.rows = Math.max(1, rows);
  }

  public int getColumns() {
    return columns;
  }

  public int getRows() {
    return rows;
  }

  public LayoutCellManager getCells() {
    return cells;
  }

  public static MigLayoutModel grid(int n) {
    int size = Math.max(1, n);
    int cols = (int) Math.ceil(Math.sqrt(size));
    int rows = (int) Math.ceil(size / (double) cols);
    return new MigLayoutModel(cols, rows);
  }
}
