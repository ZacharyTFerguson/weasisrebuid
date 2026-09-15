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

import javax.swing.JLabel;
import javax.swing.JPanel;

/** Sampling frequency and duration for the loaded waveform. */
public class InfoPanel extends JPanel {

  private final JLabel label = new JLabel(" ");

  public InfoPanel() {
    add(label);
  }

  public void setWaveform(WaveDataReadable data) {
    if (data == null || data.sampleCount() == 0) {
      label.setText(" ");
      return;
    }
    label.setText(
        String.format(
            "%.1f Hz, %.3f s, %d channel(s)",
            data.samplingFrequency(), data.durationSeconds(), data.channelCount()));
  }

  public String text() {
    return label.getText();
  }
}
