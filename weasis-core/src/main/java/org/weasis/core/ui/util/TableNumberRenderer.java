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

import java.text.NumberFormat;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class TableNumberRenderer extends DefaultTableCellRenderer {
  private final NumberFormat format = NumberFormat.getInstance();

  public TableNumberRenderer() {
    setHorizontalAlignment(SwingConstants.RIGHT);
  }

  @Override
  protected void setValue(Object value) {
    if (value instanceof Number n) {
      setText(format.format(n));
    } else {
      super.setValue(value);
    }
  }
}
