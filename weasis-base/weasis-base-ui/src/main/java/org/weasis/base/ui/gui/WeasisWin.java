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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.DataExplorerViewFactory;
import org.weasis.core.api.explorer.DicomImportFactory;
import org.weasis.core.api.explorer.ImportDicom;
import org.weasis.core.api.gui.util.AppProperties;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.RotationToolBar;
import org.weasis.core.ui.editor.image.ScreenshotToolBar;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.editor.image.ViewerToolBar;
import org.weasis.core.ui.editor.image.ZoomToolBar;
import org.weasis.core.ui.pref.PreferenceDialog;
import org.weasis.core.ui.util.ToolBarContainer;

/** Main window for {@code weasis.main.ui = weasis-base-ui}. */
public class WeasisWin extends JFrame {

  private final ToolBarContainer toolbars;
  private DataExplorerView explorerView;

  public WeasisWin() {
    super(windowTitle());
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setSize(960, 640);
    setLayout(new BorderLayout());
    setJMenuBar(createMenuBar());
    toolbars = createDefaultToolBars();
    JButton importBtn = new JButton("Import DICOM");
    importBtn.setName("import-dicom");
    importBtn.addActionListener(e -> openImportDialog(false));
    toolbars.add(importBtn);
    add(toolbars, BorderLayout.NORTH);
    addWindowListener(new WeasisWinListener(this));
    UICore.getInstance().installDockingKeyDispatcher();
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

  public DataExplorerView getExplorerView() {
    return explorerView;
  }

  public boolean handleDockingKey(KeyEvent e) {
    return UICore.getInstance().handleDockingKey(e);
  }

  public JMenuBar createMenuBar() {
    JMenuBar bar = new JMenuBar();
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
    bar.add(file);
    JMenu help = new JMenu("Help");
    JMenuItem about = new JMenuItem("About");
    about.addActionListener(e -> new WeasisAboutBox(this).setVisible(true));
    help.add(about);
    bar.add(help);
    return bar;
  }

  public void attachViewer(ViewerPlugin<?> plugin) {
    if (plugin != null) {
      add(plugin, BorderLayout.CENTER);
      revalidate();
      repaint();
    }
  }

  public void attachExplorer() {
    DataExplorerView chosen = null;
    for (DataExplorerViewFactory explorerFactory : UICore.getInstance().getExplorerFactories()) {
      DataExplorerView explorer = explorerFactory.createInstance(new Hashtable<>());
      if (!(explorer instanceof Component)) {
        continue;
      }
      if (explorer.getClass().getName().contains("dicom.explorer.DicomExplorer")) {
        chosen = explorer;
        break;
      }
      if (chosen == null) {
        chosen = explorer;
      }
    }
    if (chosen instanceof Component component) {
      explorerView = chosen;
      component.setPreferredSize(new Dimension(280, 640));
      add(component, BorderLayout.WEST);
      if (chosen.getDataExplorerModel() != null) {
        chosen.getDataExplorerModel().addPropertyChangeListener(new MainWindowListener(this));
      }
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
