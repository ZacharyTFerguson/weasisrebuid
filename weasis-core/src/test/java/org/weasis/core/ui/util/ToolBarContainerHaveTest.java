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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;

class ToolBarContainerHaveTest {

  @Test
  void preferredHeightIncludesWrappedRows() {
    ToolBarContainer bars = new ToolBarContainer();
    for (int i = 0; i < 8; i++) {
      WtoolBar bar = new WtoolBar("bar" + i, i);
      bar.add(new JButton("XXXXXXXX"));
      bars.registerToolBar(bar);
    }
    JPanel parent = new JPanel(new BorderLayout());
    parent.setSize(220, 400);
    parent.add(bars, BorderLayout.NORTH);
    bars.setSize(220, 10);
    Dimension preferred = bars.getPreferredSize();
    assertTrue(
        preferred.height >= 40, "wrapped Star/Filter row must be visible, h=" + preferred.height);
  }
}
