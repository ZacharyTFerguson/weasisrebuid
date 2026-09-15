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

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/** One ECG lead trace. */
public class LeadPanel extends JPanel {

  private final Lead lead;
  private final double[] millivolts;
  private final double samplingFrequency;

  public LeadPanel(Lead lead, double[] millivolts, double samplingFrequency) {
    this.lead = lead == null ? Lead.UNKNOWN : lead;
    this.millivolts = millivolts == null ? new double[0] : millivolts.clone();
    this.samplingFrequency = samplingFrequency;
    setToolTipText(this.lead.label());
  }

  public Lead lead() {
    return lead;
  }

  public double[] millivolts() {
    return millivolts.clone();
  }

  public double samplingFrequency() {
    return samplingFrequency;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (millivolts.length == 0 || !(g instanceof Graphics2D g2) || getWidth() <= 1) {
      return;
    }
    int w = getWidth();
    int h = getHeight();
    int mid = h / 2;
    int prevX = 0;
    int prevY = mid - (int) (millivolts[0] * (h / 4.0));
    for (int i = 1; i < millivolts.length; i++) {
      int x = (int) (i * (w - 1.0) / (millivolts.length - 1.0));
      int y = mid - (int) (millivolts[i] * (h / 4.0));
      g2.drawLine(prevX, prevY, x, y);
      prevX = x;
      prevY = y;
    }
  }
}
