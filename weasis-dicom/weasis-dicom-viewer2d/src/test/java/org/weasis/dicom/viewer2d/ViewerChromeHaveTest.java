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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractButton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.image.AffineTransformOp;
import org.weasis.core.api.image.FilterOp;
import org.weasis.core.api.image.ImageOpNode;
import org.weasis.core.api.image.PseudoColorOp;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.core.ui.editor.image.HistogramData.ColorModel;
import org.weasis.core.ui.editor.image.HistogramView;
import org.weasis.core.ui.editor.image.MouseActions;
import org.weasis.core.ui.editor.image.RotationToolBar;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.editor.image.ViewerToolBar;
import org.weasis.core.ui.editor.image.ZoomToolBar;
import org.weasis.core.ui.editor.image.ZoomWin;
import org.weasis.core.ui.editor.image.dockable.MiniTool;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.dicom.viewer2d.dockable.ImageTool;
import org.weasis.dicom.viewer2d.mpr.MprAxis;
import org.weasis.dicom.viewer2d.mpr.MprContainer;

class ViewerChromeHaveTest {

  @Test
  void resetToolsApplyCommandTokens() {
    View2d view = new View2d();
    view.setZoom(2.0);
    view.setPan(4, 5);
    view.setRotation(90);
    view.setFlip(true);
    ResetTools bar = new ResetTools();
    assertEquals("Reset", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    bar.bind(view);
    bar.apply(org.weasis.core.ui.editor.image.ResetTools.ZOOM);
    assertEquals(-200.0, view.getZoom(), 1e-9);
    assertEquals(4.0, view.getPanX(), 1e-9);
    bar.apply(org.weasis.core.ui.editor.image.ResetTools.ALL);
    assertEquals(0.0, view.getPanX(), 1e-9);
    assertEquals(0.0, view.getRotation(), 1e-9);
    assertFalse(view.isFlip());
  }

  @Test
  void histogramDockBindsNamedRgbChrome() {
    View2dContainer container = new View2dContainer();
    HistogramView dock = container.getHistogramView();
    assertEquals("histogram", dock.getName());
    assertEquals("histogram-panel", dock.getPanel().getName());
    assertEquals("histogram-stats", dock.statsLabel().getName());
    assertEquals("histogram-gray", dock.grayButton().getName());
    assertEquals("histogram-rgb", dock.rgbButton().getName());
    assertEquals("histogram-hsv", dock.hsvButton().getName());
    assertEquals("histogram-hls", dock.hlsButton().getName());
    assertTrue(
        container.getSeriesViewerUI().getTools().stream()
            .anyMatch(b -> HistogramView.NAME.equals(b.getComponentName())));
    View2d view = container.getView2d();
    view.setSourceImage(new BufferedImage(4, 4, BufferedImage.TYPE_BYTE_GRAY));
    dock.bind(view);
    assertSame(view, dock.boundView());
    assertTrue(dock.statsText().startsWith("n="));
    assertTrue(dock.statsText().contains("n=16"));
    dock.rgbButton().doClick();
    assertEquals(ColorModel.RGB, dock.getHistogramColorModel());
    assertTrue(dock.getChannels().isVisible());
    assertEquals("flip", container.getImageTool().flipButton().getName());
  }

  @Test
  void miniToolDockBindsNamedZoomPanner() {
    View2dContainer container = new View2dContainer();
    MiniTool mini = container.getMiniTool();
    assertEquals("mini-tool", mini.getName());
    assertEquals("mini-zoom", mini.getZoomSlider().getName());
    assertEquals("mini-rotation", mini.getRotationSlider().getName());
    assertEquals("mini-series", mini.getSeriesSlider().getName());
    assertEquals("mini-panner", mini.getPanner().getName());
    assertEquals("mini-zoom-value", mini.zoomValueLabel().getName());
    assertTrue(
        container.getSeriesViewerUI().getTools().stream()
            .anyMatch(b -> MiniTool.NAME.equals(b.getComponentName())));
    assertEquals("histogram", container.getHistogramView().getName());
    View2d view = container.getView2d();
    view.setSourceImage(new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY));
    mini.bind(view);
    assertSame(view, mini.boundView());
    mini.getZoomSlider().setValue(200);
    assertEquals(2.0, view.getZoom(), 1e-9);
    assertEquals("200%", mini.zoomValueText());
    mini.getRotationSlider().setValue(90);
    assertEquals(90.0, view.getRotation(), 1e-9);
    assertEquals("flip", container.getImageTool().flipButton().getName());
  }

