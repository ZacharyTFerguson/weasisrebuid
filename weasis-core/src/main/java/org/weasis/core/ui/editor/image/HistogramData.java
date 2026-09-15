/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.Color;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Intensity histogram bins for the histogram dock (weasis.org Histogram tutorial, 4.7). X axis is
 * raw samples, or modality values when a rescale slope/intercept is set.
 */
public class HistogramData {

  public enum Channel {
    LUMINANCE,
    RED,
    GREEN,
    BLUE,
    HUE,
    SATURATION,
    VALUE,
    LIGHTNESS
  }

  public enum ColorModel {
    GRAYSCALE,
    RGB,
    HSV,
    HLS
  }

  public static final int MIN_BINS = 64;
  public static final int MAX_BINS = 4096;
  public static final int DEFAULT_BIN_CAP = 512;

  private int[] bins;
  private boolean userBins;
  private Channel channel = Channel.LUMINANCE;
  private boolean accumulate;
  private boolean logarithmic;
  private boolean showIntensityColor = true;
  private double yScale = 1.0;
  private int samples;
  private int minValue;
  private int maxValue;
  private double sum;
  private int peak;
  private int peakBin;
  private double modalitySlope = 1.0;
  private double modalityIntercept;

  public HistogramData() {
    this.bins = new int[256];
    this.userBins = false;
  }

  public HistogramData(int binCount) {
    this.bins = new int[Math.max(1, binCount)];
    this.userBins = true;
  }

  public int[] getBins() {
    return bins;
  }

  public Channel getChannel() {
    return channel;
  }

  public void setChannel(Channel channel) {
    this.channel = channel == null ? Channel.LUMINANCE : channel;
  }

  public boolean isAccumulate() {
    return accumulate;
  }

  public void setAccumulate(boolean accumulate) {
    this.accumulate = accumulate;
  }

  public boolean isLogarithmic() {
    return logarithmic;
  }

  public void setLogarithmic(boolean logarithmic) {
    this.logarithmic = logarithmic;
  }

  public boolean isShowIntensityColor() {
    return showIntensityColor;
  }

  public void setShowIntensityColor(boolean showIntensityColor) {
    this.showIntensityColor = showIntensityColor;
  }

  public double getYScale() {
    return yScale;
  }

  public void setYScale(double yScale) {
    this.yScale = yScale <= 0 ? 1.0 : yScale;
  }

  public void shrinkY() {
    setYScale(getYScale() * 0.8);
  }

  public void stretchY() {
    setYScale(getYScale() * 1.25);
  }

  public int getSamples() {
    return samples;
  }

  public int getMinValue() {
    return minValue;
  }

  public int getMaxValue() {
    return maxValue;
  }

  public double getMean() {
    return samples == 0 ? 0.0 : sum / samples;
  }

  public int getPeak() {
    return peak;
  }

  public int getPeakBin() {
    return peakBin;
  }

  public double getModalitySlope() {
    return modalitySlope;
  }

  public double getModalityIntercept() {
    return modalityIntercept;
  }

  public void setModalityLut(double slope, double intercept) {
    this.modalitySlope = slope == 0 ? 1.0 : slope;
    this.modalityIntercept = intercept;
  }

  public double modalityValue(int sample) {
    return sample * modalitySlope + modalityIntercept;
  }

  public void setBinCount(int binCount) {
    int n = Math.max(MIN_BINS, Math.min(MAX_BINS, binCount));
    this.bins = new int[n];
    this.userBins = true;
  }

  public static int defaultBinCount(int min, int max) {
    int span = Math.max(1, max - min);
    return Math.max(MIN_BINS, Math.min(DEFAULT_BIN_CAP, span));
  }

  public void resetDisplay() {
    accumulate = false;
    logarithmic = false;
    showIntensityColor = true;
    yScale = 1.0;
  }

  public void accumulate(BufferedImage image) {
    accumulate(image, null);
  }

