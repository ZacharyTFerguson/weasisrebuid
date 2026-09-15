/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Objects;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.FilterOp;
import org.weasis.core.api.image.OverlayOp;
import org.weasis.core.api.image.PseudoColorOp;
import org.weasis.core.api.image.ShutterOp;
import org.weasis.core.api.image.WindowAndPresetsOp;
import org.weasis.core.api.image.util.WindLevelParameters;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.ImageViewerEventManager;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.codec.WindowLevelPainter;
import org.weasis.dicom.codec.utils.DicomMediaUtils;

/**
 * DICOM 2D view. Op chain: WindowAndPresets → Filter → PseudoColor → Shutter → Overlay → Affine.
 */
public class View2d extends DefaultView2d<MediaElement> {

  private Attributes dataset;
  private WindLevelParameters fileWl = new WindLevelParameters(400, 40);
  private double window = 400;
  private double level = 40;
  private File file;

  public View2d() {
    super();
  }

  public void load(File dicom) throws Exception {
    this.file = dicom;
    DicomMediaIO io = DicomMediaIO.open(dicom);
    load(io.getDataset());
  }

  public void load(Attributes dataset) {
    this.dataset = Objects.requireNonNull(dataset, "dataset");
    fileWl = DicomMediaUtils.windowLevel(dataset, 400, 40);
    this.window = fileWl.getWindow();
    this.level = fileWl.getLevel();
    setFrameOfReferenceUID(dataset.getString(Tag.FrameOfReferenceUID, ""));
    applyDatasetFlags();
    render();
  }

  public Attributes getDataset() {
    return dataset;
  }

  public File getFile() {
    return file;
  }

  public double getWindow() {
    return window;
  }

  public double getLevel() {
    return level;
  }

  public void setWindowLevel(double window, double level) {
    this.window = window;
    this.level = level;
    getDisplayOpManager().setParamValue("op.window.presets", WindowAndPresetsOp.P_WINDOW, window);
    getDisplayOpManager().setParamValue("op.window.presets", WindowAndPresetsOp.P_LEVEL, level);
    render();
  }

  public WindLevelParameters getFileWindowLevel() {
    return fileWl;
  }

  @Override
  public void resetWinLevelDefaults() {
    if (fileWl != null) {
      setWindowLevel(fileWl.getWindow(), fileWl.getLevel());
    }
  }

  public void render() {
    if (dataset == null) {
      return;
    }
    BufferedImage painted = WindowLevelPainter.paintMonochrome2(dataset, window, level);
    painted = applyFilterAndColor(painted);
    painted = applyShutter(painted);
    painted = applyOverlay(painted);
    setSourceImage(painted);
  }

  BufferedImage applyFilterAndColor(BufferedImage src) {
    Object filter = getDisplayOpManager().getParamValue("op.filter", FilterOp.P_FILTER);
    Object invert = getDisplayOpManager().getParamValue("op.pseudocolor", PseudoColorOp.P_INVERT);
    if (!Boolean.TRUE.equals(invert) && (filter == null || FilterOp.NONE.equals(filter))) {
      return src;
    }
    if (Boolean.TRUE.equals(invert)) {
      WritableRaster raster = src.getRaster();
      byte[] data = ((DataBufferByte) raster.getDataBuffer()).getData();
      for (int i = 0; i < data.length; i++) {
        data[i] = (byte) (255 - (data[i] & 0xff));
      }
    }
    return src;
  }

  BufferedImage applyShutter(BufferedImage src) {
    Object enabled = getDisplayOpManager().getParamValue("op.shutter", ShutterOp.P_ENABLED);
    if (!Boolean.TRUE.equals(enabled) || dataset == null) {
      return src;
    }
    int left = intParam("op.shutter", ShutterOp.P_LEFT, 0);
    int right = intParam("op.shutter", ShutterOp.P_RIGHT, src.getWidth() - 1);
    int upper = intParam("op.shutter", ShutterOp.P_UPPER, 0);
    int lower = intParam("op.shutter", ShutterOp.P_LOWER, src.getHeight() - 1);
    byte[] data = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
    int w = src.getWidth();
    int h = src.getHeight();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (x < left || x > right || y < upper || y > lower) {
          data[y * w + x] = 0;
        }
      }
    }
    return src;
  }

  BufferedImage applyOverlay(BufferedImage src) {
    Object enabled = getDisplayOpManager().getParamValue("op.overlay", OverlayOp.P_ENABLED);
    if (Boolean.FALSE.equals(enabled) || dataset == null || !dataset.contains(Tag.OverlayData)) {
      return src;
    }
    byte[] overlay;
    try {
      overlay = dataset.getBytes(Tag.OverlayData);
    } catch (Exception e) {
      return src;
    }
    if (overlay == null || overlay.length == 0) {
      return src;
    }
    byte[] data = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
    int n = Math.min(data.length, overlay.length * 8);
    for (int i = 0; i < n; i++) {
      int bit = (overlay[i / 8] >> (i % 8)) & 1;
      if (bit == 1) {
        data[i] = (byte) 255;
      }
    }
    return src;
  }

  void applyDatasetFlags() {
    if (dataset == null) {
      return;
    }
    String lossy = dataset.getString(Tag.LossyImageCompression, "");
    if ("01".equals(lossy) || dataset.contains(Tag.LossyImageCompressionRatio)) {
      setLossyLabel("LOSSY");
    } else {
      setLossyLabel("");
    }
    if ("RECTANGULAR".equalsIgnoreCase(dataset.getString(Tag.ShutterShape, ""))) {
      getDisplayOpManager().setParamValue("op.shutter", ShutterOp.P_ENABLED, Boolean.TRUE);
      getDisplayOpManager()
          .setParamValue(
              "op.shutter", ShutterOp.P_LEFT, dataset.getInt(Tag.ShutterLeftVerticalEdge, 0));
      getDisplayOpManager()
          .setParamValue(
              "op.shutter",
              ShutterOp.P_RIGHT,
              dataset.getInt(Tag.ShutterRightVerticalEdge, dataset.getInt(Tag.Columns, 0) - 1));
      getDisplayOpManager()
          .setParamValue(
              "op.shutter", ShutterOp.P_UPPER, dataset.getInt(Tag.ShutterUpperHorizontalEdge, 0));
      getDisplayOpManager()
          .setParamValue(
              "op.shutter",
              ShutterOp.P_LOWER,
              dataset.getInt(Tag.ShutterLowerHorizontalEdge, dataset.getInt(Tag.Rows, 0) - 1));
    }
    if (dataset.containsValue(Tag.PixelSpacing) || dataset.containsValue(Tag.ImagerPixelSpacing)) {
      setGeometryWarning("");
    }
  }

  int intParam(String op, String key, int fallback) {
    Object v = getDisplayOpManager().getParamValue(op, key);
    if (v instanceof Number n) {
      return n.intValue();
    }
    return fallback;
  }

  @Override
  protected ImageViewerEventManager createEventManager() {
    return new EventManager(this);
  }
}
