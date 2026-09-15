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

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.location.AbstractTreeLocation;
import bibliothek.gui.dock.common.location.CWorkingAreaLocation;
import bibliothek.gui.dock.security.GlassedPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.DataExplorerViewFactory;
import org.weasis.core.api.explorer.DicomImportFactory;
import org.weasis.core.api.explorer.ImportDicom;
import org.weasis.core.api.gui.util.AppProperties;
import org.weasis.core.api.gui.util.DynamicMenu;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
import org.weasis.core.ui.editor.image.RotationToolBar;
import org.weasis.core.ui.editor.image.ScreenshotToolBar;
import org.weasis.core.ui.editor.image.TabPlacement;
import org.weasis.core.ui.editor.image.ViewTransferHandler;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.editor.image.ViewerToolBar;
import org.weasis.core.ui.editor.image.ZoomToolBar;
import org.weasis.core.ui.pref.PreferenceDialog;
import org.weasis.core.ui.pref.ShortcutPrefView;
import org.weasis.core.ui.util.TitleMenuItem;
import org.weasis.core.ui.util.ToolBarContainer;

/** Main window for {@code weasis.main.ui = weasis-base-ui}. */
public class WeasisWin extends JFrame {

  private final ToolBarContainer toolbars;
  private final JTabbedPane viewerTabs = new JTabbedPane();
  private final JPanel explorerHost = new JPanel(new BorderLayout());
  private CControl dockingControl;
  private DefaultSingleCDockable explorerDock;
  private DefaultSingleCDockable viewerDock;
  private CWorkingArea viewerWork;
  private DataExplorerView explorerView;
  private final Map<String, DefaultSingleCDockable> seriesDocks = new LinkedHashMap<>();

  public WeasisWin() {
    super(windowTitle());
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setSize(960, 640);
    setLayout(new BorderLayout());
    toolbars = createDefaultToolBars();
    installChrome();
    addWindowListener(new WeasisWinListener(this));
    UICore.getInstance().installDockingKeyDispatcher();
    bindDigitKeys(getRootPane());
  }

  void installChrome() {
    setJMenuBar(createMenuBar());
    addImportButton();
    add(toolbars, BorderLayout.NORTH);
    installDockingHost();
  }

  void installDockingHost() {
    viewerTabs.setName("viewer-tabs");
    TabPlacement.apply(viewerTabs, TabPlacement.TOP);
    viewerTabs.addChangeListener(e -> onViewerTabChanged());
    viewerTabs.addContainerListener(
        new ContainerAdapter() {
          @Override
          public void componentAdded(ContainerEvent e) {
            bindPluginDocking(e.getChild());
          }
        });
    dockingControl = new CControl(this);
    explorerDock = uncloseableDock("explorer", "Explorer", explorerHost);
    viewerDock = uncloseableDock("viewer", "Viewer", viewerTabs);
    viewerWork = dockingControl.createWorkingArea("viewer-work");
    viewerWork.setTitleText("Viewer");
    deployExplorerAndViewer();
    add(dockingControl.getContentArea(), BorderLayout.CENTER);
    bindSeriesDrop();
    bindWorkingFocus();
    bindTabSplit();
  }

  void bindTabSplit() {
    TabSplit split = new TabSplit();
    viewerTabs.addMouseListener(split);
  }

  void bindSeriesDrop() {
    stealIfJc(dockingControl.getContentArea());
    bindDropPath(viewerTabs);
    stealIfJc(getGlassPane());
    stealIfJc(viewerWork.getComponent());
    stealGlassedTree(dockingControl.getContentArea());
  }

  void bindWorkingFocus() {
    dockingControl.addFocusListener(
        new CFocusListener() {
          @Override
          public void focusGained(CDockable dockable) {
            onSeriesFocus(dockable);
          }

          @Override
          public void focusLost(CDockable dockable) {}
        });
  }

