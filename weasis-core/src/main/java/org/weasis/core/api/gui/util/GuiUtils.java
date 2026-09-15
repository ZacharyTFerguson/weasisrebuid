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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public final class GuiUtils {
  private GuiUtils() {}

  public static JPanel getVerticalBoxLayoutPanel() {
    JPanel p = new JPanel();
    p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
    return p;
  }

  public static JPanel getFlowLayoutPanel(Component... comps) {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));
    if (comps != null) {
      for (Component c : comps) {
        p.add(c);
      }
    }
    return p;
  }

  public static TitledBorder getTitledBorder(String title) {
    return BorderFactory.createTitledBorder(title);
  }

  public static Component boxVerticalStrut(int height) {
    return Box.createVerticalStrut(height);
  }

  public static GridBagConstraints getGridBagConstraints(int x, int y) {
    return new GridBagConstraints(
        x,
        y,
        1,
        1,
        0,
        0,
        GridBagConstraints.WEST,
        GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2),
        0,
        0);
  }

  public static Dimension getDimension(int w, int h) {
    return new Dimension(w, h);
  }
}
