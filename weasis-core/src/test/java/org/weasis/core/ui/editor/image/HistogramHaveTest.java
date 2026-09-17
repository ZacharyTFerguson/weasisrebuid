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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.editor.image.HistogramData.BinInfo;
import org.weasis.core.ui.editor.image.HistogramData.Channel;
import org.weasis.core.ui.editor.image.HistogramData.ColorModel;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.layer.GraphicLayer;
import org.weasis.core.ui.model.layer.Layer;
import org.weasis.core.ui.model.layer.LayerType;

class HistogramHaveTest {

  @Test
  void accumulateCountsPeakAndStatistics() {
    HistogramData data = new HistogramData(256);
    data.accumulate(gray(new int[][] {{0, 0}, {255, 128}}));
    assertEquals(4, data.getSamples());
    assertEquals(0, data.getMinValue());
    assertEquals(255, data.getMaxValue());
    assertEquals(2, data.getBins()[0]);
    assertEquals(1, data.getBins()[255]);
    assertEquals(1, data.getBins()[128]);
    assertEquals(2, data.getPeak());
    assertEquals(0, data.getPeakBin());
    assertEquals(95.75, data.getMean(), 1e-9);
    assertTrue(data.statisticsText().contains("n=4"));
  }

  @Test
  void defaultBinCountFollowsTutorialCaps() {
    assertEquals(255, HistogramData.defaultBinCount(0, 255));
    assertEquals(64, HistogramData.defaultBinCount(10, 20));
    assertEquals(512, HistogramData.defaultBinCount(0, 4096));
    HistogramData data = new HistogramData();
    data.setBinCount(32);
    assertEquals(64, data.getBins().length);
    data.setBinCount(8000);
    assertEquals(4096, data.getBins().length);
  }

  @Test
  void bindViewFillsLuminanceAndPaintsBars() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(gray(new int[][] {{40, 40}, {200, 200}}));
    HistogramView dock = new HistogramView();
    assertEquals("Histogram", dock.getComponentName());
    assertEquals("histogram", dock.getName());
    assertEquals("histogram-panel", dock.getPanel().getName());
    assertEquals("histogram-stats", dock.statsLabel().getName());
    assertEquals("histogram-gray", dock.grayButton().getName());
    assertEquals("histogram-rgb", dock.rgbButton().getName());
    assertEquals("histogram-hsv", dock.hsvButton().getName());
    assertEquals("histogram-hls", dock.hlsButton().getName());
    assertEquals(Insertable.Type.TOOL, dock.getType());
    dock.bind(view);
    assertSame(view, dock.boundView());
    assertEquals(4, dock.getPanel().getData().getSamples());
    assertTrue(dock.getPanel().getData().getPeak() >= 2);
    BufferedImage canvas = new BufferedImage(256, 64, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = canvas.createGraphics();
    dock.getPanel().setSize(256, 64);
    dock.getPanel().paint(g);
    g.dispose();
    boolean painted = false;
    for (int x = 0; x < 256 && !painted; x++) {
      painted = canvas.getRGB(x, 32) != Color.WHITE.getRGB();
    }
    assertTrue(painted);
  }