  void onSeriesFocus(CDockable dockable) {
    ViewerPlugin<?> plugin = pluginOfDock(dockable);
    if (plugin == null) {
      return;
    }
    UICore.getInstance().setSelectedViewerPlugin(plugin);
    rebindToolBars(plugin);
  }

  void bindDropPath(Component start) {
    Component c = start;
    while (c instanceof JComponent jc) {
      stealDrop(jc);
      stealGlassed(jc);
      c = jc.getParent();
    }
  }

  void stealGlassed(JComponent c) {
    if (c instanceof GlassedPane gp) {
      stealIfJc(gp.getGlassPane());
    }
  }

  void stealGlassedTree(Component c) {
    if (c instanceof JComponent jc) {
      stealGlassed(jc);
    }
    if (c instanceof Container box) {
      for (Component child : box.getComponents()) {
        stealGlassedTree(child);
      }
    }
  }

  void stealIfJc(Component c) {
    if (c instanceof JComponent jc) {
      stealDrop(jc);
    }
  }

  void stealDrop(JComponent c) {
    TransferHandler keep = c.getTransferHandler();
    c.setTransferHandler(null);
    c.setDropTarget(null);
    c.setTransferHandler(keep instanceof ViewTransferHandler ? keep : new ViewTransferHandler());
    ViewTransferHandler.armDrop(c);
    stampPlugin(c);
  }

  static void stampPlugin(JComponent c) {
    ImageViewerPlugin<?> plugin = ImageViewerPlugin.pluginAbove(c);
    if (plugin != null) {
      c.putClientProperty(ImageViewerPlugin.class, plugin);
      c.putClientProperty(plugin.getClass(), plugin);
    }
  }

  static DefaultSingleCDockable uncloseableDock(String id, String title, Component content) {
    DefaultSingleCDockable dock = new DefaultSingleCDockable(id, title, content);
    dock.setCloseable(false);
    dock.setMinimizable(true);
    dock.setExternalizable(true);
    return dock;
  }

  void deployExplorerAndViewer() {
    CGrid grid = new CGrid(dockingControl);
    grid.add(0, 0, 1, 1, explorerDock);
    grid.add(1, 0, 3, 1, viewerWork);
    dockingControl.getContentArea().deploy(grid);
    viewerWork.show(viewerDock);
  }

  void addImportButton() {
    JButton importBtn = new JButton("Import DICOM");
    importBtn.setName("import-dicom");
    importBtn.addActionListener(e -> openImportDialog(false));
    toolbars.add(importBtn);
  }

  void onViewerTabChanged() {
    ViewerPlugin<?> plugin = selectedViewerPlugin();
    if (plugin == null) {
      return;
    }
    UICore.getInstance().setSelectedViewerPlugin(plugin);
    rebindToolBars(plugin);
  }

  ViewerPlugin<?> selectedViewerPlugin() {
    ViewerPlugin<?> focused = pluginOfDock(dockingControl.getFocusedCDockable());
    if (focused != null) {
      return focused;
    }
    Component selected = viewerTabs.getSelectedComponent();
    return selected instanceof ViewerPlugin<?> plugin ? plugin : null;
  }

  ViewerPlugin<?> pluginOfDock(CDockable dockable) {
    for (Map.Entry<String, DefaultSingleCDockable> e : seriesDocks.entrySet()) {
      if (e.getValue() == dockable) {
        return pluginWithUid(e.getKey());
      }
    }
    return null;
  }

  static ViewerPlugin<?> pluginWithUid(String uid) {
    for (ViewerPlugin<?> plugin : UICore.getInstance().getOpenViewerPlugins()) {
      if (uid.equals(plugin.getDockableUID())) {
        return plugin;
      }
    }
    return null;
  }

  void rebindToolBars(ViewerPlugin<?> plugin) {
    if (plugin.getSeriesViewerUI() == null) {
      return;
    }
    toolbars.replaceViewerBars(plugin.getSeriesViewerUI().getToolBar(), plugin);
  }

