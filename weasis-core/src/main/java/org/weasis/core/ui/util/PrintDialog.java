/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.util;

import java.awt.BorderLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

public class PrintDialog extends JDialog {
  private final PrintOptions options = new PrintOptions();
  private boolean accepted;

  public PrintDialog(Window parent) {
    super(parent, "Print", ModalityType.APPLICATION_MODAL);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JCheckBox annotations = new JCheckBox("Annotations", true);
    annotations.addActionListener(e -> options.setShowingAnnotations(annotations.isSelected()));
    add(annotations, BorderLayout.CENTER);
    JButton ok = new JButton("OK");
    ok.addActionListener(
        e -> {
          accepted = true;
          dispose();
        });
    add(ok, BorderLayout.SOUTH);
    pack();
  }

  public PrintOptions getOptions() {
    return options;
  }

  public boolean isAccepted() {
    return accepted;
  }
}
