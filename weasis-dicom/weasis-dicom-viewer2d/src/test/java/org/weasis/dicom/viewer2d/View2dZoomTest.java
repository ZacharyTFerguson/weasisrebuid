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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.AffineTransformOp;
import org.weasis.core.ui.editor.image.DefaultView2d;

class View2dZoomTest {

  @Test
  void magicZoomBestFitAndRealSize() {
    View2d view = new View2d();
    view.setSize(200, 100);
    view.setZoom(DefaultView2d.ZOOM_BEST_FIT);
    assertEquals(AffineTransformOp.ZOOM_BEST_FIT, view.getZoom());
    view.setZoom(DefaultView2d.ZOOM_REAL_SIZE);
    assertEquals(AffineTransformOp.ZOOM_REAL_SIZE, view.getZoom());
    assertEquals(1.0, view.resolvedScale(200, 100));
  }

  @Test
  void increaseDecreaseChangesScaleFromBestFit() {
    View2d view = new View2d();
    view.setSize(64, 64);
    view.setSourceImage(
        new java.awt.image.BufferedImage(32, 32, java.awt.image.BufferedImage.TYPE_BYTE_GRAY));
    view.setZoom(DefaultView2d.ZOOM_BEST_FIT);
    double fit = view.resolvedScale(64, 64);
    assertEquals(2.0, fit, 1e-9);
    view.increaseZoom(1);
    assertTrue(view.getZoom() > fit);
  }
}
