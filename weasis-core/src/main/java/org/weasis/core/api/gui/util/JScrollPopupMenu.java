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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class JScrollPopupMenu extends JPopupMenu {
  private final JScrollPane scroll = new JScrollPane();

  public JScrollPopupMenu() {
    super();
    scroll.setBorder(null);
    super.add(scroll);
  }

  @Override
  public Component add(Component comp) {
    scroll.setViewportView(comp);
    return comp;
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    d.height = Math.min(d.height, 320);
    return d;
  }
}
