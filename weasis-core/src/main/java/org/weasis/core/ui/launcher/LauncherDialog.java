/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.launcher;

import java.awt.BorderLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class LauncherDialog extends JDialog {
  private final Launcher launcher;
  private final JTextField command = new JTextField();

  public LauncherDialog(Window parent, Launcher launcher) {
    super(parent, "Launcher", ModalityType.APPLICATION_MODAL);
    this.launcher = launcher == null ? new Launcher() : launcher;
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    command.setText(this.launcher.getCommand());
    add(command, BorderLayout.CENTER);
    JButton ok = new JButton("OK");
    ok.addActionListener(
        e -> {
          this.launcher.setCommand(command.getText());
          dispose();
        });
    add(ok, BorderLayout.SOUTH);
    pack();
  }

  public Launcher getLauncher() {
    return launcher;
  }
}
