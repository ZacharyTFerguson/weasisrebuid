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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;
import java.util.List;
import javax.swing.JMenu;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.util.AppProperties;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;
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
  void headedWindowHasFileViewHelpMenus() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    try {
      assertEquals(WeasisWin.windowTitle(), win.getTitle());
      assertEquals(3, win.getJMenuBar().getMenuCount());
      assertEquals("File", win.getJMenuBar().getMenu(0).getText());
      assertEquals("View", win.getJMenuBar().getMenu(1).getText());
      assertEquals("Help", win.getJMenuBar().getMenu(2).getText());
      assertTrue(win.getToolBarContainer().getComponentCount() >= 5);
      JMenu view = win.getJMenuBar().getMenu(1);
      assertEquals(4, view.getItemCount());
      assertEquals("Reset", view.getItem(3).getText());
    } finally {
      win.dispose();
    }
  }

  @Test
  void secondPluginAddsATabInsteadOfReplacingCenter() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    core.setApplicationWindow(win);
    ViewerPlugin<?> first = plugin("A");
    ViewerPlugin<?> second = plugin("B");
    try {
      core.openViewerPlugin(first);
      core.openViewerPlugin(second);
      assertEquals(2, win.getViewerTabs().getTabCount());
      assertSame(second, win.getViewerTabs().getSelectedComponent());
      core.closeViewerPlugin(first);
      assertEquals(1, win.getViewerTabs().getTabCount());
      assertSame(second, win.getViewerTabs().getSelectedComponent());
    } finally {
      for (ViewerPlugin<?> plugin : List.copyOf(core.getOpenViewerPlugins())) {
        core.closeViewerPlugin(plugin);
      }
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  static ViewerPlugin<?> plugin(String name) {
    return new ViewerPlugin<MediaElement>(name) {};
  }
}
