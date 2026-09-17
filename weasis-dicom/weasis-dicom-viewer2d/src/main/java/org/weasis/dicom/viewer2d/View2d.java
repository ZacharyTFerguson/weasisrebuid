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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.AutoLevelsOp;
import org.weasis.core.api.image.BrightnessOp;
import org.weasis.core.api.image.CropOp;
import org.weasis.core.api.image.FilterOp;
import org.weasis.core.api.image.ImageOpNode;
import org.weasis.core.api.image.OverlayOp;
import org.weasis.core.api.image.PseudoColorOp;
import org.weasis.core.api.image.ShutterOp;
import org.weasis.core.api.image.WindowAndPresetsOp;
import org.weasis.core.api.image.util.WindLevelParameters;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.ImageViewerEventManager;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.codec.WindowLevelPainter;
import org.weasis.dicom.codec.seg.SegVisibilityPolicy;
import org.weasis.dicom.codec.utils.DicomMediaUtils;
import org.weasis.dicom.codec.utils.LutPipeline;

/**
 * DICOM 2D view. Op chain: WindowAndPresets → Filter → PseudoColor → Shutter → Overlay → Affine.
 */
public class View2d extends DefaultView2d<MediaElement> {

  private Attributes dataset;
  private WindLevelParameters fileWl = new WindLevelParameters(400, 40);
  private WindLevelParameters dataRangeWl;
  private WindLevelParameters activeVoi = new WindLevelParameters(400, 40);
  private double window = 400;
  private double level = 40;
  private boolean windowChrome;
  private boolean cropChrome;
  private boolean brightnessChrome;
  private boolean autoLevelsChrome;
  private File file;
  private final KOManager koManager = new KOManager();
  private final List<WindLevelParameters> presets = new ArrayList<>();
  private final SegVisibilityPolicy segVisibility = new SegVisibilityPolicy();

  public View2d() {
    super();
    setInfoLayer(new InfoLayer());
  }

  @Override
  public InfoLayer getInfoLayer() {
    return infoLayer instanceof InfoLayer layer ? layer : new InfoLayer();
  }

  public void load(File dicom) throws Exception {
    this.file = dicom;
    DicomMediaIO io = DicomMediaIO.open(dicom);
    load(io.getDataset());
  }

  public void load(Attributes dataset) {
    this.dataset = Objects.requireNonNull(dataset, "dataset");
    bindWindowLevel(dataset);
    setModalityLut(
        dataset.getDouble(Tag.RescaleSlope, 1.0), dataset.getDouble(Tag.RescaleIntercept, 0.0));
    setFrameOfReferenceUID(dataset.getString(Tag.FrameOfReferenceUID, ""));
    applyDatasetFlags();
    render();
  }

  void bindWindowLevel(Attributes dataset) {
    fileWl = DicomMediaUtils.windowLevel(dataset, 400, 40);
    dataRangeWl = DicomMediaUtils.dataRangeWindowLevel(dataset);
    setPresets(DicomMediaUtils.voiPresets(dataset));
    this.activeVoi = fileWl;
    this.window = fileWl.getWindow();
    this.level = fileWl.getLevel();
    bindOp(fileWl);
  }

  public Attributes getDataset() {
    return dataset;
  }

  public File getFile() {
    return file;
  }

  public KOManager getKoManager() {
    return koManager;
  }

  @Override
  public boolean toggleKeyImage() {
    if (dataset == null) {
      return false;
    }
    return koManager.toggleKeyImage(dataset.getString(Tag.SOPInstanceUID));
  }

  public List<MediaElement> visibleMedias() {
    MediaSeries<? extends MediaElement> s = getSeries();
    return koManager.visibleMedias(s == null ? List.of() : s.getMedias());
  }

  public void applyKeyImageFilter() {
    if (!koManager.isFilterKeyImages()) {
      return;
    }
    jumpToVisible();
  }

