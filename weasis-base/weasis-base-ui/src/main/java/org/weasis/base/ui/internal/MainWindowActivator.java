/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.ui.internal;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.weasis.base.ui.gui.DummySeriesViewerFactory;
import org.weasis.core.api.gui.util.GuiExecutor;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.pref.DefaultPrefBootstrap;
import org.weasis.core.ui.pref.PreferenceDialog;

/** UI aggregator named by {@code weasis.main.ui}. WP-1 opens a dummy {@link ViewerPlugin}. */
public class MainWindowActivator implements BundleActivator {

  private JFrame window;

  @Override
  public void start(BundleContext context) {
    DummySeriesViewerFactory factory = new DummySeriesViewerFactory();
    factory.activate();
    DefaultPrefBootstrap.ensureRegistered(UICore.getInstance());
    if (GraphicsEnvironment.isHeadless()) {
      UICore.getInstance().openBlankViewer(factory, new Hashtable<>());
      return;
    }
    String title = System.getProperty("weasis.name", "Weasis");
    GuiExecutor.invokeAndWait(
        () -> {
          window = new JFrame(title);
          window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
          window.setSize(960, 640);
          window.setLayout(new BorderLayout());
          window.setJMenuBar(menuBar(window));
          ViewerPlugin<?> plugin = UICore.getInstance().openBlankViewer(factory, new Hashtable<>());
          window.add(plugin, BorderLayout.CENTER);
          UICore.getInstance().setApplicationWindow(window);
          window.setLocationRelativeTo(null);
          window.setVisible(true);
        });
  }

  @Override
  public void stop(BundleContext context) {
    JFrame frame = window;
    UICore.getInstance().setApplicationWindow(null);
    if (frame != null) {
      SwingUtilities.invokeLater(frame::dispose);
    }
  }

  static JMenuBar menuBar(JFrame frame) {
    JMenuBar bar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenuItem prefs = new JMenuItem("Preferences");
    prefs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK));
    prefs.addActionListener(
        e -> {
          PreferenceDialog dialog = new PreferenceDialog(frame);
          dialog.setLocationRelativeTo(frame);
          dialog.setVisible(true);
        });
    file.add(prefs);
    bar.add(file);
    return bar;
  }
}
