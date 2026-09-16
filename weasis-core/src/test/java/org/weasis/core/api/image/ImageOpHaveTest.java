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
import static org.junit.jupiter.api.Assertions.assertSame;

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
