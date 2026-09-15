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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.image.AffineTransformOp;

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
    ScreenshotToolBar bar = new ScreenshotToolBar();
    assertEquals("Screenshot", bar.getComponentName());
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
}