  void bindPluginDocking(Component child) {
    if (!(child instanceof ViewerPlugin<?> plugin) || dockingBound(plugin)) {
      return;
    }
    plugin.putClientProperty("dockingBound", Boolean.TRUE);
    plugin.addPropertyChangeListener("dockingState", e -> applyDockingState(plugin));
    plugin.addPropertyChangeListener("closed", e -> removeSeriesDock(plugin));
    bindSeriesDrop();
  }

  DefaultSingleCDockable ensureSeriesDock(ViewerPlugin<?> plugin) {
    DefaultSingleCDockable dock = seriesDocks.get(plugin.getDockableUID());
    if (dock != null) {
      return dock;
    }
    dock = seriesDock(plugin);
    seriesDocks.put(plugin.getDockableUID(), dock);
    if (dockingControl != null) {
      dockingControl.addDockable(dock);
    }
    return dock;
  }

  static boolean dockingBound(ViewerPlugin<?> plugin) {
    return Boolean.TRUE.equals(plugin.getClientProperty("dockingBound"));
  }

  void applyDockingState(ViewerPlugin<?> plugin) {
    if (plugin.getDockingState() == ViewerPlugin.DockingState.EXTERNALIZED) {
      floatPlugin(plugin);
      return;
    }
    if (plugin.getDockingState() == ViewerPlugin.DockingState.NORMAL) {
      restorePlugin(plugin);
    }
  }

  void floatPlugin(ViewerPlugin<?> plugin) {
    if (plugin == null) {
      return;
    }
    viewerTabs.remove(plugin);
    DefaultSingleCDockable dock = ensureSeriesDock(plugin);
    dock.setLocation(CLocation.external(80, 80, 640, 480));
    dock.setVisible(true);
    UICore.getInstance().setSelectedViewerPlugin(plugin);
  }

  static DefaultSingleCDockable seriesDock(ViewerPlugin<?> plugin) {
    DefaultSingleCDockable dock =
        new DefaultSingleCDockable(plugin.getDockableUID(), plugin.getPluginName(), plugin);
    dock.setCloseable(true);
    dock.setMinimizable(true);
    dock.setExternalizable(true);
    return dock;
  }

  void restorePlugin(ViewerPlugin<?> plugin) {
    if (plugin == null) {
      return;
    }
    removeSeriesDock(plugin);
    reinsertTab(plugin);
  }

  void splitSeries(ViewerPlugin<?> plugin) {
    splitAt(plugin, "east");
  }

  void splitAt(ViewerPlugin<?> plugin, String side) {
    splitSeries(plugin, workingLocation(side));
  }

  void splitAtScreen(ViewerPlugin<?> plugin, Point screen) {
    splitSeries(plugin, locationAtScreen(screen));
  }

  void splitSeries(ViewerPlugin<?> plugin, CLocation loc) {
    if (!canSplit(plugin, loc)) {
      return;
    }
    viewerTabs.remove(plugin);
    DefaultSingleCDockable dock = ensureSeriesDock(plugin);
    dock.setWorkingArea(viewerWork);
    dock.setLocation(loc);
    dock.setVisible(true);
  }

  boolean canSplit(ViewerPlugin<?> plugin, CLocation loc) {
    return plugin != null && loc != null && viewerWork != null && otherTabsRemain(plugin);
  }

  boolean otherTabsRemain(ViewerPlugin<?> plugin) {
    int n = viewerTabs.getTabCount();
    return viewerTabs.indexOfComponent(plugin) >= 0 ? n > 1 : n >= 1;
  }

  CLocation workingLocation(String side) {
    if (side == null || viewerWork == null) {
      return null;
    }
    return sideLocation(CLocation.working(viewerWork), side);
  }

  static CLocation sideLocation(CWorkingAreaLocation base, String side) {
    if (base == null || side == null) {
      return null;
    }
    return namedSide(base, side);
  }

  static CLocation namedSide(CWorkingAreaLocation base, String side) {
    return switch (side) {
      case "east" -> base.east(0.5);
      case "west" -> base.west(0.5);
      case "south" -> base.south(0.5);
      case "north" -> base.north(0.5);
      default -> null;
    };
  }

