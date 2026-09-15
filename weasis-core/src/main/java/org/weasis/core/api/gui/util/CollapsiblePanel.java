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

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CollapsiblePanel extends JPanel {
  private final JPanel content = new JPanel(new BorderLayout());
  private boolean collapsed;

  public CollapsiblePanel(String title) {
    super(new BorderLayout());
    JButton toggle = new JButton(title == null ? "" : title);
    toggle.addActionListener(e -> setCollapsed(!collapsed));
    add(toggle, BorderLayout.NORTH);
    add(content, BorderLayout.CENTER);
  }

  public void setContent(Component component) {
    content.removeAll();
    if (component != null) {
      content.add(component, BorderLayout.CENTER);
    }
    revalidate();
  }

  public boolean isCollapsed() {
    return collapsed;
  }

  public void setCollapsed(boolean collapsed) {
    this.collapsed = collapsed;
    content.setVisible(!collapsed);
  }
}
