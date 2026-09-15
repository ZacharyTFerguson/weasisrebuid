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
import javax.swing.WindowConstants;
import org.weasis.core.api.gui.util.AppProperties;
import org.weasis.core.api.util.MemoryManager;
import org.weasis.core.api.util.ResourceMonitor;
import org.weasis.core.api.util.SystemMemory;

public class ResourceMonitorDialog extends JDialog {
  private final String statusText;

  public ResourceMonitorDialog(Window parent) {
    super(parent, "System resources", ModalityType.MODELESS);
    setName("system-resources");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    statusText = diagnosticText();
    JLabel status = new JLabel(statusText);
    status.setName("system-resources-status");
    add(status, BorderLayout.CENTER);
    JButton close = new JButton("Close");
    close.addActionListener(e -> dispose());
    add(close, BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(parent);
  }

  public String statusText() {
    return statusText;
  }

  static String diagnosticText() {
    return heapText() + "  Native " + nativeBudgetPercent() + "%";
  }

  static String heapText() {
    return "Heap "
        + SystemMemory.usedPercent()
        + "%  ("
        + MemoryManager.getUsedMemory()
        + " / "
        + MemoryManager.getMaxMemory()
        + ")";
  }

  static int nativeBudgetPercent() {
    return ResourceMonitor.nativeMemoryPercent(AppProperties.getSystemPreferences());
  }
}