  CLocation locationAtScreen(Point screen) {
    Component work = workComponent();
    if (work == null || screen == null || !work.isShowing()) {
      return null;
    }
    return locationAt(screenBox(work), screen);
  }

  Component workComponent() {
    return viewerWork == null ? null : viewerWork.getComponent();
  }

  static Rectangle screenBox(Component work) {
    Point origin = work.getLocationOnScreen();
    return new Rectangle(origin.x, origin.y, work.getWidth(), work.getHeight());
  }

  CLocation locationAt(Rectangle work, Point local) {
    return workingLocation(splitSide(work, local));
  }

  static String splitSide(Rectangle work, Point local) {
    if (work == null || local == null || !work.contains(local)) {
      return null;
    }
    return nearestEdge(work.width, work.height, local.x - work.x, local.y - work.y);
  }

  static String nearestEdge(int width, int height, int x, int y) {
    if (width <= 0 || height <= 0) {
      return null;
    }
    return edgeName(x / (double) width, y / (double) height);
  }

  static String edgeName(double fx, double fy) {
    double west = fx;
    double east = 1.0 - fx;
    double north = fy;
    double south = 1.0 - fy;
    double min = Math.min(Math.min(west, east), Math.min(north, south));
    if (min > 0.25) {
      return null;
    }
    return namedEdge(west, east, north, south, min);
  }

  static String namedEdge(double west, double east, double north, double south, double min) {
    if (min == west) {
      return "west";
    }
    if (min == east) {
      return "east";
    }
    if (min == north) {
      return "north";
    }
    return "south";
  }

  static String dockSide(DefaultSingleCDockable dock) {
    CLocation loc = dock == null ? null : dock.getBaseLocation();
    if (loc instanceof AbstractTreeLocation tree) {
      return tree.getSide().name().toLowerCase(Locale.ROOT);
    }
    return sideToken(loc == null ? null : loc.toString());
  }

  static String sideToken(String text) {
    if (text == null) {
      return null;
    }
    String u = text.toUpperCase(Locale.ROOT);
    return namedToken(u);
  }

  static String namedToken(String u) {
    if (u.contains(" EAST ")) {
      return "east";
    }
    if (u.contains(" WEST ")) {
      return "west";
    }
    if (u.contains(" SOUTH ")) {
      return "south";
    }
    if (u.contains(" NORTH ")) {
      return "north";
    }
    return u;
  }

  void reinsertTab(ViewerPlugin<?> plugin) {
    if (viewerTabs.indexOfComponent(plugin) < 0) {
      viewerTabs.addTab(plugin.getPluginName(), plugin);
    }
    viewerTabs.setSelectedComponent(plugin);
  }

  void removeSeriesDock(ViewerPlugin<?> plugin) {
    DefaultSingleCDockable dock = seriesDocks.remove(plugin.getDockableUID());
    if (dock != null && dockingControl != null) {
      dockingControl.removeDockable(dock);
    }
  }

  public static String windowTitle() {
    return AppProperties.WEASIS_NAME + " v" + AppProperties.WEASIS_VERSION;
  }

  public static ToolBarContainer createDefaultToolBars() {
    ToolBarContainer bars = new ToolBarContainer();
    bars.registerToolBar(new ViewerToolBar());
    bars.registerToolBar(new ZoomToolBar());
    bars.registerToolBar(new RotationToolBar());
    bars.registerToolBar(new ScreenshotToolBar());
    return bars;
  }

  public ToolBarContainer getToolBarContainer() {
    return toolbars;
  }

  public JTabbedPane getViewerTabs() {
    return viewerTabs;
  }

  public CControl getDockingControl() {
    return dockingControl;
  }

  public JPanel getExplorerHost() {
    return explorerHost;
  }

  public DefaultSingleCDockable getExplorerDock() {
    return explorerDock;
  }

  public DefaultSingleCDockable getViewerDock() {
    return viewerDock;
  }