  void jumpToVisible() {
    List<MediaElement> visible = visibleMedias();
    if (nothingToShow(visible)) {
      return;
    }
    selectFirstIfHidden(getSeries().getMedias(), visible);
  }

  boolean nothingToShow(List<MediaElement> visible) {
    return visible.isEmpty() || getSeries() == null;
  }

  void selectFirstIfHidden(List<? extends MediaElement> all, List<MediaElement> visible) {
    if (currentIsVisible(all, visible)) {
      return;
    }
    int idx = all.indexOf(visible.getFirst());
    if (idx >= 0) {
      setFrameIndex(idx);
    }
  }

  boolean currentIsVisible(List<? extends MediaElement> all, List<MediaElement> visible) {
    int idx = getFrameIndex();
    if (idx < 0 || idx >= all.size()) {
      return false;
    }
    return visible.contains(all.get(idx));
  }

  public double getWindow() {
    return window;
  }

  public double getLevel() {
    return level;
  }

  public void setWindowLevel(double window, double level) {
    applyVoi(shapedVoi(window, level));
  }

  public void applyWindowChrome(boolean on) {
    if (on) {
      applyNarrowFileWindow();
    } else {
      resetWinLevelDefaults();
    }
  }

  void applyNarrowFileWindow() {
    windowChrome = true;
    WindLevelParameters src = fileWindowOrActive();
    setWindowLevel(narrowWindow(src.getWindow()), src.getLevel());
  }

  WindLevelParameters fileWindowOrActive() {
    return fileWl != null ? fileWl : activeVoi;
  }

  static double narrowWindow(double window) {
    return Math.max(1.0, window / 4.0);
  }

  public boolean isWindowChrome() {
    return windowChrome;
  }

  public void applyCropChrome(boolean on) {
    cropChrome = on;
    render();
  }

  public boolean isCropChrome() {
    return cropChrome;
  }

  public void applyBrightnessChrome(boolean on) {
    brightnessChrome = on;
    render();
  }

  public boolean isBrightnessChrome() {
    return brightnessChrome;
  }

  public void applyAutoLevelsChrome(boolean on) {
    autoLevelsChrome = on;
    render();
  }

  public boolean isAutoLevelsChrome() {
    return autoLevelsChrome;
  }

  @Override
  public void setLut(String lut) {
    super.setLut(lut);
    render();
  }

  @Override
  public void setInverseLut(boolean invert) {
    super.setInverseLut(invert);
    render();
  }

  @Override
  public void setFilter(Object filter) {
    super.setFilter(filter);
    render();
  }

  void applyVoi(WindLevelParameters voi) {
    this.activeVoi = voi == null ? new WindLevelParameters(this.window, this.level) : voi;
    this.window = activeVoi.getWindow();
    this.level = activeVoi.getLevel();
    bindOp(activeVoi);
    render();
  }

  void bindOp(WindLevelParameters voi) {
    getDisplayOpManager()
        .setParamValue("op.window.presets", WindowAndPresetsOp.P_WINDOW, voi.getWindow());
    getDisplayOpManager()
        .setParamValue("op.window.presets", WindowAndPresetsOp.P_LEVEL, voi.getLevel());
    getDisplayOpManager()
        .setParamValue("op.window.presets", WindowAndPresetsOp.P_VOI_LUT_SHAPE, voi.getLutShape());
  }

  WindLevelParameters shapedVoi(double window, double level) {
    WindLevelParameters p = new WindLevelParameters(window, level);
    if (dataset != null) {
      p.setLutShape(LutPipeline.voiFunction(dataset));
    }
    return p;
  }

  public WindLevelParameters getActiveVoi() {
    return activeVoi;
  }

  public WindLevelParameters getFileWindowLevel() {
    return fileWl;
  }

  @Override
  public void resetWinLevelDefaults() {
    windowChrome = false;
    if (fileWl != null) {
      applyVoi(fileWl);
    }
  }

