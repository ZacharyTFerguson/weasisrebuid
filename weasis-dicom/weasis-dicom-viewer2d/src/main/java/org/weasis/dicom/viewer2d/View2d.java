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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
  private List<Attributes> stackDatasets = List.of();
  private List<File> stackFiles = List.of();

  static final String MULTI_FRAME_REFUSED = "multi-frame instance refused";

  public View2d() {
    super();
  }

  public void load(File dicom) throws Exception {
    loadStack(List.of(dicom));
  }

  public void loadStack(List<File> files) throws IOException {
    if (files == null || files.isEmpty()) {
      throw new IllegalArgumentException("empty stack");
    }
    List<StackEntry> entries = new ArrayList<>();
    for (File f : files) {
      DicomMediaIO io = DicomMediaIO.open(f);
      Attributes dcm = io.getDataset();
      int frames = dcm.getInt(Tag.NumberOfFrames, 1);
      if (frames > 1) {
        setGeometryWarning(MULTI_FRAME_REFUSED + " (NumberOfFrames=" + frames + ")");
        throw new IllegalArgumentException(MULTI_FRAME_REFUSED);
      }
      entries.add(new StackEntry(f, dcm));
    }
    String seriesUid = entries.getFirst().dataset.getString(Tag.SeriesInstanceUID, "");
    for (StackEntry entry : entries) {
      if (!seriesUid.equals(entry.dataset.getString(Tag.SeriesInstanceUID, ""))) {
        throw new IllegalArgumentException("mixed SeriesInstanceUID in stack");
      }
    }
    entries.sort(
        Comparator.comparingInt((StackEntry e) -> e.dataset.getInt(Tag.InstanceNumber, 0))
            .thenComparing(e -> e.file.getName()));
    List<Attributes> datasets = new ArrayList<>();
    List<File> stack = new ArrayList<>();
    for (StackEntry entry : entries) {
      datasets.add(entry.dataset);
      stack.add(entry.file);
    }
    this.stackDatasets = List.copyOf(datasets);
    this.stackFiles = List.copyOf(stack);
    showStackFrame(0);
  }

  public void load(Attributes dataset) {
    this.dataset = Objects.requireNonNull(dataset, "dataset");
    this.stackDatasets = List.of(this.dataset);
    this.stackFiles = file != null ? List.of(file) : List.of();
    bindDataset(this.dataset);
  }

  private void bindDataset(Attributes active) {
    fileWl = DicomMediaUtils.windowLevel(active, 400, 40);
    this.window = fileWl.getWindow();
    this.level = fileWl.getLevel();
    applyDatasetFlags();
    render();
  }

  private void showStackFrame(int requestedIndex) {
    if (stackDatasets.isEmpty()) {
      return;
    }
    int clamped = Math.max(0, Math.min(requestedIndex, stackDatasets.size() - 1));
    super.setFrameIndex(clamped);
    this.dataset = stackDatasets.get(clamped);
    if (!stackFiles.isEmpty()) {
      this.file = stackFiles.get(clamped);
    }
    bindDataset(this.dataset);
  }

  public int getStackSize() {
    return stackDatasets.size();
  }

  @Override
  public void setFrameIndex(int frameIndex) {
    if (stackDatasets.isEmpty()) {
      super.setFrameIndex(frameIndex);
      return;
    }
    showStackFrame(frameIndex);
  }

  private record StackEntry(File file, Attributes dataset) {}

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
    return new View2dEventManager(this);
  }

  static final class View2dEventManager extends ImageViewerEventManager {
    private final View2d view2d;

    View2dEventManager(View2d view) {
      super(view);
      this.view2d = view;
    }

    @Override
    protected void applyWindowLevel(int dx, int dy) {
      view2d.setWindowLevel(view2d.getWindow() + dx, view2d.getLevel() - dy);
    }
  }
}
