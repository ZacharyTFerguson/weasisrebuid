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
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.weasis.core.api.gui.util.AppProperties;

public class WeasisAboutBox extends JDialog {
  private final String versionText;

  public WeasisAboutBox(Window parent) {
    super(parent, "About " + AppProperties.WEASIS_NAME, ModalityType.MODELESS);
    setName("about");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    versionText = AppProperties.WEASIS_NAME + " " + AppProperties.WEASIS_VERSION;
    add(body(), BorderLayout.CENTER);
    JButton close = new JButton("Close");
    close.setName("help-about-close");
    close.addActionListener(e -> dispose());
    add(close, BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(parent);
  }

  JPanel body() {
    JLabel version = new JLabel(versionText);
    version.setName("about-version");
    JLabel note = new JLabel("Clean-room rebuild");
    note.setName("about-note");
    JPanel center = new JPanel(new BorderLayout());
    center.setName("about-body");
    center.add(version, BorderLayout.NORTH);
    center.add(note, BorderLayout.CENTER);
    return center;
  }

  public String versionText() {
    return versionText;
  }
}
