/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.gui.util;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

public class DropButtonIcon implements Icon {
  private final Icon icon;

  public DropButtonIcon(Icon icon) {
    this.icon = icon;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    if (icon != null) {
      icon.paintIcon(c, g, x, y);
    }
    int x2 = x + getIconWidth() - 6;
    int y2 = y + getIconHeight() / 2;
    g.drawLine(x2, y2, x2 + 4, y2);
    g.drawLine(x2, y2, x2 + 2, y2 + 3);
    g.drawLine(x2 + 4, y2, x2 + 2, y2 + 3);
  }

  @Override
  public int getIconWidth() {
    return (icon == null ? 16 : icon.getIconWidth()) + 8;
  }

  @Override
  public int getIconHeight() {
    return icon == null ? 16 : icon.getIconHeight();
  }
}