  @Test
  void viewerCrosshairSetsNamedPixelInfo() {
    View2dContainer container = new View2dContainer();
    ViewerToolBar bar = container.getViewerToolBar();
    assertEquals("pixel-info", bar.pixelInfoLabel().getName());
    assertEquals(ActionW.CROSSHAIR.cmd(), bar.getComponent(5).getName());
    View2d view = container.getView2d();
    BufferedImage src = new BufferedImage(8, 4, BufferedImage.TYPE_BYTE_GRAY);
    src.getRaster().setSample(2, 1, 0, 90);
    view.setSourceImage(src);
    view.setModalityLut(1.0, 0);
    ((AbstractButton) bar.getComponent(5)).doClick();
    assertEquals(MouseActions.CROSSHAIR, view.getMouseActions().getLeft());
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 2, 1));
    assertTrue(bar.pixelInfoText().contains("2,1"));
    assertTrue(bar.pixelInfoText().contains("v=90"));
    assertTrue(container.getImageTool().summaryText().contains("v=90"));
    assertEquals("mini-tool", container.getMiniTool().getName());
    assertEquals("histogram", container.getHistogramView().getName());
    assertEquals("flip", container.getImageTool().flipButton().getName());
  }

  @Test
  void lensDockBindsNamedFactorChrome() {
    View2dContainer container = new View2dContainer();
    ZoomWin lens = container.getZoomWin();
    assertEquals("lens", lens.getName());
    assertEquals("lens-factor", lens.getFactorSlider().getName());
    assertEquals("lens-factor-value", lens.factorValueLabel().getName());
    assertEquals("lens-panel", lens.previewPanel().getName());
    assertEquals("2x", lens.factorValueText());
    assertTrue(
        container.getSeriesViewerUI().getTools().stream()
            .anyMatch(b -> ZoomWin.NAME.equals(b.getComponentName())));
    assertEquals("mini-tool", container.getMiniTool().getName());
    assertEquals("histogram", container.getHistogramView().getName());
    assertEquals("pixel-info", container.getViewerToolBar().pixelInfoLabel().getName());
    View2d view = container.getView2d();
    view.setSourceImage(new BufferedImage(32, 32, BufferedImage.TYPE_BYTE_GRAY));
    lens.bind(view);
    assertSame(view, lens.boundView());
    lens.getFactorSlider().setValue(400);
    assertEquals(4.0, lens.getFactor(), 1e-9);
    assertEquals("4x", lens.factorValueText());
    assertEquals("flip", container.getImageTool().flipButton().getName());
  }

  @Test
  void lutToolBarSetsPseudoColorAndInvert() {
    View2d view = new View2d();
    LutToolBar bar = new LutToolBar();
    assertEquals("LUT", bar.getComponentName());
    bar.bind(view);
    assertEquals(PseudoColorOp.GRAY, view.getLut());
    bar.setLut("HotIron");
    assertEquals("HotIron", view.getLut());
    bar.toggleInvert();
    assertTrue(view.isInverseLut());
    bar.toggleInvert();
    assertFalse(view.isInverseLut());
  }

  @Test
  void inverseLutClickRendersComplement(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    int before = view.getSourceImage().getRaster().getSample(4, 4, 0);
    AbstractButton inverse = (AbstractButton) container.getLutToolBar().getComponent(3);
    assertEquals("Inverse", inverse.getText());
    assertEquals("inverseLut", inverse.getName());
    inverse.doClick();
    assertTrue(view.isInverseLut());
    int inverted = view.getSourceImage().getRaster().getSample(4, 4, 0);
    assertEquals(255 - before, inverted);
    view.setSize(8, 8);
    view.setZoom(1.0);
    view.setRotation(0);
    assertEquals(inverted, paint(view).getRGB(4, 4) & 0xFF);
    inverse.doClick();
    assertFalse(view.isInverseLut());
    assertEquals(before, view.getSourceImage().getRaster().getSample(4, 4, 0));
  }

  @Test
  void sharpenClickRendersKernelWithoutChangingInverse(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("peak.dcm");
    TestCt.writePeak(file.toFile());
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    int before = view.getSourceImage().getRaster().getSample(4, 4, 0);
    AbstractButton inverse = (AbstractButton) container.getLutToolBar().getComponent(3);
    AbstractButton sharpen = (AbstractButton) container.getLutToolBar().getComponent(4);
    assertEquals("Inverse", inverse.getText());
    assertEquals("inverseLut", inverse.getName());
    assertEquals("Sharpen", sharpen.getText());
    assertEquals(ActionW.FILTER.cmd(), sharpen.getName());
    sharpen.doClick();
    assertTrue(LutToolBar.sharpened(view.getFilter()));
    int sharp = view.getSourceImage().getRaster().getSample(4, 4, 0);
    assertTrue(sharp > before);
    view.setSize(8, 8);
    view.setZoom(1.0);
    view.setRotation(0);
    assertEquals(sharp, paint(view).getRGB(4, 4) & 0xFF);
    assertFalse(view.isInverseLut());
    AffineTransformOp affine = new AffineTransformOp();
    affine.setParam(ImageOpNode.INPUT_IMG, view.getSourceImage());
    affine.process();
    assertSame(view.getSourceImage(), affine.getParam(ImageOpNode.OUTPUT_IMG));
    sharpen.doClick();
    assertEquals(FilterOp.NONE, String.valueOf(view.getFilter()));
    assertEquals(before, view.getSourceImage().getRaster().getSample(4, 4, 0));
    assertEquals(before, paint(view).getRGB(4, 4) & 0xFF);
  }

  @Test
  void windowClickNarrowsFileWlOnPaintedViewWithoutChangingFlip(@TempDir Path dir)
      throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    int left = view.getSourceImage().getRaster().getSample(0, 4, 0);
    int right = view.getSourceImage().getRaster().getSample(7, 4, 0);
    AbstractButton window = container.getImageTool().windowButton();
    assertEquals("Window", window.getText());
    assertEquals(ActionW.WINDOW.cmd(), window.getName());
    assertEquals("flip", container.getImageTool().flipButton().getName());
    window.doClick();
    assertTrue(view.isWindowChrome());
    assertEquals(100.0, view.getWindow(), 1e-9);
    assertEquals(40.0, view.getLevel(), 1e-9);
    int leftNarrow = view.getSourceImage().getRaster().getSample(0, 4, 0);
    int rightNarrow = view.getSourceImage().getRaster().getSample(7, 4, 0);
    assertTrue(leftNarrow < left);
    assertTrue(rightNarrow > right);
    view.setSize(8, 8);
    view.setZoom(1.0);
    view.setRotation(0);
    assertEquals(leftNarrow, paint(view).getRGB(0, 4) & 0xFF);
    assertEquals(rightNarrow, paint(view).getRGB(7, 4) & 0xFF);
    assertFalse(view.isFlip());
    assertEquals(FilterOp.NONE, String.valueOf(view.getFilter()));
    AffineTransformOp affine = new AffineTransformOp();
    affine.setParam(ImageOpNode.INPUT_IMG, view.getSourceImage());
    affine.process();
    assertSame(view.getSourceImage(), affine.getParam(ImageOpNode.OUTPUT_IMG));
    window.doClick();
    assertFalse(view.isWindowChrome());
    assertEquals(400.0, view.getWindow(), 1e-9);
    assertEquals(left, view.getSourceImage().getRaster().getSample(0, 4, 0));
    assertEquals(right, view.getSourceImage().getRaster().getSample(7, 4, 0));
    assertNotEquals(leftNarrow, left);
  }

  @Test
  void cropClickCopiesCenterHalfWithoutChangingWindowOrFlip(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    int origin = view.getSourceImage().getRaster().getSample(2, 2, 0);
    int fullW = view.getSourceImage().getWidth();
    AbstractButton crop = container.getImageTool().cropButton();
    assertEquals("Crop", crop.getText());
    assertEquals(ImageTool.CROP, crop.getName());
    assertEquals(ActionW.WINDOW.cmd(), container.getImageTool().windowButton().getName());
    assertEquals("flip", container.getImageTool().flipButton().getName());
    crop.doClick();
    assertTrue(view.isCropChrome());
    assertEquals(4, view.getSourceImage().getWidth());
    assertEquals(4, view.getSourceImage().getHeight());
    assertEquals(origin, view.getSourceImage().getRaster().getSample(0, 0, 0));
    view.setSize(4, 4);
    view.setZoom(1.0);
    view.setRotation(0);
    assertEquals(origin, paint(view).getRGB(0, 0) & 0xFF);
    assertFalse(view.isWindowChrome());
    assertFalse(view.isFlip());
    assertEquals(FilterOp.NONE, String.valueOf(view.getFilter()));
    AffineTransformOp affine = new AffineTransformOp();
    affine.setParam(ImageOpNode.INPUT_IMG, view.getSourceImage());
    affine.process();
    assertSame(view.getSourceImage(), affine.getParam(ImageOpNode.OUTPUT_IMG));
    crop.doClick();
    assertFalse(view.isCropChrome());
    assertEquals(fullW, view.getSourceImage().getWidth());
    assertEquals(origin, view.getSourceImage().getRaster().getSample(2, 2, 0));
  }

  @Test
  void brightnessClickLiftsPixelsWithoutChangingCropOrFlip(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    int before = view.getSourceImage().getRaster().getSample(4, 4, 0);
    AbstractButton brightness = container.getImageTool().brightnessButton();
    assertEquals("Brightness", brightness.getText());
    assertEquals(ImageTool.BRIGHTNESS, brightness.getName());
    assertEquals(ImageTool.CROP, container.getImageTool().cropButton().getName());
    assertEquals("flip", container.getImageTool().flipButton().getName());
    brightness.doClick();
    assertTrue(view.isBrightnessChrome());
    int lifted = view.getSourceImage().getRaster().getSample(4, 4, 0);
    assertEquals(Math.min(255, before + 48), lifted);
    view.setSize(8, 8);
    view.setZoom(1.0);
    view.setRotation(0);
    assertEquals(lifted, paint(view).getRGB(4, 4) & 0xFF);
    assertFalse(view.isCropChrome());
    assertFalse(view.isWindowChrome());
    assertFalse(view.isFlip());
    assertEquals(FilterOp.NONE, String.valueOf(view.getFilter()));
    AffineTransformOp affine = new AffineTransformOp();
    affine.setParam(ImageOpNode.INPUT_IMG, view.getSourceImage());
    affine.process();
    assertSame(view.getSourceImage(), affine.getParam(ImageOpNode.OUTPUT_IMG));
    brightness.doClick();
    assertFalse(view.isBrightnessChrome());
    assertEquals(before, view.getSourceImage().getRaster().getSample(4, 4, 0));
    assertEquals(before, paint(view).getRGB(4, 4) & 0xFF);
  }

  @Test
  void autoLevelsClickStretchesMinMaxWithoutChangingBrightness(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    int left = view.getSourceImage().getRaster().getSample(0, 4, 0);
    int right = view.getSourceImage().getRaster().getSample(7, 4, 0);
    AbstractButton auto = container.getImageTool().autoLevelsButton();
    assertEquals("AutoLevels", auto.getText());
    assertEquals(ImageTool.AUTO_LEVELS, auto.getName());
    assertEquals(ImageTool.BRIGHTNESS, container.getImageTool().brightnessButton().getName());
    assertEquals("flip", container.getImageTool().flipButton().getName());
    auto.doClick();
    assertTrue(view.isAutoLevelsChrome());
    int leftStretch = view.getSourceImage().getRaster().getSample(0, 4, 0);
    int rightStretch = view.getSourceImage().getRaster().getSample(7, 4, 0);
    assertEquals(0, leftStretch);
    assertEquals(255, rightStretch);
    assertTrue(leftStretch < left);
    assertTrue(rightStretch > right);
    view.setSize(8, 8);
    view.setZoom(1.0);
    view.setRotation(0);
    assertEquals(0, paint(view).getRGB(0, 4) & 0xFF);
    assertEquals(255, paint(view).getRGB(7, 4) & 0xFF);
    assertFalse(view.isBrightnessChrome());
    assertFalse(view.isCropChrome());
    assertFalse(view.isWindowChrome());
    assertFalse(view.isFlip());
    assertEquals(FilterOp.NONE, String.valueOf(view.getFilter()));
    AffineTransformOp affine = new AffineTransformOp();
    affine.setParam(ImageOpNode.INPUT_IMG, view.getSourceImage());
    affine.process();
    assertSame(view.getSourceImage(), affine.getParam(ImageOpNode.OUTPUT_IMG));
    auto.doClick();
    assertFalse(view.isAutoLevelsChrome());
    assertEquals(left, view.getSourceImage().getRaster().getSample(0, 4, 0));
    assertEquals(right, view.getSourceImage().getRaster().getSample(7, 4, 0));
  }

  @Test
  void maskClickBlackensOutsideCenterWithoutChangingAutoLevels(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    int origin = view.getSourceImage().getRaster().getSample(2, 2, 0);
    int left = view.getSourceImage().getRaster().getSample(0, 4, 0);
    int fullW = view.getSourceImage().getWidth();
    AbstractButton mask = container.getImageTool().maskButton();
    assertEquals("Mask", mask.getText());
    assertEquals(ImageTool.MASK, mask.getName());
    assertEquals(ImageTool.AUTO_LEVELS, container.getImageTool().autoLevelsButton().getName());
    assertEquals("flip", container.getImageTool().flipButton().getName());
    assertTrue(left > 0);
    assertTrue(origin > 0);
    mask.doClick();
    assertTrue(view.isMaskChrome());
    assertEquals(fullW, view.getSourceImage().getWidth());
    assertEquals(8, view.getSourceImage().getHeight());
    assertEquals(0, view.getSourceImage().getRaster().getSample(0, 4, 0));
    assertEquals(origin, view.getSourceImage().getRaster().getSample(2, 2, 0));
    view.setSize(8, 8);
    view.setZoom(1.0);
    view.setRotation(0);
    assertEquals(0, paint(view).getRGB(0, 4) & 0xFF);
    assertEquals(origin, paint(view).getRGB(2, 2) & 0xFF);
    assertFalse(view.isAutoLevelsChrome());
    assertFalse(view.isBrightnessChrome());
    assertFalse(view.isCropChrome());
    assertFalse(view.isWindowChrome());
    assertFalse(view.isFlip());
    assertEquals(FilterOp.NONE, String.valueOf(view.getFilter()));
    AffineTransformOp affine = new AffineTransformOp();
    affine.setParam(ImageOpNode.INPUT_IMG, view.getSourceImage());
    affine.process();
    assertSame(view.getSourceImage(), affine.getParam(ImageOpNode.OUTPUT_IMG));
    mask.doClick();
    assertFalse(view.isMaskChrome());
    assertEquals(left, view.getSourceImage().getRaster().getSample(0, 4, 0));
    assertEquals(origin, view.getSourceImage().getRaster().getSample(2, 2, 0));
  }

  @Test
  void shutterClickBlackensEighthInsetWithoutChangingMask(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    int origin = view.getSourceImage().getRaster().getSample(2, 2, 0);
    int left = view.getSourceImage().getRaster().getSample(0, 4, 0);
    int inner = view.getSourceImage().getRaster().getSample(1, 4, 0);
    int fullW = view.getSourceImage().getWidth();
    AbstractButton shutter = container.getImageTool().shutterButton();
    assertEquals("Shutter", shutter.getText());
    assertEquals(ImageTool.SHUTTER, shutter.getName());
    assertEquals(ImageTool.MASK, container.getImageTool().maskButton().getName());
    assertEquals("flip", container.getImageTool().flipButton().getName());
    assertTrue(left > 0);
    assertTrue(inner > 0);
    assertTrue(origin > 0);
    shutter.doClick();
    assertTrue(view.isShutterChrome());
    assertEquals(fullW, view.getSourceImage().getWidth());
    assertEquals(8, view.getSourceImage().getHeight());
    assertEquals(0, view.getSourceImage().getRaster().getSample(0, 4, 0));
    assertEquals(inner, view.getSourceImage().getRaster().getSample(1, 4, 0));
    assertEquals(origin, view.getSourceImage().getRaster().getSample(2, 2, 0));
    view.setSize(8, 8);
    view.setZoom(1.0);
    view.setRotation(0);
    assertEquals(0, paint(view).getRGB(0, 4) & 0xFF);
    assertEquals(inner, paint(view).getRGB(1, 4) & 0xFF);
    assertFalse(view.isMaskChrome());
    assertFalse(view.isAutoLevelsChrome());
    assertFalse(view.isBrightnessChrome());
    assertFalse(view.isCropChrome());
    assertFalse(view.isWindowChrome());
    assertFalse(view.isFlip());
    assertEquals(FilterOp.NONE, String.valueOf(view.getFilter()));
    AffineTransformOp affine = new AffineTransformOp();
    affine.setParam(ImageOpNode.INPUT_IMG, view.getSourceImage());
    affine.process();
    assertSame(view.getSourceImage(), affine.getParam(ImageOpNode.OUTPUT_IMG));
    shutter.doClick();
    assertFalse(view.isShutterChrome());
    assertEquals(left, view.getSourceImage().getRaster().getSample(0, 4, 0));
    assertEquals(inner, view.getSourceImage().getRaster().getSample(1, 4, 0));
    assertEquals(origin, view.getSourceImage().getRaster().getSample(2, 2, 0));
  }

  @Test
  void dumpHeaderShowsSelectedTagsWithoutChangingImageTool(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    DcmHeaderToolBar header = container.getHeaderToolBar();
    AbstractButton dump = header.dumpButton();
    assertEquals("Dump", dump.getText());
    assertEquals(DcmHeaderToolBar.DUMP, dump.getName());
    assertEquals(DcmHeaderToolBar.DUMP_TEXT, header.dumpArea().getName());
    assertSame(view, header.boundView());
    int origin = view.getSourceImage().getRaster().getSample(4, 4, 0);
    dump.doClick();
    assertSame(view, header.boundView());
    String text = header.dumpArea().getText();
    assertTrue(text.contains("SYN-CT-0001"));
    assertTrue(text.contains("PatientID"));
    assertTrue(text.contains("[OW]"));
    assertTrue(text.contains("(0010,0020)") || text.contains("0010,0020"));
    assertEquals(text, header.lastDump());
    assertEquals(origin, view.getSourceImage().getRaster().getSample(4, 4, 0));
    assertFalse(view.isShutterChrome());
    assertFalse(view.isMaskChrome());
    assertFalse(view.isFlip());
    assertEquals(FilterOp.NONE, String.valueOf(view.getFilter()));
  }

  @Test
  void screenshotToolbarBindsPaintedViewAndNamesSaveChrome(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.load(file.toFile());
    org.weasis.core.ui.editor.image.ScreenshotToolBar bar = container.getScreenshotToolBar();
    assertEquals("screenshot", bar.button().getName());
    assertSame(view, bar.boundView());
    bar.dialog().ensureWindow();
    assertEquals("screenshot-dialog", bar.dialog().ensureWindow().getName());
    bar.dialog().setScope(org.weasis.core.ui.editor.image.ScreenshotDialog.Scope.NATIVE_PIXELS);
    Path dest = dir.resolve("shot.png");
    bar.dialog().setPath(dest.toString());
    bar.dialog().save();
    assertTrue(bar.dialog().statusText().startsWith("Saved "));
    assertTrue(java.nio.file.Files.size(dest) > 0);
    assertFalse(view.isShutterChrome());
    assertFalse(view.isFlip());
  }

  @Test
  void namedFlipMirrorsEveryDxHangCellWithoutRasterizingSource() throws Exception {
    View2dContainer container = new View2dContainer();
    container.applyHanging(1, 2);
    assertEquals(2, container.getLayoutCount());
    BufferedImage src = splitGray(512, 256);
    View2d primary = container.getView2d();
    View2d extra = container.getLayoutViews().get(1);
    primary.setSourceImage(src);
    primary.setZoom(AffineTransformOp.ZOOM_BEST_FIT);
    primary.setRotation(0);
    extra.copyDisplay(primary);
    extra.setZoom(AffineTransformOp.ZOOM_BEST_FIT);
    extra.setRotation(0);
    AbstractButton flip = container.getImageTool().flipButton();
    assertEquals("Flip", flip.getText());
    assertEquals("flip", flip.getName());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> ImageTool.NAME.equals(b.getComponentName())));
    int left = band(paintAt(primary, 400, 300), 24, 80);
    int right = band(paintAt(primary, 400, 300), 320, 376);
    assertTrue(left < right);
    assertTrue(band(paintAt(extra, 400, 300), 24, 80) < band(paintAt(extra, 400, 300), 320, 376));
    int srcLeft = src.getRaster().getSample(16, 128, 0);
    flip.doClick();
    assertTrue(primary.isFlip());
    assertTrue(extra.isFlip());
    assertTrue(flip.isSelected());
    assertEquals(srcLeft, src.getRaster().getSample(16, 128, 0));
    assertSame(src, primary.getSourceImage());
    assertSame(src, extra.getSourceImage());
    assertTrue(
        band(paintAt(primary, 400, 300), 24, 80) > band(paintAt(primary, 400, 300), 320, 376));
    assertTrue(band(paintAt(extra, 400, 300), 24, 80) > band(paintAt(extra, 400, 300), 320, 376));
    AffineTransformOp affine = new AffineTransformOp();
    affine.setParam(org.weasis.core.api.image.ImageOpNode.INPUT_IMG, src);
    affine.process();
    assertSame(src, affine.getParam(org.weasis.core.api.image.ImageOpNode.OUTPUT_IMG));
    flip.doClick();
    assertFalse(primary.isFlip());
    assertFalse(extra.isFlip());
    assertEquals(left, band(paintAt(primary, 400, 300), 24, 80));
    assertEquals(right, band(paintAt(primary, 400, 300), 320, 376));
    assertEquals(left, band(paintAt(extra, 400, 300), 24, 80));
    assertEquals(right, band(paintAt(extra, 400, 300), 320, 376));
  }

  @Test
  void namedFlipMirrorsImportedChestOnSingleView() throws Exception {
    View2dContainer container = new View2dContainer();
    assertEquals(1, container.getLayoutCount());
    BufferedImage src = splitGray(1929, 2207);
    View2d view = container.getView2d();
    view.setSourceImage(src);
    view.setZoom(AffineTransformOp.ZOOM_BEST_FIT);
    view.setRotation(0);
    container.getImageTool().bind(view);
    AbstractButton flip = container.getImageTool().flipButton();
    assertSame(view, container.getImageTool().boundView());
    int left = band(paintAt(view, 340, 500), 16, 80);
    int right = band(paintAt(view, 340, 500), 260, 324);
    assertTrue(left < right);
    int srcLeft = src.getRaster().getSample(20, 1100, 0);
    flip.doClick();
    assertTrue(view.isFlip());
    assertEquals(srcLeft, src.getRaster().getSample(20, 1100, 0));
    assertSame(src, view.getSourceImage());
    assertTrue(band(paintAt(view, 340, 500), 16, 80) > band(paintAt(view, 340, 500), 260, 324));
    AffineTransformOp affine = new AffineTransformOp();
    affine.setParam(org.weasis.core.api.image.ImageOpNode.INPUT_IMG, src);
    affine.process();
    assertSame(src, affine.getParam(org.weasis.core.api.image.ImageOpNode.OUTPUT_IMG));
    flip.doClick();
    assertFalse(view.isFlip());
    assertEquals(left, band(paintAt(view, 340, 500), 16, 80));
    assertEquals(right, band(paintAt(view, 340, 500), 260, 324));
  }

  @Test
  void view2dContainerWiresZoomAndRotationChrome() {
    View2dContainer container = new View2dContainer();
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> ZoomToolBar.NAME.equals(b.getComponentName())));
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> RotationToolBar.NAME.equals(b.getComponentName())));
    assertSame(container.getView2d(), container.getZoomToolBar().boundView());
    assertSame(container.getView2d(), container.getRotationToolBar().boundView());
    AbstractButton twoX = (AbstractButton) container.getZoomToolBar().getComponent(3);
    AbstractButton ninety = (AbstractButton) container.getRotationToolBar().getComponent(1);
    assertEquals("2x", twoX.getName());
    assertEquals("90°", ninety.getName());
    twoX.doClick();
    assertEquals(2.0, container.getView2d().getZoom(), 1e-9);
    ninety.doClick();
    assertEquals(90.0, container.getView2d().getRotation(), 1e-9);
    ((AbstractButton) container.getZoomToolBar().getComponent(0)).doClick();
    assertEquals(AffineTransformOp.ZOOM_BEST_FIT, container.getView2d().getZoom(), 1e-9);
    ((AbstractButton) container.getRotationToolBar().getComponent(0)).doClick();
    assertEquals(0.0, container.getView2d().getRotation(), 1e-9);
  }

  @Test
  void basic3DToolBarOpensMprContainer() {
    UICore core = new UICore();
    Basic3DToolBar bar = new Basic3DToolBar();
    assertEquals("Basic 3D", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    MprContainer opened = bar.openMpr(core);
    assertInstanceOf(MprContainer.class, opened);
    assertSame(opened, core.getSelectedViewerPlugin());
  }

  @Test
  void view2dContainerBasic3DOpensMprTabWithThreePlanes() {
    UICore core = UICore.getInstance();
    closePlugins(core);
    View2dContainer twoD = new View2dContainer();
    try {
      core.openViewerPlugin(twoD);
      assertTrue(
          twoD.getSeriesViewerUI().getToolBar().stream()
              .anyMatch(b -> Basic3DToolBar.NAME.equals(b.getComponentName())));
      AbstractButton mpr = (AbstractButton) twoD.getBasic3DToolBar().getComponent(0);
      AbstractButton volume = (AbstractButton) twoD.getBasic3DToolBar().getComponent(1);
      assertEquals("MPR", mpr.getText());
      assertEquals("mpr", mpr.getName());
      assertEquals("3D", volume.getText());
      assertEquals("3d", volume.getName());
      mpr.doClick();
      assertInstanceOf(MprContainer.class, core.getSelectedViewerPlugin());
      assertEquals(2, core.getOpenViewerPlugins().size());
      MprContainer opened = (MprContainer) core.getSelectedViewerPlugin();
      assertEquals(MprContainer.NAME, opened.getPluginName());
      assertEquals(3, opened.getPlaneGrid().getComponentCount());
      assertSame(opened.getController().getAxial(), opened.getPlaneGrid().getComponent(0));
      assertSame(opened.getController().getCoronal(), opened.getPlaneGrid().getComponent(1));
      assertSame(opened.getController().getSagittal(), opened.getPlaneGrid().getComponent(2));
      assertEquals(MprAxis.AXIAL, opened.getController().getAxial().getAxis());
      assertEquals(MprAxis.CORONAL, opened.getController().getCoronal().getAxis());
      assertEquals(MprAxis.SAGITTAL, opened.getController().getSagittal().getAxis());
    } finally {
      closePlugins(core);
    }
  }

  @Test
  void basic3DToolBarOpensVolumeViewerWhenFactoryRegistered() {
    UICore core = new UICore();
    core.registerSeriesViewerFactory(volumeStub());
    Basic3DToolBar bar = new Basic3DToolBar();
    ViewerPlugin<?> opened = bar.open3d(core);
    assertEquals(Basic3DToolBar.VOLUME_VIEWER, opened.getPluginName());
    assertSame(opened, core.getSelectedViewerPlugin());
  }

  @Test
  void view2dContainerBasic3DOpensVolumeTabWhenFactoryRegistered() {
    UICore core = UICore.getInstance();
    closePlugins(core);
    SeriesViewerFactory stub = volumeStub();
    core.registerSeriesViewerFactory(stub);
    View2dContainer twoD = new View2dContainer();
    try {
      core.openViewerPlugin(twoD);
      AbstractButton volume = (AbstractButton) twoD.getBasic3DToolBar().getComponent(1);
      assertEquals("3D", volume.getText());
      assertEquals("3d", volume.getName());
      volume.doClick();
      assertEquals(Basic3DToolBar.VOLUME_VIEWER, core.getSelectedViewerPlugin().getPluginName());
      assertEquals(2, core.getOpenViewerPlugins().size());
    } finally {
      closePlugins(core);
      core.unregisterSeriesViewerFactory(stub);
    }
  }

  @Test
  void view2dContainerExposesViewerLutResetAndCineToolbars() {
    View2dContainer container = new View2dContainer();
    assertTrue(container.getToolBars().getComponentCount() >= 8);
    assertEquals("Viewer", container.getViewerToolBar().getComponentName());
    assertEquals("LUT", container.getLutToolBar().getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, container.getViewerToolBar().getType());
    assertSame(container.getView2d(), container.getViewerToolBar().boundView());
    assertSame(container.getView2d(), container.getLutToolBar().boundView());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> "LUT".equals(b.getComponentName())));
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> "Key Object".equals(b.getComponentName())));
    javax.swing.AbstractButton star =
        (javax.swing.AbstractButton) container.getKeyObjectToolBar().getComponent().getComponent(0);
    javax.swing.AbstractButton filter =
        (javax.swing.AbstractButton) container.getKeyObjectToolBar().getComponent().getComponent(1);
    assertEquals("Star", star.getText());
    assertEquals("Filter", filter.getText());
    assertSame(container.getView2d(), container.getKeyObjectToolBar().boundView());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> "Measure".equals(b.getComponentName())));
    javax.swing.AbstractButton distance =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0);
    javax.swing.AbstractButton angle =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(1);
    javax.swing.AbstractButton polyline =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(2);
    assertEquals("D", distance.getText());
    assertEquals("A", angle.getText());
    assertEquals("Y", polyline.getText());
    assertEquals(
        "G",
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(3))
            .getText());
    assertEquals(
        "B",
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(4))
            .getText());
    assertSame(container.getView2d(), container.getMeasureToolBar().boundView());
    assertEquals(
        "Key Object", container.getSeriesViewerUI().getToolBar().get(1).getComponentName());
    assertEquals("Measure", container.getSeriesViewerUI().getToolBar().get(2).getComponentName());
  }

  @Test
  void measureDayButtonsDrawAndPaintLengthOnView2d() {
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.setSize(200, 200);
    view.setSourceImage(new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY));
    view.setZoom(2.0);
    javax.swing.AbstractButton d =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0);
    org.weasis.core.ui.editor.image.MeasureToolBar.pressRelease(d);
    assertEquals("D", container.getMeasureToolBar().getSelected());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 20, 20));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 80, 20));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 80, 20));
    assertEquals(1, view.getGraphicList().size());
    assertTrue(view.getGraphicList().getFirst() instanceof LineGraphic);
    LineGraphic line = (LineGraphic) view.getGraphicList().getFirst();
    assertEquals(30.0, line.getLength(), 0.01);
    assertTrue(line.getLabel()[0].contains("px"));
    BufferedImage page = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = page.createGraphics();
    try {
      view.paint(g);
    } finally {
      g.dispose();
    }
    assertTrue(yellowStrokeOnChest(page));
  }

  @Test
  void mouseLeftActionMeasurePaintsOnLayoutExtra() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(2);
    View2d extra = container.getLayoutViews().get(1);
    extra.setSize(200, 200);
    extra.setSourceImage(new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY));
    extra.setZoom(2.0);
    DicomView2dCommands cmd = new DicomView2dCommands(container.getView2d());
    assertEquals(
        org.weasis.core.ui.editor.image.MouseActions.MEASURE, cmd.mouseLeftAction("measure"));
    assertEquals(
        org.weasis.core.ui.editor.image.MouseActions.MEASURE, extra.getMouseActions().getLeft());
    extra.dispatchEvent(mouse(extra, MouseEvent.MOUSE_PRESSED, 20, 20));
    extra.dispatchEvent(mouse(extra, MouseEvent.MOUSE_DRAGGED, 80, 20));
    extra.dispatchEvent(mouse(extra, MouseEvent.MOUSE_RELEASED, 80, 20));
    assertEquals(1, extra.getGraphicList().size());
    assertTrue(extra.getGraphicList().getFirst() instanceof LineGraphic);
    BufferedImage page = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = page.createGraphics();
    try {
      extra.paint(g);
    } finally {
      g.dispose();
    }
    assertTrue(yellowStrokeOnChest(page));
  }

  @Test
  void mouseLeftActionDrawSelectsRectNotDistance() {
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    DicomView2dCommands cmd = new DicomView2dCommands(view);
    javax.swing.AbstractButton distance =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0);
    javax.swing.AbstractButton rect =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(3);
    assertTrue(distance.isSelected());
    assertEquals(org.weasis.core.ui.editor.image.MouseActions.DRAW, cmd.mouseLeftAction("draw"));
    assertTrue(rect.isSelected());
    assertFalse(distance.isSelected());
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.RECTANGLE, view.activeMeasureTool());
    assertEquals(
        org.weasis.core.ui.editor.image.MouseActions.DRAW, view.getMouseActions().getLeft());
    for (View2d cell : container.getLayoutViews()) {
      assertEquals(
          org.weasis.core.ui.editor.image.dockable.MeasureTool.RECTANGLE, cell.activeMeasureTool());
    }
  }

  @Test
  void mouseLeftActionMeasureKeepsAngleNotDistance() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 1);
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.ANGLE, view.activeMeasureTool());
    DicomView2dCommands cmd = new DicomView2dCommands(view);
    assertEquals(
        org.weasis.core.ui.editor.image.MouseActions.MEASURE, cmd.mouseLeftAction("measure"));
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.ANGLE, view.activeMeasureTool());
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(1))
            .isSelected());
    assertFalse(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0))
            .isSelected());
  }

  @Test
  void distancePaintsYellowOnBestFitChestRaster() {
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.setSize(400, 400);
    view.setSourceImage(new BufferedImage(2000, 2000, BufferedImage.TYPE_BYTE_GRAY));
    javax.swing.AbstractButton d =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0);
    org.weasis.core.ui.editor.image.MeasureToolBar.pressRelease(d);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 240, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 240, 40));
    assertEquals(1, view.getGraphicList().size());
    assertTrue(((LineGraphic) view.getGraphicList().getFirst()).getLength() > 1.0);
    assertTrue(view.getGraphicList().getFirst().getLabel()[0].contains("px"));
    BufferedImage page = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = page.createGraphics();
    try {
      view.paint(g);
    } finally {
      g.dispose();
    }
    assertTrue(yellowStrokeOnChest(page));
  }

  @Test
  void angleClickDragReleasePaintsDegreesOnChest() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 1);
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(1))
            .isSelected());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 40, 160));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 40, 160));
    assertEquals(1, view.getGraphicList().size());
    assertFalse(view.getGraphicList().getFirst() instanceof LineGraphic);
    org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic angle =
        (org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic)
            view.getGraphicList().getFirst();
    assertTrue(angle.getAngleDegrees() > 1.0);
    assertTrue(angle.getLabel()[0].contains("°"));
    assertTrue(yellowStrokeOnChest(paint(view)));
  }

  @Test
  void polylineClickDragPaintsLengthOnChest() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 2);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 240, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 240, 40));
    assertEquals(1, view.getGraphicList().size());
    org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic poly =
        (org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic)
            view.getGraphicList().getFirst();
    assertTrue(poly.getLabel()[0].contains("px"));
    assertTrue(yellowStrokeOnChest(paint(view)));
  }

  @Test
  void rectangleGPaintsClosedRoiAndStatsOnChest() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 3);
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(3))
            .isSelected());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 200, 160));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 200, 160));
    assertEquals(1, view.getGraphicList().size());
    assertFalse(view.getGraphicList().getFirst() instanceof LineGraphic);
    assertTrue(
        view.getGraphicList().getFirst()
            instanceof org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic);
    org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic roi =
        (org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic)
            view.getGraphicList().getFirst();
    assertNotNull(roi.getShape());
    assertTrue(
        org.weasis.core.ui.editor.image.ImageRegionStatistics.compute(
                    view.getSourceImage(), roi.getShape(), 1.0, 0.0)
                .getSamples()
            > 0);
    BufferedImage page = paint(view);
    assertTrue(yellowStrokeOnChest(page));
  }

  @Test
  void clickAAndGAfterDistanceMustNotCreateLineGraphic() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 0);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 240, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 240, 40));
    assertTrue(view.getGraphicList().getFirst() instanceof LineGraphic);
    clickMeasure(container, 1);
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(1))
            .isSelected());
    ((javax.swing.AbstractButton) container.getViewerToolBar().getComponent(6)).doClick();
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.ANGLE, view.activeMeasureTool());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 80));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 40, 200));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 40, 200));
    assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
    assertTrue(
        view.getGraphicList().getLast()
            instanceof org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic);
    clickMeasure(container, 3);
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(3))
            .isSelected());
    ((javax.swing.AbstractButton) container.getViewerToolBar().getComponent(6)).doClick();
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.RECTANGLE, view.activeMeasureTool());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 60, 60));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 180, 160));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 180, 160));
    assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
    assertTrue(
        view.getGraphicList().getLast()
            instanceof org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic);
  }

  @Test
  void selectThenDeleteRemovesPaintedGraphicFromChest() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 0);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 240, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 240, 40));
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.NONE);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 120, 40));
    assertEquals(1, view.getSelectedGraphics().size());
    BufferedImage selected = paint(view);
    assertTrue(yellowStrokeOnChest(selected));
    assertTrue(whiteHandleOnChest(selected));
    javax.swing.JPanel explorer = new javax.swing.JPanel();
    org.weasis.core.ui.editor.image.ImageViewerEventManager.DrawStroke.rememberView(view);
    org.weasis.core.ui.editor.image.ImageViewerEventManager.DrawStroke.deleteOutside(
        new KeyEvent(explorer, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_DELETE, '\0'));
    assertTrue(view.getGraphicList().isEmpty());
    assertFalse(yellowStrokeOnChest(paint(view)));
    assertFalse(whiteHandleOnChest(paint(view)));
  }

  static View2dContainer chestContainer() {
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.setSize(400, 400);
    view.setSourceImage(new BufferedImage(2000, 2000, BufferedImage.TYPE_BYTE_GRAY));
    return container;
  }

  static void clickMeasure(View2dContainer container, int index) {
    javax.swing.AbstractButton button =
        (javax.swing.AbstractButton)
            container.getMeasureToolBar().getComponent().getComponent(index);
    org.weasis.core.ui.editor.image.MeasureToolBar.pressRelease(button);
  }

  static BufferedImage paint(View2d view) {
    BufferedImage page = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = page.createGraphics();
    try {
      view.paint(g);
    } finally {
      g.dispose();
    }
    return page;
  }

  static BufferedImage paintAt(View2d view, int w, int h) {
    view.setSize(w, h);
    BufferedImage page = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = page.createGraphics();
    try {
      view.paint(g);
    } finally {
      g.dispose();
    }
    return page;
  }

  static BufferedImage splitGray(int w, int h) {
    BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        src.getRaster().setSample(x, y, 0, x < w / 2 ? 20 : 220);
      }
    }
    return src;
  }

  static int band(BufferedImage page, int x0, int x1) {
    int y0 = page.getHeight() / 2 - 8;
    int y1 = page.getHeight() / 2 + 8;
    long sum = 0;
    int n = 0;
    for (int y = y0; y < y1; y++) {
      for (int x = x0; x < x1; x++) {
        sum += page.getRGB(x, y) & 0xFF;
        n++;
      }
    }
    return n == 0 ? 0 : (int) (sum / n);
  }

  @Test
  void tabCyclesLayoutViewsWhenMoreThanOne() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(3);
    View2d first = container.getView2d();
    first.getEventManager().keyPressed(tab(first, 0));
    assertEquals(1, container.getLayoutIndex());
    first.getEventManager().keyPressed(tab(first, InputEvent.SHIFT_DOWN_MASK));
    assertEquals(0, container.getLayoutIndex());
    first.getEventManager().keyPressed(tab(first, InputEvent.CTRL_DOWN_MASK));
    assertEquals(0, container.getLayoutIndex());
  }

  @Test
  void viewMenuResetClearsZoomPanRotation() {
    View2dContainer container = new View2dContainer();
    container.getView2d().setZoom(2.0);
    container.getView2d().setPan(4, 5);
    container.getView2d().setRotation(90);
    container.setLayoutCount(2);
    assertEquals(2, container.getLayoutCount());
    container.resetDisplay();
    assertEquals(0.0, container.getView2d().getPanX(), 1e-9);
    assertEquals(0.0, container.getView2d().getRotation(), 1e-9);
  }

  @Test
  void oneByTwoAndTwoByTwoSplitTheSelectedPlugin() {
    View2dContainer container = new View2dContainer();
    assertEquals(1, container.getViewGrid().getComponentCount());
    container.setLayoutCount(2);
    assertEquals(2, container.getViewGrid().getComponentCount());
    GridLayout oneByTwo = (GridLayout) container.getViewGrid().getLayout();
    assertEquals(1, oneByTwo.getRows());
    assertEquals(2, oneByTwo.getColumns());
    assertSame(container.getLayoutViews().get(0), container.getViewGrid().getComponent(0));
    assertSame(container.getLayoutViews().get(1), container.getViewGrid().getComponent(1));
    container.setLayoutCount(4);
    assertEquals(4, container.getViewGrid().getComponentCount());
    GridLayout twoByTwo = (GridLayout) container.getViewGrid().getLayout();
    assertEquals(2, twoByTwo.getRows());
    assertEquals(2, twoByTwo.getColumns());
    container.setLayoutCount(1);
    assertEquals(1, container.getViewGrid().getComponentCount());
  }

  @Test
  void editSelectAllSelectsDrawingsOnFocusedView() {
    View2dContainer container = new View2dContainer();
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(4, 0));
    container.getView2d().addGraphic(line);
    container.selectAllGraphics();
    assertTrue(Boolean.TRUE.equals(line.getSelected()));
    container.deselectAllGraphics();
    assertTrue(container.getView2d().getSelectedGraphics().isEmpty());
  }

  @Test
  void deleteAllGraphicsClearsEveryLayoutCellIncludingEmptyBr() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(4);
    View2d bl = container.getLayoutViews().get(2);
    View2d br = container.getLayoutViews().get(3);
    LineGraphic onBl = new LineGraphic();
    onBl.setHandlePoint(0, new Point2D.Double(0, 0));
    onBl.setHandlePoint(1, new Point2D.Double(4, 0));
    bl.addGraphic(onBl);
    LineGraphic onBr = new LineGraphic();
    onBr.setHandlePoint(0, new Point2D.Double(0, 0));
    onBr.setHandlePoint(1, new Point2D.Double(4, 0));
    br.addGraphic(onBr);
    container.deleteAllGraphics();
    for (View2d cell : container.getLayoutViews()) {
      assertTrue(cell.getGraphicList().isEmpty());
    }
  }

  static KeyEvent tab(View2d view, int mods) {
    return new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, mods, KeyEvent.VK_TAB, '\t');
  }

  static MouseEvent mouse(View2d view, int id, int x, int y) {
    int mods = id == MouseEvent.MOUSE_RELEASED ? 0 : InputEvent.BUTTON1_DOWN_MASK;
    return new MouseEvent(view, id, 0L, mods, x, y, 1, false, MouseEvent.BUTTON1);
  }

  static boolean yellowStrokeOnChest(BufferedImage page) {
    int maxX = Math.min(page.getWidth() - 1, 360);
    int maxY = Math.min(page.getHeight() - 1, 200);
    for (int y = 10; y < maxY; y++) {
      for (int x = 10; x < maxX; x++) {
        if (yellowAt(page, x, y)) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean yellowAt(BufferedImage page, int x, int y) {
    for (int dy = -2; dy <= 2; dy++) {
      for (int dx = -2; dx <= 2; dx++) {
        int px = x + dx;
        int py = y + dy;
        if (px < 0 || py < 0 || px >= page.getWidth() || py >= page.getHeight()) {
          continue;
        }
        int rgb = page.getRGB(px, py);
        int r = (rgb >> 16) & 255;
        int green = (rgb >> 8) & 255;
        int b = rgb & 255;
        if (r > 200 && green > 200 && b < 80) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean whiteHandleOnChest(BufferedImage page) {
    int maxX = Math.min(page.getWidth() - 1, 360);
    int maxY = Math.min(page.getHeight() - 1, 200);
    for (int y = 10; y < maxY; y++) {
      for (int x = 10; x < maxX; x++) {
        int rgb = page.getRGB(x, y);
        int r = (rgb >> 16) & 255;
        int green = (rgb >> 8) & 255;
        int b = rgb & 255;
        if (r > 220 && green > 220 && b > 220) {
          return true;
        }
      }
    }
    return false;
  }

  static void closePlugins(UICore core) {
    for (ViewerPlugin<?> plugin : List.copyOf(core.getOpenViewerPlugins())) {
      core.closeViewerPlugin(plugin);
    }
  }

  static SeriesViewerFactory volumeStub() {
    return new SeriesViewerFactory() {
      @Override
      public SeriesViewer<?> createSeriesViewer(Hashtable<String, Object> properties) {
        return new ViewerPlugin<MediaElement>(Basic3DToolBar.VOLUME_VIEWER) {};
      }

      @Override
      public boolean canReadMimeType(String mimeType) {
        return false;
      }

      @Override
      public boolean isViewerCreatedByThisFactory(SeriesViewer<?> viewer) {
        return viewer instanceof ViewerPlugin<?> plugin
            && Basic3DToolBar.VOLUME_VIEWER.equals(plugin.getPluginName());
      }

      @Override
      public int getLevel() {
        return 120;
      }

      @Override
      public boolean canAddSeries() {
        return true;
      }

      @Override
      public boolean canExternalizeSeries() {
        return true;
      }

      @Override
      public String getUIName() {
        return Basic3DToolBar.VOLUME_VIEWER;
      }

      @Override
      public String getDescription() {
        return Basic3DToolBar.VOLUME_VIEWER;
      }

      @Override
      public String getIconPath() {
        return null;
      }

      @Override
      public String getSeriesViewerName() {
        return Basic3DToolBar.VOLUME_VIEWER;
      }

      @Override
      public String getClassName() {
        return "org.weasis.dicom.viewer3d.View3DContainer";
      }
    };
  }
}
