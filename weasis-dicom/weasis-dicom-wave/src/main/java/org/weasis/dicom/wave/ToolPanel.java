/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Format selector for the ECG page. */
public class ToolPanel extends JPanel {

  private final JComboBox<Format> formatBox = new JComboBox<>(Format.values());

  public ToolPanel(WaveView view) {
    add(new JLabel("Format"));
    add(formatBox);
    formatBox.addActionListener(
        e -> {
          if (view != null) {
            view.setFormat((Format) formatBox.getSelectedItem());
          }
        });
  }

  public void setFormat(Format format) {
    formatBox.setSelectedItem(format == null ? Format.DEFAULT : format);
  }

  public Format format() {
    Format selected = (Format) formatBox.getSelectedItem();
    return selected == null ? Format.DEFAULT : selected;
  }
}