  public CWorkingArea getViewerWork() {
    return viewerWork;
  }

  public int seriesDockCount() {
    return seriesDocks.size();
  }

  public DefaultSingleCDockable seriesDockOf(ViewerPlugin<?> plugin) {
    return plugin == null ? null : seriesDocks.get(plugin.getDockableUID());
  }

  void focusSeries(ViewerPlugin<?> plugin) {
    DefaultSingleCDockable dock = seriesDockOf(plugin);
    if (dock == null) {
      selectTab(plugin);
      return;
    }
    dock.setVisible(true);
    UICore.getInstance().setSelectedViewerPlugin(plugin);
    rebindToolBars(plugin);
  }

  void selectTab(ViewerPlugin<?> plugin) {
    if (plugin != null && viewerTabs.indexOfComponent(plugin) >= 0) {
      viewerTabs.setSelectedComponent(plugin);
    }
  }

  public DataExplorerView getExplorerView() {
    return explorerView;
  }

  public boolean handleDockingKey(KeyEvent e) {
    return UICore.getInstance().handleDockingKey(e);
  }

  public boolean handleViewerKey(KeyEvent e) {
    return UICore.getInstance().handleViewerKey(e);
  }

  void bindDigitKeys(JComponent root) {
    if (root == null) {
      return;
    }
    InputMap inputs = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actions = root.getActionMap();
    bindDigitRange(inputs, actions, KeyEvent.VK_0);
    bindDigitRange(inputs, actions, KeyEvent.VK_NUMPAD0);
  }

  void bindDigitRange(InputMap inputs, ActionMap actions, int base) {
    for (int d = 0; d <= 9; d++) {
      bindDigit(inputs, actions, base + d, d);
    }
  }

  void bindDigit(InputMap inputs, ActionMap actions, int keyCode, int index) {
    String name = "voi-preset-" + index;
    inputs.put(KeyStroke.getKeyStroke(keyCode, 0), name);
    actions.put(name, presetAction(index));
  }

