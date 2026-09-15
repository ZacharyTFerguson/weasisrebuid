/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class PropertiesDialog extends JDialog {
  private final Properties properties;

  public PropertiesDialog(Window parent, String title, Properties properties) {
    super(parent, title, ModalityType.APPLICATION_MODAL);
    this.properties = properties == null ? new Properties() : properties;
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JTextArea area = new JTextArea(this.properties.toString());
    area.setEditable(false);
    add(new JScrollPane(area), BorderLayout.CENTER);
    JButton close = new JButton("Close");
    close.addActionListener(e -> dispose());
    add(close, BorderLayout.SOUTH);
    pack();
  }

  public Properties getProperties() {
    return properties;
  }
}
