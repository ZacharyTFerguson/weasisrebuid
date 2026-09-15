/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.base.viewer2d.dockable.DisplayTool;
import org.weasis.base.viewer2d.dockable.ImageTool;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.model.layer.AbstractInfoLayer.Visibility;

class InfoLayerHaveTest {

  @Test
  void stillsOverlayAndDisplayToolCycle() {
    View2d view = new View2d();
    view.setSourceImage(new BufferedImage(8, 4, BufferedImage.TYPE_INT_RGB));
    InfoLayer layer = view.getInfoLayer();
    assertEquals(Visibility.FULL, layer.getVisibility());
    assertTrue(layer.overlayText(view).contains("8x4"));
    assertTrue(layer.overlayText(view).contains("zoom="));
    DisplayTool display = new DisplayTool();
    display.bind(layer);
    display.cycle();
    assertEquals(Visibility.MINIMAL, layer.getVisibility());
    assertEquals("8x4", layer.overlayText(view));
    display.cycle();
    assertEquals("", layer.overlayText(view));
    ImageTool image = new ImageTool();
    image.bind(view);
    assertTrue(image.summaryText().contains("8x4"));
    assertEquals(Insertable.Type.TOOL, display.getType());
    assertTrue(view.getEventManager() instanceof EventManager);
  }
}