  AbstractAction presetAction(int index) {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyPresetFromWindow(index);
      }
    };
  }

  void applyPresetFromWindow(int index) {
    ImageViewerPlugin<?> image = focusedImagePlugin();
    if (image != null) {
      image.applyPreset(index);
    }
  }

  public JMenuBar createMenuBar() {
    JMenuBar bar = new JMenuBar();
    bar.add(createFileMenu());
    bar.add(createEditMenu());
    bar.add(createViewMenu());
    bar.add(createToolsMenu());
    bar.add(createWindowMenu());
    bar.add(createHelpMenu());
    return bar;
  }

  public JMenu menuNamed(String name) {
    return menuAt(getJMenuBar(), name);
  }

  static JMenu menuAt(JMenuBar bar, String name) {
    if (missingBar(bar, name)) {
      return null;
    }
    return firstNamed(bar, name);
  }

  static boolean missingBar(JMenuBar bar, String name) {
    return bar == null || name == null;
  }

  static JMenu firstNamed(JMenuBar bar, String name) {
    for (int i = 0; i < bar.getMenuCount(); i++) {
      JMenu menu = matchMenu(bar.getMenu(i), name);
      if (menu != null) {
        return menu;
      }
    }
    return null;
  }

  static JMenu matchMenu(JMenu menu, String name) {
    if (menu == null || !name.equals(menu.getText())) {
      return null;
    }
    return menu;
  }

  JMenu createFileMenu() {
    JMenu file = new JMenu("File");
    JMenu importMenu = new JMenu("Import");
    JMenuItem importDicom = new JMenuItem("DICOM");
    importDicom.addActionListener(e -> openImportDialog(false));
    JMenuItem importCd = new JMenuItem("DICOM CD");
    importCd.addActionListener(e -> openImportDialog(true));
    importMenu.add(importDicom);
    importMenu.add(importCd);
    file.add(importMenu);
    JMenuItem prefs = new JMenuItem("Preferences");
    prefs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK));
    prefs.addActionListener(
        e -> {
          PreferenceDialog dialog = new PreferenceDialog(this);
          dialog.setLocationRelativeTo(this);
          dialog.setVisible(true);
        });
    file.add(prefs);
    return file;
  }

  JMenu createEditMenu() {
    JMenu edit = new JMenu("Edit");
    edit.add(accelItem("Select All", KeyEvent.VK_A, this::selectAllGraphics));
    edit.add(accelItem("Deselect All", KeyEvent.VK_D, this::deselectAllGraphics));
    return edit;
  }

  void selectAllGraphics() {
    ImageViewerPlugin<?> image = focusedImagePlugin();
    if (image != null) {
      image.selectAllGraphics();
    }
  }

  void deselectAllGraphics() {
    ImageViewerPlugin<?> image = focusedImagePlugin();
    if (image != null) {
      image.deselectAllGraphics();
    }
  }

  JMenu createToolsMenu() {
    JMenu tools = new JMenu("Tools");
    JMenuItem monitor = new JMenuItem("Resource Monitor");
    monitor.setName("resource-monitor");
    monitor.addActionListener(e -> showResourceMonitor());
    tools.add(monitor);
    return tools;
  }

  void showResourceMonitor() {
    ResourceMonitorDialog dialog = new ResourceMonitorDialog(this);
    dialog.setVisible(true);
  }

  JMenu createWindowMenu() {
    return new DynamicMenu("Window") {
      @Override
      public void popupMenuWillBecomeVisible() {
        fillWindowMenu(this);
      }
    };
  }

  void fillWindowMenu(JMenu menu) {
    if (menu == null) {
      return;
    }
    menu.removeAll();
    addWindowCommands(menu);
    addOpenPluginItems(menu);
  }

  void addWindowCommands(JMenu menu) {
    UICore core = UICore.getInstance();
    menu.add(namedItem("Maximize", core::toggleMaximizeSelectedPlugin));
    menu.add(namedItem("Close", core::closeSelectedPlugin));
    menu.add(namedItem("Externalize", core::externalizeSelectedPlugin));
    menu.add(namedItem("Normalize", core::normalizeSelectedPlugin));
    menu.add(namedItem("Docking List", core::showDockingList));
  }

  void addOpenPluginItems(JMenu menu) {
    List<ViewerPlugin<?>> plugins = UICore.getInstance().getOpenViewerPlugins();
    if (plugins.isEmpty()) {
      return;
    }
    menu.addSeparator();
    menu.add(new TitleMenuItem("Open"));
    for (ViewerPlugin<?> plugin : plugins) {
      menu.add(pluginItem(plugin));
    }
  }

  JMenuItem pluginItem(ViewerPlugin<?> plugin) {
    String name = plugin == null ? "Viewer" : plugin.getPluginName();
    return namedItem(name, () -> UICore.getInstance().setSelectedViewerPlugin(plugin));
  }

  JMenuItem accelItem(String text, int key, Runnable action) {
    JMenuItem item = namedItem(text, action);
    item.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
    return item;
  }

  JMenuItem namedItem(String text, Runnable action) {
    JMenuItem item = new JMenuItem(text);
    item.setName(text);
    if (action != null) {
      item.addActionListener(e -> action.run());
    }
    return item;
  }

  JMenu createViewMenu() {
    JMenu view = new JMenu("View");
    view.add(layoutItem("1×1", 1));
    view.add(layoutItem("1×2", 2));
    view.add(layoutItem("2×2", 4));
    JMenuItem reset = new JMenuItem("Reset");
    reset.setName("view-reset");
    reset.addActionListener(e -> resetSelectedView());
    view.add(reset);
    return view;
  }

  JMenuItem layoutItem(String name, int count) {
    JMenuItem item = new JMenuItem(name);
    item.setName("layout-" + count);
    item.addActionListener(e -> applyLayout(count));
    return item;
  }

  void applyLayout(int count) {
    ImageViewerPlugin<?> image = focusedImagePlugin();
    if (image == null) {
      return;
    }
    image.setLayoutCount(count);
    image.revalidate();
    image.repaint();
  }

  void resetSelectedView() {
    ImageViewerPlugin<?> image = focusedImagePlugin();
    if (image != null) {
      image.resetDisplay();
    }
  }

  ImageViewerPlugin<?> focusedImagePlugin() {
    ImageViewerPlugin<?> fromTabs = imagePluginFromTabs();
    if (fromTabs != null) {
      return fromTabs;
    }
    return UICore.getInstance().getFocusedImagePlugin();
  }

  ImageViewerPlugin<?> imagePluginFromTabs() {
    ImageViewerPlugin<?> image = ImageViewerPlugin.pluginIn(viewerTabs.getSelectedComponent());
    if (image == null) {
      return null;
    }
    UICore.getInstance().setSelectedViewerPlugin(image);
    return image;
  }

  JMenu createHelpMenu() {
    JMenu help = new JMenu("Help");
    help.add(namedItem("Keyboard Shortcuts", this::showKeyboardShortcuts));
    help.add(namedItem("About", this::showAbout));
    help.add(namedItem("Licenses", this::showLicenses));
    return help;
  }

  void showKeyboardShortcuts() {
    keyboardShortcutsDialog().setVisible(true);
  }

  void showAbout() {
    new WeasisAboutBox(this).setVisible(true);
  }

  void showLicenses() {
    new LicencesDialog(this).setVisible(true);
  }

  JDialog keyboardShortcutsDialog() {
    JDialog dialog = new JDialog(this, "Keyboard Shortcuts", Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setName("keyboard-shortcuts");
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.add(liveShortcutMap(), BorderLayout.CENTER);
    JButton close = new JButton("Close");
    close.addActionListener(e -> dialog.dispose());
    dialog.add(close, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    return dialog;
  }

  static ShortcutPrefView liveShortcutMap() {
    ShortcutPrefView map = new ShortcutPrefView();
    map.setName("keyboard-shortcuts-map");
    return map;
  }

  public void attachViewer(ViewerPlugin<?> plugin) {
    if (plugin != null) {
      UICore.getInstance().openViewerPlugin(plugin);
    }
  }

  public void attachExplorer() {
    DataExplorerView chosen = pickExplorer();
    if (chosen instanceof Component component) {
      dockExplorer(chosen, component);
    }
  }

  DataExplorerView pickExplorer() {
    DataExplorerView chosen = null;
    for (DataExplorerViewFactory explorerFactory : UICore.getInstance().getExplorerFactories()) {
      DataExplorerView explorer = explorerFactory.createInstance(new Hashtable<>());
      chosen = preferExplorer(chosen, explorer);
      if (isDicomExplorer(explorer)) {
        break;
      }
    }
    return chosen;
  }

  static boolean isDicomExplorer(DataExplorerView explorer) {
    return explorer != null
        && explorer.getClass().getName().contains("dicom.explorer.DicomExplorer");
  }

  static DataExplorerView preferExplorer(DataExplorerView chosen, DataExplorerView explorer) {
    if (!(explorer instanceof Component)) {
      return chosen;
    }
    if (isDicomExplorer(explorer)) {
      return explorer;
    }
    return chosen == null ? explorer : chosen;
  }

  void dockExplorer(DataExplorerView chosen, Component component) {
    explorerView = chosen;
    component.setPreferredSize(new Dimension(280, 640));
    explorerHost.removeAll();
    explorerHost.add(component, BorderLayout.CENTER);
    explorerHost.revalidate();
    explorerHost.repaint();
    listenToExplorerModel(chosen);
  }

  void listenToExplorerModel(DataExplorerView chosen) {
    if (chosen.getDataExplorerModel() != null) {
      chosen.getDataExplorerModel().addPropertyChangeListener(new MainWindowListener(this));
    }
  }

  void openImportDialog(boolean cd) {
    JDialog dialog = importDialog(cd);
    dialog.setVisible(true);
    disposeImportDialog(dialog);
  }

  JDialog importDialog(boolean cd) {
    JDialog dialog =
        new JDialog(
            this, cd ? "Import DICOM CD" : "Import DICOM", Dialog.ModalityType.DOCUMENT_MODAL);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setContentPane(importPagePanel(cd));
    dialog.setSize(480, 320);
    dialog.setLocationRelativeTo(this);
    return dialog;
  }

  JPanel importPagePanel(boolean cd) {
    JPanel panel = new JPanel(new BorderLayout());
    addFirstImportPage(panel, importProps(cd));
    return panel;
  }

  Hashtable<String, Object> importProps(boolean cd) {
    Hashtable<String, Object> props = new Hashtable<>();
    props.put("title", cd ? "DICOM CD" : "DICOM");
    if (explorerView != null && explorerView.getDataExplorerModel() != null) {
      props.put("model", explorerView.getDataExplorerModel());
    }
    return props;
  }

  static void addFirstImportPage(JPanel panel, Hashtable<String, Object> props) {
    for (DicomImportFactory factory : UICore.getInstance().getDicomImportFactories()) {
      ImportDicom page = factory.createDicomImportPage(props);
      if (page instanceof Component component) {
        panel.add(component, BorderLayout.CENTER);
        return;
      }
    }
  }

  static void disposeImportDialog(JDialog dialog) {
    if (dialog != null && dialog.isDisplayable()) {
      dialog.dispose();
    }
  }

  @Override
  public void dispose() {
    destroyDocking();
    super.dispose();
  }

  void destroyDocking() {
    CControl control = dockingControl;
    dockingControl = null;
    if (control == null) {
      return;
    }
    try {
      control.destroy();
    } catch (RuntimeException ignored) {
      // already destroyed in tests
    }
  }

  /**
   * Viewer-tab press/release maps onto {@link CLocation} working-area edges. Series hang-fill
   * ({@code dragging()!=null}) is skipped. A click that stays on the tab strip does not split.
   */
  final class TabSplit extends MouseAdapter {
    ViewerPlugin<?> plugin;
    Point press;

    @Override
    public void mousePressed(MouseEvent e) {
      armTab(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      dropTab(e);
    }

    void armTab(MouseEvent e) {
      plugin = null;
      press = null;
      if (!canArmTab(e)) {
        return;
      }
      plugin = pluginAtTab(e);
      press = e.getPoint();
    }

    void dropTab(MouseEvent e) {
      ViewerPlugin<?> tab = plugin;
      Point start = press;
      plugin = null;
      press = null;
      if (tab == null || !canDropTab(e, start)) {
        return;
      }
      splitAtScreen(tab, screenOf(e));
    }
  }

  boolean canArmTab(MouseEvent e) {
    return e != null
        && SwingUtilities.isLeftMouseButton(e)
        && ViewTransferHandler.dragging() == null;
  }

  boolean canDropTab(MouseEvent e, Point start) {
    return e != null
        && ViewTransferHandler.dragging() == null
        && farEnough(start, e.getPoint())
        && viewerTabs.indexAtLocation(e.getX(), e.getY()) < 0;
  }

  ViewerPlugin<?> pluginAtTab(MouseEvent e) {
    return e == null ? null : pluginAt(viewerTabs.indexAtLocation(e.getX(), e.getY()));
  }

  ViewerPlugin<?> pluginAt(int index) {
    if (index < 0) {
      return null;
    }
    Component c = viewerTabs.getComponentAt(index);
    return c instanceof ViewerPlugin<?> p ? p : null;
  }

  static boolean farEnough(Point a, Point b) {
    if (a == null || b == null) {
      return false;
    }
    int dx = a.x - b.x;
    int dy = a.y - b.y;
    return dx * dx + dy * dy >= 256;
  }

  static Point screenOf(MouseEvent e) {
    if (e == null) {
      return null;
    }
    Component c = e.getComponent();
    if (c == null || !c.isShowing()) {
      return e.getPoint();
    }
    return e.getLocationOnScreen();
  }
}
