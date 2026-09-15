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
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.List;
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
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.editor.image.ViewerToolBar;
import org.weasis.core.ui.editor.image.ZoomToolBar;
import org.weasis.core.ui.pref.PreferenceDialog;
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
  private DataExplorerView explorerView;

  public WeasisWin() {
    super(windowTitle());
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
    dockingControl = new CControl(this);
    explorerDock = uncloseableDock("explorer", "Explorer", explorerHost);
    viewerDock = uncloseableDock("viewer", "Viewer", viewerTabs);
    deployExplorerAndViewer();
    add(dockingControl.getContentArea(), BorderLayout.CENTER);
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
    grid.add(1, 0, 3, 1, viewerDock);
    dockingControl.getContentArea().deploy(grid);
  }

  void addImportButton() {
    JButton importBtn = new JButton("Import DICOM");
    importBtn.setName("import-dicom");
    importBtn.addActionListener(e -> openImportDialog(false));
    toolbars.add(importBtn);
  }

  void onViewerTabChanged() {
    Component selected = viewerTabs.getSelectedComponent();
    if (selected instanceof ViewerPlugin<?> plugin) {
      UICore.getInstance().setSelectedViewerPlugin(plugin);
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
    if (image != null) {
      image.setLayoutCount(count);
    }
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
    Component selected = viewerTabs.getSelectedComponent();
    if (!(selected instanceof ImageViewerPlugin<?> image)) {
      return null;
    }
    UICore.getInstance().setSelectedViewerPlugin(image);
    return image;
  }

  JMenu createHelpMenu() {
    JMenu help = new JMenu("Help");
    JMenuItem about = new JMenuItem("About");
    about.addActionListener(e -> new WeasisAboutBox(this).setVisible(true));
    help.add(about);
    JMenuItem licenses = new JMenuItem("Licenses");
    licenses.addActionListener(e -> new LicencesDialog(this).setVisible(true));
    help.add(licenses);
    return help;
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
    Hashtable<String, Object> props = new Hashtable<>();
    props.put("title", cd ? "DICOM CD" : "DICOM");
    if (explorerView != null && explorerView.getDataExplorerModel() != null) {
      props.put("model", explorerView.getDataExplorerModel());
    }
    JDialog dialog = new JDialog(this, cd ? "Import DICOM CD" : "Import DICOM", true);
    JPanel panel = new JPanel(new BorderLayout());
    for (DicomImportFactory factory : UICore.getInstance().getDicomImportFactories()) {
      ImportDicom page = factory.createDicomImportPage(props);
      if (page instanceof Component component) {
        panel.add(component, BorderLayout.CENTER);
        break;
      }
    }
    dialog.setContentPane(panel);
    dialog.setSize(480, 320);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }
}
