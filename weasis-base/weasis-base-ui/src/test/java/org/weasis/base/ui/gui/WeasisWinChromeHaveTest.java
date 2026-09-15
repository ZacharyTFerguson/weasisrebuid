/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.ui.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.util.AppProperties;
import org.weasis.core.ui.util.ToolBarContainer;

class WeasisWinChromeHaveTest {

  @Test
  void windowTitleIncludesVersionAndDefaultToolbarsArePresent() {
    String title = WeasisWin.windowTitle();
    assertTrue(title.startsWith(AppProperties.WEASIS_NAME));
    assertTrue(title.contains(AppProperties.WEASIS_VERSION));
    ToolBarContainer bars = WeasisWin.createDefaultToolBars();
    assertTrue(bars.getComponentCount() >= 4);
  }

  @Test
  void headedWindowHasHelpMenuAndImportToolbarButton() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    try {
      assertEquals(WeasisWin.windowTitle(), win.getTitle());
      assertEquals(2, win.getJMenuBar().getMenuCount());
      assertEquals("File", win.getJMenuBar().getMenu(0).getText());
      assertEquals("Help", win.getJMenuBar().getMenu(1).getText());
      assertTrue(win.getToolBarContainer().getComponentCount() >= 5);
    } finally {
      win.dispose();
    }
  }
}
