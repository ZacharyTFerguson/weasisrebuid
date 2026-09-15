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

import java.awt.BorderLayout;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.core.ui.editor.image.HistogramData.ColorModel;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.GraphicArea;

/**
 * Histogram dock. Binds the selected 2D view, plots luminance or per-channel histograms, and can
 * restrict samples to a selected closed measurement graphic.
 */
public class HistogramView extends PluginTool {

  public static final String NAME = "Histogram";

  private final HistogramPanel panel = new HistogramPanel();
  private final ChannelHistogramPanel channels = new ChannelHistogramPanel();
  private final JLabel stats = new JLabel(" ");
  private DefaultView2d<?> view;
  private ColorModel histogramColorModel = ColorModel.GRAYSCALE;
  private boolean statisticsVisible = true;

  public HistogramView() {
    super(NAME, 80);
    add(panel, BorderLayout.CENTER);
    add(channels, BorderLayout.SOUTH);
    add(stats, BorderLayout.NORTH);
    channels.setVisible(false);
  }

  public HistogramPanel getPanel() {
    return panel;
  }

  public ChannelHistogramPanel getChannels() {
    return channels;
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    refresh();
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public ColorModel getHistogramColorModel() {
    return histogramColorModel;
  }

  public void setHistogramColorModel(ColorModel colorModel) {
    this.histogramColorModel = colorModel == null ? ColorModel.GRAYSCALE : colorModel;
    refresh();
  }

  public void setStatisticsVisible(boolean statisticsVisible) {
    this.statisticsVisible = statisticsVisible;
    stats.setVisible(statisticsVisible);
  }

  public boolean isStatisticsVisible() {
    return statisticsVisible;
  }

  public void setAccumulate(boolean accumulate) {
    panel.getData().setAccumulate(accumulate);
    channels.getRed().getData().setAccumulate(accumulate);
    refresh();
  }

  public void setLogarithmic(boolean logarithmic) {
    panel.getData().setLogarithmic(logarithmic);
    channels.getRed().getData().setLogarithmic(logarithmic);
    refresh();
  }

  public void shrinkY() {
    panel.getData().shrinkY();
    channels.getRed().getData().shrinkY();
    refresh();
  }

  public void stretchY() {
    panel.getData().stretchY();
    channels.getRed().getData().stretchY();
    refresh();
  }

  public void resetDisplay() {
    panel.resetDisplay();
    channels.resetDisplay();
    histogramColorModel = ColorModel.GRAYSCALE;
    refresh();
  }

  public void refresh() {
    BufferedImage image = view == null ? null : view.getSourceImage();
    Shape roi = selectedRoi(view);
    if (view != null) {
      panel.getData().setModalityLut(view.getModalityLutSlope(), view.getModalityLutIntercept());
      channels
          .getRed()
          .getData()
          .setModalityLut(view.getModalityLutSlope(), view.getModalityLutIntercept());
    }
    boolean multi = histogramColorModel != ColorModel.GRAYSCALE;
    channels.setVisible(multi);
    panel.setVisible(!multi);
    if (multi) {
      channels.update(image, roi, histogramColorModel);
      stats.setText(channels.getRed().getData().statisticsText());
    } else {
      panel.setChannel(HistogramData.Channel.LUMINANCE);
      panel.update(image, roi);
      stats.setText(panel.getData().statisticsText());
    }
    stats.setVisible(statisticsVisible);
  }

  static Shape selectedRoi(DefaultView2d<?> view) {
    if (view == null) {
      return null;
    }
    for (Graphic graphic : view.getGraphicList()) {
      if (Boolean.TRUE.equals(graphic.getSelected())
          && graphic instanceof GraphicArea
          && graphic.getShape() != null) {
        return graphic.getShape();
      }
    }
    return null;
  }
}
