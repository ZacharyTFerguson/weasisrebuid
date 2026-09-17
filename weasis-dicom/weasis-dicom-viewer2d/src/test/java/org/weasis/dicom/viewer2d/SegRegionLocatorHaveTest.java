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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.imp.seg.SegGraphic;
import org.weasis.core.ui.model.graphic.imp.seg.SegRegion;
import org.weasis.dicom.codec.seg.MaskFrames;
import org.weasis.dicom.viewer2d.dockable.SegmentationTool;

class SegRegionLocatorHaveTest {

  @Test
  void locatesMaskLabelAndSkipsHiddenCatalogRegion() {
    byte[] plane = new byte[] {0, 3, 0, 0};
    MaskFrames frames = new MaskFrames(2, 2, new byte[][] {plane});
    SegRegionLocator locator = new SegRegionLocator();
    assertEquals(3, locator.labelAt(frames, 0, 1, 0));
    assertEquals(0, locator.labelAt(frames, 0, 0, 0));
    SegRegion found = locator.locate(frames, 0, 1, 0);
    assertNotNull(found);
    assertEquals(3, found.getNumber());

    SegRegion liver = new SegRegion();
    liver.setNumber(3);
    liver.setLabel("liver");
    assertSame(liver, locator.locate(frames, 0, 1, 0, List.of(liver)));
    liver.setVisible(false);
    assertNull(locator.locate(frames, 0, 1, 0, List.of(liver)));
  }

  @Test
  void locatesOverlayGraphicOnViewCanvas() {
    byte[] plane = new byte[] {0, 1, 1, 0, 0, 0};
    MaskFrames frames = new MaskFrames(2, 3, new byte[][] {plane});
    View2d view = new View2d();
    List<SegGraphic> graphics = new SegComponentFactory().applyTo(view, frames, 0);
    assertEquals(1, graphics.size());
    SegRegionLocator locator = new SegRegionLocator();
    SegGraphic hit = locator.locate(view, 1.5, 0.5);
    assertNotNull(hit);
    assertEquals(1, hit.getContour().getRegion().getNumber());
    view.setSegmentationsVisible(false);
    assertNull(locator.locate(view, 1.5, 0.5));
  }

  @Test
  void segmentationToolLocateUsesCatalogAndOverlayFlag() {
    byte[] plane = new byte[] {0, 4, 0, 0};
    MaskFrames frames = new MaskFrames(2, 2, new byte[][] {plane});
    SegmentationTool tool = new SegmentationTool();
    SegRegion region = new SegRegion();
    region.setNumber(4);
    region.setLabel("kidney");
    tool.addRegion(region);
    assertEquals("kidney", tool.locate(frames, 0, 1, 0).getLabel());
    tool.setOverlayVisible(false);
    assertNull(tool.locate(frames, 0, 1, 0));
  }
}
