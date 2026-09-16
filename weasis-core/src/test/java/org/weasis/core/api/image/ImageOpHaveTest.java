/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.junit.jupiter.api.Test;

class ImageOpHaveTest {

  @Test
  void flipHorizontalMirrorsBufferedImage() throws Exception {
    BufferedImage src = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0xFF0000);
    src.setRGB(1, 0, 0x0000FF);
    FlipOp off = new FlipOp();
    off.setParam(ImageOpNode.INPUT_IMG, src);
    off.process();
    assertSame(src, off.getParam(ImageOpNode.OUTPUT_IMG));

    FlipOp on = new FlipOp();
    on.setParam(FlipOp.P_HORIZONTAL, Boolean.TRUE);
    on.setParam(ImageOpNode.INPUT_IMG, src);
    on.process();
    BufferedImage out = (BufferedImage) on.getParam(ImageOpNode.OUTPUT_IMG);
    assertEquals(0xFF0000FF, out.getRGB(0, 0));
    assertEquals(0xFFFF0000, out.getRGB(1, 0));
  }

  @Test
  void linearWindowMapsLevelToMidGrayAndSigmoidDiffersAtUpper() throws Exception {
    BufferedImage src = gray(40);
    WindowOp linear = new WindowOp();
    linear.setParam(WindowOp.P_WINDOW, 80.0);
    linear.setParam(WindowOp.P_LEVEL, 40.0);
    linear.setParam(WindowOp.P_VOI_LUT_SHAPE, "LINEAR");
    linear.setParam(ImageOpNode.INPUT_IMG, src);
    linear.process();
    assertEquals(128, grayAt(linear));

    BufferedImage high = gray(80);
    WindowOp linUpper = new WindowOp();
    linUpper.setParam(WindowOp.P_WINDOW, 80.0);
    linUpper.setParam(WindowOp.P_LEVEL, 40.0);
    linUpper.setParam(WindowOp.P_VOI_LUT_SHAPE, "LINEAR");
    linUpper.setParam(ImageOpNode.INPUT_IMG, high);
    linUpper.process();
    WindowOp sigUpper = new WindowOp();
    sigUpper.setParam(WindowOp.P_WINDOW, 80.0);
    sigUpper.setParam(WindowOp.P_LEVEL, 40.0);
    sigUpper.setParam(WindowOp.P_VOI_LUT_SHAPE, "SIGMOID");
    sigUpper.setParam(ImageOpNode.INPUT_IMG, high);
    sigUpper.process();
    assertEquals(255, grayAt(linUpper));
    assertEquals(225, grayAt(sigUpper));
    assertNotEquals(grayAt(linUpper), grayAt(sigUpper));
  }

  @Test
  void windowAndPresetsChainNodeAppliesWindowWhenParamsSet() throws Exception {
    WindowAndPresetsOp op = new WindowAndPresetsOp();
    BufferedImage src = gray(40);
    op.setParam(ImageOpNode.INPUT_IMG, src);
    op.process();
    assertSame(src, op.getParam(ImageOpNode.OUTPUT_IMG));
    op.setParam(WindowAndPresetsOp.P_WINDOW, 80.0);
    op.setParam(WindowAndPresetsOp.P_LEVEL, 40.0);
    op.process();
    assertEquals(128, grayAt(op));
  }

  @Test
  void rotation90CwPutsLeftPixelOnTop() throws Exception {
    BufferedImage src = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0xFF0000);
    src.setRGB(1, 0, 0x0000FF);
    RotationOp zero = new RotationOp();
    zero.setParam(ImageOpNode.INPUT_IMG, src);
    zero.process();
    assertSame(src, zero.getParam(ImageOpNode.OUTPUT_IMG));

    RotationOp ninety = new RotationOp();
    ninety.setParam(RotationOp.P_ROTATION, 90.0);
    ninety.setParam(ImageOpNode.INPUT_IMG, src);
    ninety.process();
    BufferedImage out = (BufferedImage) ninety.getParam(ImageOpNode.OUTPUT_IMG);
    assertEquals(1, out.getWidth());
    assertEquals(2, out.getHeight());
    assertEquals(0xFFFF0000, out.getRGB(0, 0));
    assertEquals(0xFF0000FF, out.getRGB(0, 1));

    RotationOp half = new RotationOp();
    half.setParam(RotationOp.P_ROTATION, 180.0);
    half.setParam(ImageOpNode.INPUT_IMG, src);
    half.process();
    BufferedImage flipped = (BufferedImage) half.getParam(ImageOpNode.OUTPUT_IMG);
    assertEquals(2, flipped.getWidth());
    assertEquals(0xFF0000FF, flipped.getRGB(0, 0));
    assertEquals(0xFFFF0000, flipped.getRGB(1, 0));
  }

  @Test
  void zoomZeroAndRealSizePassthroughAndBestFitScales() throws Exception {
    BufferedImage src = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0xFF0000);
    ZoomOp zero = new ZoomOp();
    zero.setParam(ImageOpNode.INPUT_IMG, src);
    zero.process();
    assertSame(src, zero.getParam(ImageOpNode.OUTPUT_IMG));

    ZoomOp real = new ZoomOp();
    real.setParam(ZoomOp.P_ZOOM, AffineTransformOp.ZOOM_REAL_SIZE);
    real.setParam(ImageOpNode.INPUT_IMG, src);
    real.process();
    assertSame(src, real.getParam(ImageOpNode.OUTPUT_IMG));

    ZoomOp two = new ZoomOp();
    two.setParam(ZoomOp.P_ZOOM, 2.0);
    two.setParam(ImageOpNode.INPUT_IMG, src);
    two.process();
    BufferedImage doubled = (BufferedImage) two.getParam(ImageOpNode.OUTPUT_IMG);
    assertEquals(2, doubled.getWidth());
    assertEquals(2, doubled.getHeight());
    assertEquals(0xFFFF0000, doubled.getRGB(0, 0));
    assertEquals(0xFFFF0000, doubled.getRGB(1, 1));

    ZoomOp fit = new ZoomOp();
    fit.setParam(ZoomOp.P_ZOOM, AffineTransformOp.ZOOM_BEST_FIT);
    fit.setParam(ZoomOp.P_VIEW_WIDTH, 6);
    fit.setParam(ZoomOp.P_VIEW_HEIGHT, 4);
    BufferedImage square = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
    square.setRGB(0, 0, 0xFF0000);
    square.setRGB(1, 0, 0xFF0000);
    square.setRGB(0, 1, 0xFF0000);
    square.setRGB(1, 1, 0xFF0000);
    fit.setParam(ImageOpNode.INPUT_IMG, square);
    fit.process();
    BufferedImage fitted = (BufferedImage) fit.getParam(ImageOpNode.OUTPUT_IMG);
    assertEquals(4, fitted.getWidth());
    assertEquals(4, fitted.getHeight());
  }

  @Test
  void cropRegionCopiesPixelsWithoutSharingRaster() throws Exception {
    BufferedImage src = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0xFF0000);
    src.setRGB(1, 0, 0x00FF00);
    src.setRGB(0, 1, 0x0000FF);
    src.setRGB(1, 1, 0xFFFFFF);
    CropOp none = new CropOp();
    none.setParam(ImageOpNode.INPUT_IMG, src);
    none.process();
    assertSame(src, none.getParam(ImageOpNode.OUTPUT_IMG));

    CropOp crop = new CropOp();
    crop.setParam(CropOp.P_REGION, new Rectangle(1, 0, 1, 2));
    crop.setParam(ImageOpNode.INPUT_IMG, src);
    crop.process();
    BufferedImage out = (BufferedImage) crop.getParam(ImageOpNode.OUTPUT_IMG);
    assertEquals(1, out.getWidth());
    assertEquals(2, out.getHeight());
    assertEquals(0xFF00FF00, out.getRGB(0, 0));
    assertEquals(0xFFFFFFFF, out.getRGB(0, 1));
    assertNotSame(src, out);
    out.setRGB(0, 0, 0);
    assertEquals(0xFF00FF00, src.getRGB(1, 0));
  }

  @Test
  void brightnessIdentityPassthroughAndRescaleMapsGray() throws Exception {
    BufferedImage src = gray(40);
    BrightnessOp identity = new BrightnessOp();
    identity.setParam(ImageOpNode.INPUT_IMG, src);
    identity.process();
    assertSame(src, identity.getParam(ImageOpNode.OUTPUT_IMG));

    BrightnessOp two = new BrightnessOp();
    two.setParam(BrightnessOp.P_CONTRAST, 2.0);
    two.setParam(BrightnessOp.P_BRIGHTNESS, 0.0);
    two.setParam(ImageOpNode.INPUT_IMG, src);
    two.process();
    assertEquals(80, grayAt(two));
  }

  @Test
  void autoLevelsStretchesMinMaxToFullRange() throws Exception {
    BufferedImage src = new BufferedImage(2, 1, BufferedImage.TYPE_BYTE_GRAY);
    src.getRaster().setSample(0, 0, 0, 64);
    src.getRaster().setSample(1, 0, 0, 192);
    AutoLevelsOp stretch = new AutoLevelsOp();
    stretch.setParam(ImageOpNode.INPUT_IMG, src);
    stretch.process();
    BufferedImage out = (BufferedImage) stretch.getParam(ImageOpNode.OUTPUT_IMG);
    assertEquals(0, out.getRaster().getSample(0, 0, 0));
    assertEquals(255, out.getRaster().getSample(1, 0, 0));

    BufferedImage flat = gray(80);
    AutoLevelsOp none = new AutoLevelsOp();
    none.setParam(ImageOpNode.INPUT_IMG, flat);
    none.process();
    assertSame(flat, none.getParam(ImageOpNode.OUTPUT_IMG));
  }

  @Test
  void maskZerosBlackenSourcePixels() throws Exception {
    BufferedImage src = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0xFF0000);
    src.setRGB(1, 0, 0x0000FF);
    MaskOp none = new MaskOp();
    none.setParam(ImageOpNode.INPUT_IMG, src);
    none.process();
    assertSame(src, none.getParam(ImageOpNode.OUTPUT_IMG));

    BufferedImage mask = new BufferedImage(2, 1, BufferedImage.TYPE_BYTE_GRAY);
    mask.getRaster().setSample(0, 0, 0, 255);
    mask.getRaster().setSample(1, 0, 0, 0);
    MaskOp op = new MaskOp();
    op.setParam(MaskOp.P_MASK, mask);
    op.setParam(ImageOpNode.INPUT_IMG, src);
    op.process();
    BufferedImage out = (BufferedImage) op.getParam(ImageOpNode.OUTPUT_IMG);
    assertEquals(0xFFFF0000, out.getRGB(0, 0));
    assertEquals(0xFF000000, out.getRGB(1, 0));
    assertNotSame(src, out);
  }

  @Test
  void mergeOverlayDrawsOnTopWithoutSharingRaster() throws Exception {
    BufferedImage src = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0x000000);
    src.setRGB(1, 0, 0x0000FF);
    MergeImgOp none = new MergeImgOp();
    none.setParam(ImageOpNode.INPUT_IMG, src);
    none.process();
    assertSame(src, none.getParam(ImageOpNode.OUTPUT_IMG));

    BufferedImage overlay = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    overlay.setRGB(0, 0, 0xFF0000);
    MergeImgOp op = new MergeImgOp();
    op.setParam(MergeImgOp.P_OVERLAY, overlay);
    op.setParam(ImageOpNode.INPUT_IMG, src);
    op.process();
    BufferedImage out = (BufferedImage) op.getParam(ImageOpNode.OUTPUT_IMG);
    assertEquals(0xFFFF0000, out.getRGB(0, 0));
    assertEquals(0xFF0000FF, out.getRGB(1, 0));
    assertNotSame(src, out);
    out.setRGB(0, 0, 0);
    assertEquals(0xFF000000, src.getRGB(0, 0));
  }

  static BufferedImage gray(int sample) {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
    image.getRaster().setSample(0, 0, 0, sample);
    return image;
  }

  static int grayAt(AbstractOp op) {
    BufferedImage image = (BufferedImage) op.getParam(ImageOpNode.OUTPUT_IMG);
    return ((DataBufferByte) image.getRaster().getDataBuffer()).getData()[0] & 0xFF;
  }
}
