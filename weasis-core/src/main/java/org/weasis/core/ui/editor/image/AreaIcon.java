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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

/** Filled-square icon used by overlay {@link ViewButton}s. */
public class AreaIcon implements Icon {

  public static final int SIZE = 12;

  private final Color fill;
  private final int size;

  public AreaIcon() {
    this(Color.GREEN, SIZE);
  }

  public AreaIcon(Color fill) {
    this(fill, SIZE);
  }

  public AreaIcon(Color fill, int size) {
    this.fill = fill == null ? Color.GREEN : fill;
    this.size = size <= 0 ? SIZE : size;
  }

  public Color fill() {
    return fill;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      g2.setColor(fill);
      g2.fillRect(x, y, size, size);
    } finally {
      g2.dispose();
    }
  }

  @Override
  public int getIconWidth() {
    return size;
  }

  @Override
  public int getIconHeight() {
    return size;
  }
}
