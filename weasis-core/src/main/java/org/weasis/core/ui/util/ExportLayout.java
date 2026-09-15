/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.util;

public class ExportLayout {
  private int columns = 1;
  private int rows = 1;

  public ExportLayout() {}

  public ExportLayout(int columns, int rows) {
    this.columns = Math.max(1, columns);
    this.rows = Math.max(1, rows);
  }

  public int getColumns() {
    return columns;
  }

  public int getRows() {
    return rows;
  }
}