  public void setPresets(List<WindLevelParameters> presets) {
    this.presets.clear();
    if (presets != null) {
      this.presets.addAll(presets);
    }
  }

  public List<WindLevelParameters> getPresets() {
    return List.copyOf(presets);
  }

  @Override
  public void applyPreset(int index) {
    if (index <= 0) {
      applyDataRange();
      return;
    }
    applyPositivePreset(index);
  }

  void applyPositivePreset(int index) {
    if (presets.isEmpty()) {
      applyDataRange();
      return;
    }
    applyIndexedPreset(index);
  }

  void applyDataRange() {
    WindLevelParameters range = visibleDataRange();
    if (range != null) {
      setWindowLevel(range.getWindow(), range.getLevel());
    }
  }

  WindLevelParameters visibleDataRange() {
    if (dataRangeWl == null) {
      return fileWl;
    }
    if (presets.isEmpty() || !sameOverlayWindow(dataRangeWl, fileWl)) {
      return dataRangeWl;
    }
    return halfWindow(dataRangeWl);
  }

  static boolean sameOverlayWindow(WindLevelParameters a, WindLevelParameters b) {
    if (a == null || b == null) {
      return false;
    }
    return overlayWindow(a) == overlayWindow(b);
  }

  static int overlayWindow(WindLevelParameters range) {
    return (int) range.getWindow();
  }

  static WindLevelParameters halfWindow(WindLevelParameters range) {
    return new WindLevelParameters(Math.max(1.0, range.getWindow() / 2.0), range.getLevel());
  }

  void applyIndexedPreset(int index) {
    int i = Math.min(presets.size(), index) - 1;
    applyVoi(presets.get(i));
  }

  public SegVisibilityPolicy getSegVisibility() {
    return segVisibility;
  }

  @Override
  public void setSegmentationsVisible(boolean visible) {
    super.setSegmentationsVisible(visible);
    segVisibility.setVisible(visible);
  }

  @Override
  public void toggleSegmentations() {
    super.toggleSegmentations();
    segVisibility.setVisible(isSegmentationsVisible());
  }

  public void render() {
    if (dataset == null) {
      return;
    }
    BufferedImage painted = WindowLevelPainter.paintMonochrome2(dataset, activeVoi);
    painted = applyFilterAndColor(painted);
    painted = applyBrightness(painted);
    painted = applyAutoLevels(painted);
    painted = applyShutter(painted);
    painted = applyOverlay(painted);
    painted = applyCrop(painted);
    setSourceImage(painted);
  }

  /**
   * Copy already-painted pixels, VOI chrome, and paint-time flip; do not re-run {@link #render()}.
   */
  public void copyDisplay(View2d from) {
    if (from == null || from == this) {
      return;
    }
    copyMetadata(from);
    BufferedImage image = from.getSourceImage();
    if (image != null) {
      setSourceImage(image);
    }
    setFlip(from.isFlip());
  }

  void copyMetadata(View2d from) {
    this.dataset = from.dataset;
    this.file = from.file;
    this.fileWl = from.fileWl;
    this.dataRangeWl = from.dataRangeWl;
    this.activeVoi = from.activeVoi;
    this.window = from.window;
    this.level = from.level;
  }

  BufferedImage applyBrightness(BufferedImage src) {
    if (!brightnessChrome) {
      return src;
    }
    return runBrightnessOp(src);
  }

  static BufferedImage runBrightnessOp(BufferedImage src) {
    BrightnessOp op = new BrightnessOp();
    op.setParam(BrightnessOp.P_BRIGHTNESS, 48.0);
    op.setParam(ImageOpNode.INPUT_IMG, src);
    try {
      op.process();
    } catch (Exception e) {
      return src;
    }
    Object out = op.getParam(ImageOpNode.OUTPUT_IMG);
    return out instanceof BufferedImage img ? img : src;
  }

  BufferedImage applyAutoLevels(BufferedImage src) {
    if (!autoLevelsChrome) {
      return src;
    }
    return runAutoLevelsOp(src);
  }

