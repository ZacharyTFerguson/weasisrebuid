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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.model.layer.AbstractInfoLayer.Visibility;
import org.weasis.core.ui.model.layer.LayerAnnotation;
import org.weasis.core.ui.model.layer.LayerType;
import org.weasis.dicom.viewer2d.dockable.DisplayTool;
import org.weasis.dicom.viewer2d.dockable.ImageTool;

class InfoLayerHaveTest {

  @Test
  void overlayTextFollowsFullMinimalHidden() {
    InfoLayer layer = new InfoLayer();
    String full = layer.overlayText("TEST^A", "CT", 400, 40);
    assertTrue(full.contains("TEST^A"));
    assertTrue(full.contains("CT"));
    assertTrue(full.contains("W:400"));
    layer.cycle();
    String min = layer.overlayText("TEST^A", "CT", 400, 40);
    assertEquals("W:400 L:40", min);
    assertFalse(min.contains("TEST^A"));
    layer.cycle();
    assertEquals("", layer.overlayText("TEST^A", "CT", 400, 40));
  }

  @Test
  void displayToolAppliesVisibilityAndImageToolSummarizes() {
    View2d view = new View2d();
    assertTrue(view.getInfoLayer().overlayText(view).contains("W:" + (int) view.getWindow()));
    DisplayTool display = new DisplayTool();
    assertEquals(Insertable.Type.TOOL, display.getType());
    display.bind(view.getInfoLayer());
    display.cycle();
    assertEquals(Visibility.MINIMAL, view.getInfoLayer().getVisibility());
    display.cycle();
    assertEquals(Visibility.HIDDEN, view.getInfoLayer().getVisibility());
    ImageTool image = new ImageTool();
    image.bind(view);
    assertTrue(image.summaryText().contains("W:400"));
    assertEquals(Insertable.Type.TOOL, image.getType());
  }

  @Test
  void layerAnnotationCanHidePatientAndDisplayTogglesCrosslines() {
    InfoLayer layer = new InfoLayer();
    layer.getLayerAnnotation().setItemVisible(LayerAnnotation.PATIENT, false);
    String full = layer.overlayText("TEST^A", "CT", 400, 40);
    assertFalse(full.contains("TEST^A"));
    assertTrue(full.contains("W:400"));
    View2d view = new View2d();
    DisplayTool display = new DisplayTool();
    display.bind(view);
    assertEquals(5, display.layerItems().size());
    display.setLayerVisible(LayerType.CROSSLINES, false);
    assertFalse(view.isLayerVisible(LayerType.CROSSLINES));
  }
}
