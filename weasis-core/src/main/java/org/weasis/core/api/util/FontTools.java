/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public final class FontTools {
  private FontTools() {}

  public static Font getFont(FontItem item) {
    return item == null ? FontItem.DEFAULT.getFont() : item.getFont();
  }

  public static int stringWidth(Graphics2D g2, String text) {
    if (g2 == null || text == null) {
      return 0;
    }
    FontMetrics fm = g2.getFontMetrics();
    return fm.stringWidth(text);
  }
}