  static BufferedImage runAutoLevelsOp(BufferedImage src) {
    AutoLevelsOp op = new AutoLevelsOp();
    op.setParam(ImageOpNode.INPUT_IMG, src);
    try {
      op.process();
    } catch (Exception e) {
      return src;
    }
    Object out = op.getParam(ImageOpNode.OUTPUT_IMG);
    return out instanceof BufferedImage img ? img : src;
  }

  BufferedImage applyCrop(BufferedImage src) {
    if (!cropChrome || dataset == null) {
      return src;
    }
    Rectangle region = centerHalf();
    if (region == null) {
      return src;
    }
    return runCropOp(src, region);
  }

  Rectangle centerHalf() {
    int w = dataset.getInt(Tag.Columns, 0);
    int h = dataset.getInt(Tag.Rows, 0);
    if (w < 2 || h < 2) {
      return null;
    }
    return new Rectangle(w / 4, h / 4, Math.max(1, w / 2), Math.max(1, h / 2));
  }

  static BufferedImage runCropOp(BufferedImage src, Rectangle region) {
    CropOp op = new CropOp();
    op.setParam(CropOp.P_REGION, region);
    op.setParam(ImageOpNode.INPUT_IMG, src);
    try {
      op.process();
    } catch (Exception e) {
      return src;
    }
    Object out = op.getParam(ImageOpNode.OUTPUT_IMG);
    return out instanceof BufferedImage img ? img : src;
  }

  BufferedImage applyFilterAndColor(BufferedImage src) {
    return applyInvert(applyKernel(src));
  }

  BufferedImage applyKernel(BufferedImage src) {
    return runFilterOp(src, getDisplayOpManager().getParamValue("op.filter", FilterOp.P_FILTER));
  }

  static BufferedImage runFilterOp(BufferedImage src, Object filter) {
    FilterOp op = new FilterOp();
    if (filter != null) {
      op.setParam(FilterOp.P_FILTER, filter);
    }
    op.setParam(ImageOpNode.INPUT_IMG, src);
    try {
      op.process();
    } catch (Exception e) {
      return src;
    }
    Object out = op.getParam(ImageOpNode.OUTPUT_IMG);
    return out instanceof BufferedImage img ? img : src;
  }

  BufferedImage applyInvert(BufferedImage src) {
    Object invert = getDisplayOpManager().getParamValue("op.pseudocolor", PseudoColorOp.P_INVERT);
    if (!Boolean.TRUE.equals(invert)) {
      return src;
    }
    WritableRaster raster = src.getRaster();
    byte[] data = ((DataBufferByte) raster.getDataBuffer()).getData();
    for (int i = 0; i < data.length; i++) {
      data[i] = (byte) (255 - (data[i] & 0xff));
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
  public void setFrameIndex(int frameIndex, boolean propagate) {
    super.setFrameIndex(frameIndex, propagate);
    loadFrameMedia();
  }

  void loadFrameMedia() {
    MediaSeries<? extends MediaElement> series = getSeries();
    if (series == null) {
      return;
    }
    List<? extends MediaElement> medias = series.getMedias();
    int index = getFrameIndex();
    if (index < 0 || index >= medias.size()) {
      return;
    }
    MediaElement media = medias.get(index);
    if (media == null || media.getMediaURI() == null) {
      return;
    }
    try {
      File file = new File(media.getMediaURI());
      if (file.isFile()) {
        load(file);
      }
    } catch (Exception ignored) {
      // stills with decoded pixels are applied in DefaultView2d.applyFramePixels
    }
  }

  @Override
  protected void paintDecorations(Graphics2D g) {
    super.paintDecorations(g);
    if (getInfoLayer() instanceof InfoLayer layer) {
      layer.paint(g, this);
    }
  }

  @Override
  protected ImageViewerEventManager createEventManager() {
    return new EventManager(this);
  }
}
