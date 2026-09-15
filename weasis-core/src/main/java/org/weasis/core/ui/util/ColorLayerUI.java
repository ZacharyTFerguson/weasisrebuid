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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class ColorLayerUI {
  private final JComponent parent;
  private boolean showing;

  public ColorLayerUI(JComponent parent) {
    this.parent = parent;
  }

  public static ColorLayerUI createTransparentLayerUI(JComponent parent) {
    return new ColorLayerUI(parent);
  }

  public void showUI() {
    showing = true;
    if (parent != null) {
      parent.repaint();
    }
  }

  public void hideUI() {
    showing = false;
    if (parent != null) {
      parent.repaint();
    }
  }

  public void paint(Graphics g) {
    if (!showing || g == null || parent == null) {
      return;
    }
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
    g2.setColor(Color.BLACK);
    g2.fillRect(0, 0, parent.getWidth(), parent.getHeight());
    g2.dispose();
  }
}
