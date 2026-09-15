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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.DataExplorerViewFactory;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.gui.util.AppProperties;
import org.weasis.core.api.gui.util.DynamicMenu;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
import org.weasis.core.ui.editor.image.TabPlacement;
import org.weasis.core.ui.editor.image.ViewTransferHandler;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.pref.ShortcutPrefView;
import org.weasis.core.ui.util.ToolBarContainer;
import org.weasis.core.ui.util.WtoolBar;

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
  void menuAtFindsEditBetweenFileAndView() {
    JMenuBar bar = new JMenuBar();
    bar.add(new JMenu("File"));
    bar.add(new JMenu("Edit"));
    bar.add(new JMenu("View"));
    assertEquals("Edit", WeasisWin.menuAt(bar, "Edit").getText());
    assertEquals("View", WeasisWin.menuAt(bar, "View").getText());
    assertEquals(null, WeasisWin.menuAt(bar, "Tools"));
  }

  @Test
  void headedWindowHasFileEditViewToolsWindowHelpMenus() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    try {
      assertEquals(WeasisWin.windowTitle(), win.getTitle());
      assertEquals(6, win.getJMenuBar().getMenuCount());
      assertEquals("File", win.getJMenuBar().getMenu(0).getText());
      assertEquals("Edit", win.getJMenuBar().getMenu(1).getText());
      assertEquals("View", win.getJMenuBar().getMenu(2).getText());
      assertEquals("Tools", win.getJMenuBar().getMenu(3).getText());
      assertEquals("Window", win.getJMenuBar().getMenu(4).getText());
      assertEquals("Help", win.getJMenuBar().getMenu(5).getText());
      assertTrue(win.getToolBarContainer().getComponentCount() >= 5);
      JMenu view = win.menuNamed("View");
      assertEquals(4, view.getItemCount());
      assertEquals("Reset", view.getItem(3).getText());
      JMenu edit = win.menuNamed("Edit");
      assertEquals("Select All", edit.getItem(0).getText());
      assertEquals("Deselect All", edit.getItem(1).getText());
      assertEquals("Resource Monitor", win.menuNamed("Tools").getItem(0).getText());
      assertEquals("Keyboard Shortcuts", win.menuNamed("Help").getItem(0).getText());
      assertEquals("About", win.menuNamed("Help").getItem(1).getText());
      assertEquals("Licenses", win.menuNamed("Help").getItem(2).getText());
      assertEquals("System resources", win.menuNamed("Help").getItem(3).getText());
    } finally {
      win.dispose();
    }
  }

  @Test
  void helpKeyboardShortcutsShowsLiveMapFromShortcutManager() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    JDialog dialog = null;
    try {
      dialog = win.keyboardShortcutsDialog();
      assertEquals("Keyboard Shortcuts", dialog.getTitle());
      assertEquals("keyboard-shortcuts", dialog.getName());
      ShortcutPrefView map = shortcutMapIn(dialog);
      assertNotNull(map);
      assertEquals("keyboard-shortcuts-map", map.getName());
      assertSame(ActionW.PAN, map.actionFor(KeyEvent.VK_T));
      assertSame(ActionW.WINLEVEL, map.actionFor(KeyEvent.VK_W));
      assertSame(ActionW.CINE, map.actionFor(KeyEvent.VK_C));
      assertSame(ActionW.MEASURE, map.actionFor(KeyEvent.VK_M));
      assertTrue(map.listedRows().stream().anyMatch(row -> row.contains(ActionW.PAN.cmd())));
      assertTrue(map.listedRows().stream().anyMatch(row -> row.contains(ActionW.CINE.cmd())));
    } finally {
      if (dialog != null) {
        dialog.dispose();
      }
      win.dispose();
    }
  }

  @Test
  void helpSystemResourcesShowsHeapAndNativeDiagnostic() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    ResourceMonitorDialog dialog = null;
    try {
      assertEquals("System resources", win.menuNamed("Help").getItem(3).getText());
      dialog = win.systemResourcesDialog();
      assertEquals("System resources", dialog.getTitle());
      assertEquals("system-resources", dialog.getName());
      assertTrue(dialog.statusText().startsWith("Heap "));
      assertTrue(dialog.statusText().contains("Native "));
      assertTrue(dialog.statusText().contains("%"));
    } finally {
      if (dialog != null) {
        dialog.dispose();
      }
      win.dispose();
    }
  }

  static ShortcutPrefView shortcutMapIn(JDialog dialog) {
    for (Component child : dialog.getContentPane().getComponents()) {
      if (child instanceof ShortcutPrefView map) {
        return map;
      }
    }
    return null;
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
      assertSame(first, win.getViewerTabs().getComponentAt(0));
      assertSame(second, win.getViewerTabs().getComponentAt(1));
      assertSame(second, core.getSelectedViewerPlugin());
      assertEquals(0, win.seriesDockCount());
      core.closeViewerPlugin(first);
      assertEquals(1, win.getViewerTabs().getTabCount());
      assertSame(second, core.getSelectedViewerPlugin());
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
      assertEquals(TabPlacement.TOP, win.getViewerTabs().getTabPlacement());
      assertEquals(3, win.getDockingControl().getCDockableCount());
      assertFalse(win.getExplorerDock().isCloseable());
      assertFalse(win.getViewerDock().isCloseable());
      assertFalse(win.getViewerWork().isCloseable());
      assertNotNull(win.getViewerWork());
      assertTrue(win.getExplorerDock().isMinimizable());
      assertTrue(win.getExplorerDock().isExternalizable());
      Object center =
          ((BorderLayout) win.getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
      assertTrue(center.getClass().getName().contains("CContentArea"));
      assertInstanceOf(ViewTransferHandler.class, ((JComponent) center).getTransferHandler());
      assertInstanceOf(ViewTransferHandler.class, win.getViewerTabs().getTransferHandler());
      assertInstanceOf(
          ViewTransferHandler.class, ((JComponent) win.getGlassPane()).getTransferHandler());
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
      core.openViewerPlugin(image);
      win.focusSeries(image);
      win.menuNamed("View").getItem(2).doClick();
      assertEquals(4, image.layout);
      JPanel dockHost = new JPanel();
      KeyEvent digit = new KeyEvent(dockHost, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_7, '7');
      assertTrue(win.handleViewerKey(digit));
      assertEquals(7, image.lastPreset);
      assertSame(image, core.getSelectedViewerPlugin());
      javax.swing.Action key1 = win.getRootPane().getActionMap().get("voi-preset-1");
      assertNotNull(key1);
      key1.actionPerformed(null);
      assertEquals(1, image.lastPreset);
    } finally {
      closeOpen(core);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  @Test
  void editAndWindowMenusDispatchToFocusedPlugin() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    closeOpen(core);
    core.setApplicationWindow(win);
    LayoutPlugin image = new LayoutPlugin();
    try {
      core.openViewerPlugin(image);
      win.focusSeries(image);
      win.menuNamed("Edit").getItem(0).doClick();
      assertTrue(image.selectedAll);
      win.menuNamed("Edit").getItem(1).doClick();
      assertTrue(image.deselectedAll);
      JMenu window = win.menuNamed("Window");
      assertTrue(window instanceof DynamicMenu);
      ((DynamicMenu) window).popupMenuWillBecomeVisible();
      assertEquals("Maximize", window.getItem(0).getText());
      window.getItem(0).doClick();
      assertEquals(ViewerPlugin.DockingState.MAXIMIZED, image.getDockingState());
      assertTrue(window.getItemCount() >= 7);
    } finally {
      closeOpen(core);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  @Test
  void importDialogCloseDoesNotDisposeWeasisWin() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    try {
      win.setVisible(true);
      JDialog dialog = win.importDialog(false);
      assertEquals(WindowConstants.DISPOSE_ON_CLOSE, dialog.getDefaultCloseOperation());
      assertEquals(Dialog.ModalityType.DOCUMENT_MODAL, dialog.getModalityType());
      dialog.setModal(false);
      dialog.setVisible(true);
      win.dispatchEvent(new WindowEvent(win, WindowEvent.WINDOW_CLOSING));
      assertTrue(win.isDisplayable());
      dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
      WeasisWin.disposeImportDialog(dialog);
      assertTrue(win.isDisplayable());
      assertEquals(WindowConstants.DO_NOTHING_ON_CLOSE, win.getDefaultCloseOperation());
    } finally {
      win.dispose();
    }
  }

  @Test
  void externalizeSelectedPluginAddsPerSeriesDockable() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    closeOpen(core);
    core.setApplicationWindow(win);
    ViewerPlugin<?> first = plugin("A");
    ViewerPlugin<?> second = plugin("B");
    try {
      core.openViewerPlugin(first);
      core.openViewerPlugin(second);
      assertEquals(2, win.getViewerTabs().getTabCount());
      assertEquals(0, win.seriesDockCount());
      core.externalizeSelectedPlugin();
      assertEquals(ViewerPlugin.DockingState.EXTERNALIZED, second.getDockingState());
      assertEquals(1, win.getViewerTabs().getTabCount());
      assertEquals(1, win.seriesDockCount());
      assertSame(first, win.getViewerTabs().getComponentAt(0));
      assertSame(second, core.getSelectedViewerPlugin());
      core.normalizeSelectedPlugin();
      assertEquals(ViewerPlugin.DockingState.NORMAL, second.getDockingState());
      assertEquals(2, win.getViewerTabs().getTabCount());
      assertEquals(0, win.seriesDockCount());
      assertSame(second, win.getViewerTabs().getSelectedComponent());
    } finally {
      closeOpen(core);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  @Test
  void remainingSeriesTabsSplitInWorkingArea() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    closeOpen(core);
    core.setApplicationWindow(win);
    ViewerPlugin<?> first = plugin("A");
    ViewerPlugin<?> second = plugin("B");
    try {
      core.openViewerPlugin(first);
      core.openViewerPlugin(second);
      assertEquals(2, win.getViewerTabs().getTabCount());
      win.splitSeries(second);
      assertEquals(1, win.getViewerTabs().getTabCount());
      assertSame(first, win.getViewerTabs().getComponentAt(0));
      assertTrue(win.seriesDockOf(second).isVisible());
      assertSame(win.getViewerWork(), win.seriesDockOf(second).getWorkingArea());
      assertEquals("east", WeasisWin.dockSide(win.seriesDockOf(second)));
      assertEquals(ViewerPlugin.DockingState.NORMAL, first.getDockingState());
      assertEquals(ViewerPlugin.DockingState.NORMAL, second.getDockingState());
      assertEquals(2, core.getOpenViewerPlugins().size());
    } finally {
      closeOpen(core);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  @Test
  void splitSideMapsWorkAreaEdgesAndLeavesCenterUnsplit() {
    Rectangle work = new Rectangle(10, 20, 400, 200);
    assertEquals("east", WeasisWin.splitSide(work, new Point(390, 120)));
    assertEquals("west", WeasisWin.splitSide(work, new Point(20, 120)));
    assertEquals("south", WeasisWin.splitSide(work, new Point(210, 210)));
    assertEquals("north", WeasisWin.splitSide(work, new Point(210, 30)));
    assertEquals(null, WeasisWin.splitSide(work, new Point(210, 120)));
    assertEquals(null, WeasisWin.splitSide(work, new Point(0, 0)));
    assertTrue(WeasisWin.farEnough(new Point(0, 0), new Point(20, 0)));
    assertFalse(WeasisWin.farEnough(new Point(0, 0), new Point(8, 0)));
  }

  @Test
  void remainingSeriesTabSouthSplitUsesWorkingAreaCLocation() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    closeOpen(core);
    core.setApplicationWindow(win);
    ViewerPlugin<?> first = plugin("A");
    ViewerPlugin<?> second = plugin("B");
    try {
      core.openViewerPlugin(first);
      core.openViewerPlugin(second);
      win.splitAt(second, "south");
      assertEquals(1, win.getViewerTabs().getTabCount());
      assertSame(first, win.getViewerTabs().getComponentAt(0));
      assertTrue(win.seriesDockOf(second).isVisible());
      assertSame(win.getViewerWork(), win.seriesDockOf(second).getWorkingArea());
      assertEquals("south", WeasisWin.dockSide(win.seriesDockOf(second)));
      assertEquals(ViewerPlugin.DockingState.NORMAL, first.getDockingState());
    } finally {
      closeOpen(core);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  @Test
  void viewerTabDragToEastEdgeSplitsWorkingArea() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    closeOpen(core);
    core.setApplicationWindow(win);
    ViewerPlugin<?> first = plugin("A");
    ViewerPlugin<?> second = plugin("B");
    try {
      core.openViewerPlugin(first);
      core.openViewerPlugin(second);
      win.setSize(960, 640);
      win.setVisible(true);
      win.validate();
      dragTabToWorkEdge(win, 1, "east");
      assertEquals(1, win.getViewerTabs().getTabCount());
      assertSame(first, win.getViewerTabs().getComponentAt(0));
      assertTrue(win.seriesDockOf(second).isVisible());
      assertSame(win.getViewerWork(), win.seriesDockOf(second).getWorkingArea());
      assertEquals("east", WeasisWin.dockSide(win.seriesDockOf(second)));
    } finally {
      win.setVisible(false);
      closeOpen(core);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  @Test
  void tabClickStaysOnStripAndSeriesDragDoNotSplit() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    closeOpen(core);
    core.setApplicationWindow(win);
    ViewerPlugin<?> first = plugin("A");
    ViewerPlugin<?> second = plugin("B");
    try {
      core.openViewerPlugin(first);
      core.openViewerPlugin(second);
      win.setSize(960, 640);
      win.setVisible(true);
      win.validate();
      clickTab(win, 1);
      assertEquals(2, win.getViewerTabs().getTabCount());
      assertEquals(0, win.seriesDockCount());
      ViewTransferHandler.beginDrag(new Series<>());
      dragTabToWorkEdge(win, 1, "east");
      assertEquals(2, win.getViewerTabs().getTabCount());
      assertEquals(0, win.seriesDockCount());
    } finally {
      ViewTransferHandler.clearDragged();
      win.setVisible(false);
      closeOpen(core);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  static void dragTabToWorkEdge(WeasisWin win, int tab, String side) {
    Point press = tabPoint(win, tab);
    Point release = workEdge(win, side);
    win.getViewerTabs().dispatchEvent(mouse(win.getViewerTabs(), MouseEvent.MOUSE_PRESSED, press));
    win.getViewerTabs()
        .dispatchEvent(mouse(win.getViewerTabs(), MouseEvent.MOUSE_RELEASED, release));
  }

  static void clickTab(WeasisWin win, int tab) {
    Point p = tabPoint(win, tab);
    win.getViewerTabs().dispatchEvent(mouse(win.getViewerTabs(), MouseEvent.MOUSE_PRESSED, p));
    win.getViewerTabs().dispatchEvent(mouse(win.getViewerTabs(), MouseEvent.MOUSE_RELEASED, p));
  }

  static Point tabPoint(WeasisWin win, int tab) {
    Rectangle r = win.getViewerTabs().getBoundsAt(tab);
    if (r == null) {
      return new Point(40 + tab * 80, 8);
    }
    return new Point(r.x + r.width / 2, r.y + r.height / 2);
  }

  static Point workEdge(WeasisWin win, String side) {
    Component tabs = win.getViewerTabs();
    int w = Math.max(2, tabs.getWidth());
    int h = Math.max(2, tabs.getHeight());
    if ("south".equals(side)) {
      return new Point(w / 2, h - 2);
    }
    if ("west".equals(side)) {
      return new Point(2, h / 2);
    }
    if ("north".equals(side)) {
      return new Point(w / 2, Math.min(h - 2, 2));
    }
    return new Point(w - 2, h / 2);
  }

  static MouseEvent mouse(Component c, int id, Point p) {
    return new MouseEvent(
        c, id, 0L, InputEvent.BUTTON1_DOWN_MASK, p.x, p.y, 1, false, MouseEvent.BUTTON1);
  }

  @Test
  void toolbarsFollowFocusedTabSeriesViewerUi() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    WeasisWin win = new WeasisWin();
    UICore core = UICore.getInstance();
    closeOpen(core);
    core.setApplicationWindow(win);
    ViewerPlugin<?> twoD = pluginWithBar("2d", "LUT");
    ViewerPlugin<?> audio = pluginWithBar("au", "Audio");
    try {
      core.openViewerPlugin(twoD);
      assertTrue(hasBar(win.getToolBarContainer(), "LUT"));
      assertFalse(hasBar(win.getToolBarContainer(), "Audio"));
      core.openViewerPlugin(audio);
      assertFalse(hasBar(win.getToolBarContainer(), "LUT"));
      assertTrue(hasBar(win.getToolBarContainer(), "Audio"));
      win.focusSeries(twoD);
      assertTrue(hasBar(win.getToolBarContainer(), "LUT"));
      assertFalse(hasBar(win.getToolBarContainer(), "Audio"));
    } finally {
      closeOpen(core);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  static ViewerPlugin<?> pluginWithBar(String name, String bar) {
    ViewerPlugin<?> plugin = plugin(name);
    plugin.getSeriesViewerUI().getToolBar().add(new WtoolBar(bar, 10));
    return plugin;
  }

  static boolean hasBar(ToolBarContainer bars, String name) {
    for (Component c : bars.getComponents()) {
      if (c instanceof Insertable ins && name.equals(ins.getComponentName())) {
        return true;
      }
    }
    return false;
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
    boolean selectedAll;
    boolean deselectedAll;

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

    @Override
    public void selectAllGraphics() {
      selectedAll = true;
    }

    @Override
    public void deselectAllGraphics() {
      deselectedAll = true;
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
