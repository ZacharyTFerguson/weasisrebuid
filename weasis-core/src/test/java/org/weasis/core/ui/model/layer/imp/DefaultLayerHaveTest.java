/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.layer.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.util.Unit;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.model.layer.GraphicLayer;
import org.weasis.core.ui.model.layer.LayerType;

class DefaultLayerHaveTest {

  @Test
  void viewImageLayerReceivesRasterAndMeasureAdapter() {
    DefaultView2d<?> view = new DefaultView2d<>();
    assertInstanceOf(RenderedImageLayer.class, view.getLayer(LayerType.IMAGE));
    assertInstanceOf(GraphicLayer.class, view.getLayer(LayerType.MEASURE));
    assertInstanceOf(DefaultLayer.class, view.getLayer(LayerType.CROSSLINES));
    RenderedImageLayer image = view.getImageLayer();
    assertFalse(image.hasContent());
    AtomicInteger fires = new AtomicInteger();
    image.addLayerChangeListener(fires::incrementAndGet);
    BufferedImage raster = new BufferedImage(4, 4, BufferedImage.TYPE_BYTE_GRAY);
    view.setSourceImage(raster);
    assertSame(raster, image.getImage());
    assertTrue(image.hasContent());
    assertEquals(1, fires.get());
    assertEquals(Unit.MILLIMETER, image.getMeasurementAdapter(Unit.MILLIMETER).getUnit());
    DefaultLayer draw = new DefaultLayer(LayerType.DRAW);
    draw.setLocked(true);
    draw.setUuid("layer-draw");
    assertTrue(draw.isLocked());
    assertEquals("layer-draw", draw.getUuid());
  }
}