  public void accumulate(BufferedImage image, Shape roi) {
    Arrays.fill(bins, 0);
    samples = 0;
    sum = 0;
    peak = 0;
    peakBin = 0;
    minValue = 0;
    maxValue = 0;
    if (image == null) {
      return;
    }
    int w = image.getWidth();
    int h = image.getHeight();
    int foundMin = Integer.MAX_VALUE;
    int foundMax = Integer.MIN_VALUE;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (roi != null && !roi.contains(x + 0.5, y + 0.5)) {
          continue;
        }
        int v = sample(image.getRGB(x, y));
        foundMin = Math.min(foundMin, v);
        foundMax = Math.max(foundMax, v);
        samples++;
        sum += v;
      }
    }
    if (samples == 0) {
      return;
    }
    minValue = foundMin;
    maxValue = foundMax;
    if (!userBins) {
      int next = defaultBinCount(minValue, maxValue);
      if (bins.length != next) {
        bins = new int[next];
      } else {
        Arrays.fill(bins, 0);
      }
    }
    int range = Math.max(1, maxValue - minValue);
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (roi != null && !roi.contains(x + 0.5, y + 0.5)) {
          continue;
        }
        int v = sample(image.getRGB(x, y));
        int bin = (int) ((v - minValue) * (bins.length - 1.0) / range);
        bin = Math.max(0, Math.min(bins.length - 1, bin));
        bins[bin]++;
      }
    }
    for (int i = 0; i < bins.length; i++) {
      if (bins[i] > peak) {
        peak = bins[i];
        peakBin = i;
      }
    }
  }

  public int[] displayBins() {
    int[] out = new int[bins.length];
    int running = 0;
    for (int i = 0; i < bins.length; i++) {
      int v = bins[i];
      if (accumulate) {
        running += v;
        v = running;
      }
      if (logarithmic) {
        v = v <= 0 ? 0 : (int) Math.round(Math.log1p(v) * 100.0);
      }
      out[i] = (int) Math.round(v * yScale);
    }
    return out;
  }

  public int displayPeak() {
    int max = 1;
    for (int v : displayBins()) {
      max = Math.max(max, v);
    }
    return max;
  }

  public BinInfo binAt(int index) {
    if (index < 0 || index >= bins.length) {
      return new BinInfo(-1, 0, 0, 0, 0, 0);
    }
    int range = Math.max(1, maxValue - minValue);
    int from = minValue + (int) Math.round(index * (double) range / Math.max(1, bins.length - 1));
    int to =
        minValue
            + (int) Math.round((index + 1) * (double) range / Math.max(1, bins.length - 1));
    return new BinInfo(
        index, bins[index], from, to, modalityValue(from), modalityValue(to));
  }

  public BinInfo hit(int x, int width) {
    if (bins.length == 0 || width <= 0) {
      return binAt(-1);
    }
    int index = x * bins.length / width;
    return binAt(index);
  }

  public String statisticsText() {
    return "n="
        + samples
        + " min="
        + modalityValue(minValue)
        + " max="
        + modalityValue(maxValue)
        + " mean="
        + (samples == 0 ? 0.0 : modalityValue((int) Math.round(getMean())))
        + " peak="
        + peak;
  }

  int sample(int rgb) {
    int r = (rgb >> 16) & 0xff;
    int g = (rgb >> 8) & 0xff;
    int b = rgb & 0xff;
    return switch (channel) {
      case RED -> r;
      case GREEN -> g;
      case BLUE -> b;
      case HUE -> Math.round(Color.RGBtoHSB(r, g, b, null)[0] * 255f);
      case SATURATION -> saturation(r, g, b);
      case VALUE -> Math.max(r, Math.max(g, b));
      case LIGHTNESS -> (Math.max(r, Math.max(g, b)) + Math.min(r, Math.min(g, b))) / 2;
      case LUMINANCE -> (int) Math.round(0.299 * r + 0.587 * g + 0.114 * b);
    };
  }

  static int saturation(int r, int g, int b) {
    float[] hsb = Color.RGBtoHSB(r, g, b, null);
    return Math.round(hsb[1] * 255f);
  }

  /** Occurrence count and sample / modality range covered by one bar. */
  public static final class BinInfo {
    private final int index;
    private final int count;
    private final int valueFrom;
    private final int valueTo;
    private final double modalityFrom;
    private final double modalityTo;

    public BinInfo(
        int index,
        int count,
        int valueFrom,
        int valueTo,
        double modalityFrom,
        double modalityTo) {
      this.index = index;
      this.count = count;
      this.valueFrom = valueFrom;
      this.valueTo = valueTo;
      this.modalityFrom = modalityFrom;
      this.modalityTo = modalityTo;
    }

    public int index() {
      return index;
    }

    public int count() {
      return count;
    }

    public int valueFrom() {
      return valueFrom;
    }

    public int valueTo() {
      return valueTo;
    }

    public double modalityFrom() {
      return modalityFrom;
    }

    public double modalityTo() {
      return modalityTo;
    }
  }
}
