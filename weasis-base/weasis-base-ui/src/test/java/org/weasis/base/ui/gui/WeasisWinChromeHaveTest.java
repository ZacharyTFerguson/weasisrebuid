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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.DataExplorerViewFactory;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.AppProperties;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
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

  @Test
  void dockingHostPutsExplorerLeftOfNamedViewerTabs() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    DataExplorerViewFactory factory = stubExplorerFactory();
    core.registerExplorerFactory(factory);
    try {
      assertEquals("viewer-tabs", win.getViewerTabs().getName());
      assertEquals(2, win.getDockingControl().getCDockableCount());
      assertFalse(win.getExplorerDock().isCloseable());
      assertFalse(win.getViewerDock().isCloseable());
      assertTrue(win.getExplorerDock().isMinimizable());
      assertTrue(win.getExplorerDock().isExternalizable());
      Object center =
          ((BorderLayout) win.getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
      assertTrue(center.getClass().getName().contains("CContentArea"));
      win.attachExplorer();
      assertTrue(
          SwingUtilities.isDescendingFrom(
              (Component) win.getExplorerView(), win.getExplorerHost()));
    } finally {
      core.unregisterInsertableFactory(factory);
      win.dispose();
    }
  }

  @Test
  void viewMenuAndDigitKeysUseFocusedTabPluginNotStaleCoreSelection() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    closeOpen(core);
    core.setApplicationWindow(win);
    ViewerPlugin<?> other = plugin("other");
    LayoutPlugin image = new LayoutPlugin();
    try {
      core.openViewerPlugin(other);
      win.getViewerTabs().addTab("DICOM 2D", image);
      win.getViewerTabs().setSelectedComponent(image);
      win.getJMenuBar().getMenu(1).getItem(2).doClick();
      assertEquals(4, image.layout);
      JPanel dockHost = new JPanel();
      KeyEvent digit = new KeyEvent(dockHost, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_7, '7');
      assertTrue(win.handleViewerKey(digit));
      assertEquals(7, image.lastPreset);
      assertSame(image, core.getSelectedViewerPlugin());
    } finally {
      closeOpen(core);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  static void closeOpen(UICore core) {
    for (ViewerPlugin<?> plugin : List.copyOf(core.getOpenViewerPlugins())) {
      core.closeViewerPlugin(plugin);
    }
  }

  static ViewerPlugin<?> plugin(String name) {
    return new ViewerPlugin<MediaElement>(name) {};
  }

  static final class LayoutPlugin extends ImageViewerPlugin<MediaElement> {
    int layout = 1;
    int lastPreset = -1;

    LayoutPlugin() {
      super("layout");
    }

    @Override
    public void setLayoutCount(int n) {
      layout = n;
    }

    @Override
    public int getLayoutCount() {
      return layout;
    }

    @Override
    public void applyPreset(int index) {
      lastPreset = index;
    }
  }

  static DataExplorerViewFactory stubExplorerFactory() {
    return new DataExplorerViewFactory() {
      @Override
      public DataExplorerView createInstance(Hashtable<String, Object> properties) {
        return new StubExplorer();
      }

      @Override
      public void dispose(Insertable component) {}

      @Override
      public boolean isComponentCreatedByThisFactory(Insertable component) {
        return component instanceof StubExplorer;
      }
    };
  }

  static final class StubExplorer extends JPanel implements DataExplorerView {
    @Override
    public DataExplorerModel getDataExplorerModel() {
      return null;
    }

    @Override
    public void dispose() {}

    @Override
    public String getComponentName() {
      return "stub-explorer";
    }

    @Override
    public int getComponentPosition() {
      return 0;
    }

    @Override
    public void setComponentPosition(int position) {}

    @Override
    public boolean isComponentEnabled() {
      return true;
    }

    @Override
    public void setComponentEnabled(boolean enabled) {}
  }
}
