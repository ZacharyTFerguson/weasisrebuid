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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.AbstractButton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.image.AffineTransformOp;
import org.weasis.core.api.image.ImageOpNode;

class ZoomScreenshotHaveTest {

  @Test
  void zoomToolbarMagicValues() {
    ZoomToolBar bar = new ZoomToolBar();
    assertEquals(AffineTransformOp.ZOOM_BEST_FIT, bar.selectedZoom());
    DefaultView2d<?> view = new DefaultView2d<>();
    bar.setSelectedZoom(AffineTransformOp.ZOOM_REAL_SIZE);
    bar.apply(view);
    assertEquals(AffineTransformOp.ZOOM_REAL_SIZE, view.getZoom());
    assertTrue(ZoomToolBar.isRealSize(view.getZoom()));
    bar.setSelectedZoom(AffineTransformOp.ZOOM_BEST_FIT);
    bar.apply(view);
    assertTrue(ZoomToolBar.isBestFit(view.getZoom()));
    PopUpMenuOnZoom menu = new PopUpMenuOnZoom();
    assertEquals(AffineTransformOp.ZOOM_BEST_FIT, menu.selectedZoom());
  }

  @Test
  void lensDoublesResolvedScale() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(new BufferedImage(64, 32, BufferedImage.TYPE_INT_RGB));
    view.setZoom(1.0);
    ZoomWin lens = new ZoomWin();
    assertEquals(2.0, lens.getFactor());
    assertEquals(2.0, lens.magnifiedScale(view, 64, 32));
    lens.setFactor(4.0);
    lens.setOrigin(10, 12);
    assertEquals(4.0, lens.magnifiedScale(view, 64, 32));
    assertEquals(10.0, lens.originX());
    assertEquals("lens", lens.getName());
    assertEquals("lens-factor", lens.getFactorSlider().getName());
    assertEquals("lens-factor-value", lens.factorValueLabel().getName());
    assertEquals("lens-panel", lens.previewPanel().getName());
    assertEquals("4x", lens.factorValueText());
    lens.getFactorSlider().setValue(200);
    assertEquals(2.0, lens.getFactor(), 1e-9);
    assertEquals("2x", lens.factorValueText());
  }

  @Test
  void screenshotWritesPngWithoutOverlays(@TempDir Path dir) throws Exception {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(new BufferedImage(16, 8, BufferedImage.TYPE_INT_RGB));
    view.setLossyLabel("LOSSY");
    ScreenshotDialog dialog = new ScreenshotDialog();
    dialog.setFormat(ScreenshotDialog.Format.PNG);
    dialog.setScope(ScreenshotDialog.Scope.NATIVE_PIXELS);
    dialog.setIncludeOverlays(false);
    Path file = dir.resolve("view.png");
    dialog.write(view, file);
    assertTrue(Files.size(file) > 0);
    BufferedImage read = javax.imageio.ImageIO.read(file.toFile());
    assertEquals(16, read.getWidth());
    assertEquals(8, read.getHeight());
    assertEquals("png", dialog.imageIoFormat());
    dialog.setVisible(true);
    ScreenshotToolBar bar = new ScreenshotToolBar();
    assertEquals("Screenshot", bar.getComponentName());
    assertEquals("screenshot", bar.button().getName());
    bar.bind(view);
    assertSame(view, bar.boundView());
    bar.dialog().ensureWindow();
    assertEquals("screenshot-dialog", bar.dialog().ensureWindow().getName());
    bar.dialog().setPath(dir.resolve("chrome.png").toString());
    bar.dialog().save();
    assertTrue(bar.dialog().statusText().startsWith("Saved "));
    assertTrue(Files.size(dir.resolve("chrome.png")) > 0);
  }

  @Test
  void rotationToolbarAppliesQuarterTurns() {
    RotationToolBar bar = new RotationToolBar();
    assertEquals("Rotation", bar.getComponentName());
    DefaultView2d<?> view = new DefaultView2d<>();
    bar.setSelectedRotation(90);
    bar.apply(view);
    assertEquals(90.0, view.getRotation());
    bar.setSelectedRotation(0);
    bar.apply(view);
    assertEquals(0.0, view.getRotation());
  }

  @Test
  void namedClicksApplyAffineParamsWithoutRasterizing() throws Exception {
    DefaultView2d<?> view = new DefaultView2d<>();
    ZoomToolBar zoom = new ZoomToolBar();
    zoom.bind(view);
    AbstractButton twoX = (AbstractButton) zoom.getComponent(3);
    assertEquals("2x", twoX.getName());
    twoX.doClick();
    assertEquals(2.0, view.getZoom(), 1e-9);
    assertEquals(
        2.0, view.getDisplayOpManager().getParamValue("op.affine", AffineTransformOp.P_ZOOM));

    RotationToolBar rotation = new RotationToolBar();
    rotation.bind(view);
    AbstractButton ninety = (AbstractButton) rotation.getComponent(1);
    assertEquals("90°", ninety.getName());
    ninety.doClick();
    assertEquals(90.0, view.getRotation(), 1e-9);
    assertEquals(
        90.0, view.getDisplayOpManager().getParamValue("op.affine", AffineTransformOp.P_ROTATION));

    AffineTransformOp affine = new AffineTransformOp();
    BufferedImage src = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
    affine.setParam(ImageOpNode.INPUT_IMG, src);
    affine.process();
    assertSame(src, affine.getParam(ImageOpNode.OUTPUT_IMG));
  }
}
