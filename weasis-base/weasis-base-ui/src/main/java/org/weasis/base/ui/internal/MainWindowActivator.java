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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** Empty UI aggregator named by {@code weasis.main.ui}. Stub window is allowed in WP-0. */
public class MainWindowActivator implements BundleActivator {

  private JFrame window;

  @Override
  public void start(BundleContext context) {
    if (GraphicsEnvironment.isHeadless()) {
      return;
    }
    String title = System.getProperty("weasis.name", "Weasis");
    SwingUtilities.invokeLater(
        () -> {
          window = new JFrame(title);
          window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
          window.setSize(960, 640);
          window.add(new JLabel(title + " — WP-0 stub (Felix + core)", SwingConstants.CENTER));
          window.setLocationRelativeTo(null);
          window.setVisible(true);
        });
  }

  @Override
  public void stop(BundleContext context) {
    JFrame frame = window;
    if (frame != null) {
      SwingUtilities.invokeLater(frame::dispose);
    }
  }
}
