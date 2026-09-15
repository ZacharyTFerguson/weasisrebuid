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

import java.awt.GraphicsEnvironment;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.weasis.base.ui.gui.DummySeriesViewerFactory;
import org.weasis.base.ui.gui.WeasisWin;
import org.weasis.core.api.gui.util.GuiExecutor;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.pref.DefaultPrefBootstrap;

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
    GuiExecutor.invokeAndWait(
        () -> {
          WeasisWin win = new WeasisWin();
          window = win;
          win.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
          UICore.getInstance().setApplicationWindow(win);
          win.attachExplorer();
          ViewerPlugin<?> plugin = UICore.getInstance().openBlankViewer(factory, new Hashtable<>());
          win.attachViewer(plugin);
          win.setLocationRelativeTo(null);
          win.setVisible(true);
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
}