  @Test
  void rgbChannelsFillSeparateBins() {
    BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 2; y++) {
      for (int x = 0; x < 2; x++) {
        image.setRGB(x, y, new Color(255, 0, 0).getRGB());
      }
    }
    ChannelHistogramPanel rgb = new ChannelHistogramPanel();
    rgb.update(image);
    assertEquals(4, rgb.getRed().getData().getSamples());
    assertEquals(4, rgb.getRed().getData().getPeak());
    assertEquals(255, rgb.getRed().getData().getMaxValue());
    assertEquals(0, rgb.getGreen().getData().getMaxValue());
    assertEquals(0, rgb.getBlue().getData().getMaxValue());
    HistogramView dock = new HistogramView();
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(image);
    dock.bind(view);
    dock.setHistogramColorModel(ColorModel.RGB);
    assertEquals(Channel.RED, dock.getChannels().getRed().getData().getChannel());
    assertEquals("histogram-channels", dock.getChannels().getName());
    assertTrue(dock.getChannels().isVisible());
    assertEquals(255, dock.getChannels().getRed().getData().getMaxValue());
    assertEquals(0, dock.getChannels().getGreen().getData().getMaxValue());
  }

  @Test
  void hsvAndHlsChannels() {
    HistogramData hue = new HistogramData(256);
    hue.setChannel(Channel.HUE);
    BufferedImage red = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    red.setRGB(0, 0, Color.RED.getRGB());
    hue.accumulate(red);
    assertEquals(1, hue.getSamples());
    assertEquals(0, hue.getMaxValue());
    HistogramData value = new HistogramData(256);
    value.setChannel(Channel.VALUE);
    value.accumulate(red);
    assertEquals(255, value.getMaxValue());
    HistogramData light = new HistogramData(256);
    light.setChannel(Channel.LIGHTNESS);
    light.accumulate(red);
    assertEquals(127, light.getMaxValue());
  }

  @Test
  void selectedClosedGraphicLimitsSamples() {
    BufferedImage image = gray(new int[][] {{255, 0}, {255, 0}});
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(image);
    RectangleGraphic roi = new RectangleGraphic();
    roi.setHandlePoint(0, new Point2D.Double(0, 0));
    roi.setHandlePoint(1, new Point2D.Double(1, 2));
    roi.setSelected(true);
    view.addGraphic(roi);
    HistogramView dock = new HistogramView();
    dock.bind(view);
    assertEquals(2, dock.getPanel().getData().getSamples());
    assertEquals(255, dock.getPanel().getData().getMinValue());
    assertEquals(255, dock.getPanel().getData().getMaxValue());
  }

  @Test
  void cumulativeLogAndBinHit() {
    HistogramData data = new HistogramData(256);
    data.accumulate(gray(new int[][] {{10, 20}, {30, 40}}));
    data.setAccumulate(true);
    int[] cum = data.displayBins();
    assertEquals(data.getSamples(), cum[cum.length - 1]);
    data.setLogarithmic(true);
    assertTrue(data.displayPeak() > 0);
    data.resetDisplay();
    assertFalse(data.isAccumulate());
    assertFalse(data.isLogarithmic());
    BinInfo hit = data.binAt(data.getPeakBin());
    assertTrue(hit.count() >= 1);
    assertTrue(hit.valueTo() >= hit.valueFrom());
    HistogramPanel panel = new HistogramPanel();
    panel.setSize(256, 64);
    panel.update(gray(new int[][] {{8, 8}, {8, 8}}));
    BinInfo click = panel.hit(0);
    assertEquals(click.count(), panel.getLastHit().count());
  }

  @Test
  void modalityLutMapsHounsfieldRange() {
    HistogramData data = new HistogramData(256);
    data.setModalityLut(1.0, -1024);
    data.accumulate(gray(new int[][] {{0, 0}, {0, 0}}));
    assertEquals(-1024.0, data.modalityValue(0), 1e-9);
    assertEquals(-1024.0, data.binAt(data.getPeakBin()).modalityFrom(), 1e-9);
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setModalityLut(1.0, -1024);
    view.setSourceImage(gray(new int[][] {{0}}));
    HistogramView dock = new HistogramView();
    dock.bind(view);
    assertEquals(-1024.0, dock.getPanel().getData().modalityValue(0), 1e-9);
  }

  @Test
  void layerTypeConstantsAndGraphicLayerDefault() {
    assertEquals(LayerType.IMAGE, new Layer().getType());
    assertEquals(LayerType.MEASURE, new GraphicLayer().getType());
    GraphicLayer draw = new GraphicLayer(LayerType.DRAW);
    draw.setVisible(false);
    assertEquals(LayerType.DRAW, draw.getType());
    assertFalse(draw.isVisible());
    assertNotNull(LayerType.CROSSLINES);
    assertNotNull(LayerType.ANNOTATION);
    assertNotNull(LayerType.TEMP_DRAW);
    assertNotNull(LayerType.TEMP_LINE);
    assertNotNull(LayerType.ACQ_IMAGE);
    assertNotNull(LayerType.PROCESSING);
    assertNotNull(LayerType.DICOM_PR);
  }

  static BufferedImage gray(int[][] pixels) {
    BufferedImage image =
        new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_BYTE_GRAY);
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        int v = pixels[y][x];
        image.getRaster().setSample(x, y, 0, v);
      }
    }
    return image;
  }
}
