/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.weasis.core.ui.editor.image.HistogramData.Channel;
import org.weasis.core.ui.editor.image.HistogramData.ColorModel;

/** RGB / HSV / HLS channel histograms (one panel per channel of the chosen model). */
public class ChannelHistogramPanel extends JPanel {

  private final HistogramPanel red = new HistogramPanel();
  private final HistogramPanel green = new HistogramPanel();
  private final HistogramPanel blue = new HistogramPanel();

  public ChannelHistogramPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setName("histogram-channels");
    red.setName("histogram-red");
    green.setName("histogram-green");
    blue.setName("histogram-blue");
    add(red);
    add(green);
    add(blue);
    applyChannels(ColorModel.RGB);
  }

  public HistogramPanel getRed() {
    return red;
  }

  public HistogramPanel getGreen() {
    return green;
  }

  public HistogramPanel getBlue() {
    return blue;
  }

  public void update(BufferedImage image) {
    update(image, null, ColorModel.RGB);
  }

  public void update(BufferedImage image, Shape roi) {
    update(image, roi, ColorModel.RGB);
  }

  public void update(BufferedImage image, Shape roi, ColorModel model) {
    applyChannels(model == null ? ColorModel.RGB : model);
    copyLut(red);
    red.update(image, roi);
    copyLut(green);
    green.update(image, roi);
    copyLut(blue);
    blue.update(image, roi);
  }

  public void resetDisplay() {
    red.resetDisplay();
    green.resetDisplay();
    blue.resetDisplay();
  }

  void applyChannels(ColorModel model) {
    switch (model) {
      case HSV -> {
        red.setChannel(Channel.HUE);
        green.setChannel(Channel.SATURATION);
        blue.setChannel(Channel.VALUE);
      }
      case HLS -> {
        red.setChannel(Channel.HUE);
        green.setChannel(Channel.LIGHTNESS);
        blue.setChannel(Channel.SATURATION);
      }
      default -> {
        red.setChannel(Channel.RED);
        green.setChannel(Channel.GREEN);
        blue.setChannel(Channel.BLUE);
      }
    }
  }

  void copyLut(HistogramPanel panel) {
    HistogramData src = red.getData();
    HistogramData dst = panel.getData();
    dst.setModalityLut(src.getModalitySlope(), src.getModalityIntercept());
    dst.setAccumulate(src.isAccumulate());
    dst.setLogarithmic(src.isLogarithmic());
    dst.setShowIntensityColor(src.isShowIntensityColor());
    dst.setYScale(src.getYScale());
  }
}
