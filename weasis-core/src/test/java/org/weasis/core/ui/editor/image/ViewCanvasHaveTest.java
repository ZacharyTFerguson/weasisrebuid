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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class ViewCanvasHaveTest {

  @Test
  void defaultView2dIsViewCanvasWithAffineAndSegVisibility() {
    DefaultView2d<?> view = new DefaultView2d<>();
    assertInstanceOf(Canvas.class, view);
    assertInstanceOf(ViewCanvas.class, view);
    BufferedImage src = new BufferedImage(4, 2, BufferedImage.TYPE_BYTE_GRAY);
    view.setSourceImage(src);
    view.setSize(8, 4);
    view.setZoom(1.0);
    view.setPan(3, -1);
    AffineTransform tx = view.getAffineTransform();
    assertEquals(1.0, tx.getScaleX(), 1e-9);
    assertEquals(3.0, view.getPanX(), 1e-9);
    assertEquals(src, view.getSourceImage());
    assertTrue(view.isSegmentationsVisible());
    view.setSegmentationsVisible(false);
    assertFalse(view.isSegmentationsVisible());
    view.toggleSegmentations();
    assertTrue(view.isSegmentationsVisible());
  }
}
