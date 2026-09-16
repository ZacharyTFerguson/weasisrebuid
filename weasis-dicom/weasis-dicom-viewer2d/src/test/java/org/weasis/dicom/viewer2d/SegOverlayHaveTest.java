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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import javax.swing.JCheckBox;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.editor.image.ViewCanvas;
import org.weasis.core.ui.model.graphic.imp.seg.SegGraphic;
import org.weasis.core.ui.model.graphic.imp.seg.SegRegion;
import org.weasis.dicom.codec.seg.MaskFrames;
import org.weasis.dicom.viewer2d.dockable.SegmentationTool;
import org.weasis.dicom.viewer2d.dockable.SegmentationToolFactory;
import org.weasis.dicom.viewer2d.pref.SegPrefFactory;
import org.weasis.dicom.viewer2d.pref.SegPrefView;

class SegOverlayHaveTest {

  @Test
  void segmentationToolBindsViewCanvasAndTogglesOverlay() {
    View2d view = new View2d();
    assertInstanceOf(ViewCanvas.class, view);
    SegmentationToolFactory factory = new SegmentationToolFactory();
    Insertable created = factory.createInstance(null);
    assertInstanceOf(SegmentationTool.class, created);
    assertEquals(Insertable.Type.TOOL, factory.getType());
    assertTrue(factory.isComponentCreatedByThisFactory(created));

    SegmentationTool tool = (SegmentationTool) created;
    tool.bind(view);
    SegRegion region = new SegRegion();
    region.setNumber(2);
    region.setLabel("liver");
    tool.addRegion(region);
    assertEquals(1, tool.visibleRegions().size());

    tool.setOverlayVisible(false);
    assertFalse(view.isSegmentationsVisible());
    assertFalse(view.getSegVisibility().isVisible());
    assertTrue(tool.visibleRegions().isEmpty());
    tool.setRegionVisible(region, false);
    tool.setOverlayVisible(true);
    assertTrue(view.isSegmentationsVisible());
    assertTrue(tool.visibleRegions().isEmpty());
  }

  @Test
  void segComponentFactoryAttachesMaskOverlaysWhenVisible() {
    byte[] plane = new byte[6];
    plane[1] = 1;
    plane[2] = 1;
    MaskFrames frames = new MaskFrames(2, 3, new byte[][] {plane});
    View2d view = new View2d();
    SegComponentFactory factory = new SegComponentFactory();
    List<SegGraphic> graphics = factory.applyTo(view, frames, 0);
    assertEquals(1, graphics.size());
    assertEquals(1, graphics.get(0).getContour().getRegion().getNumber());
    assertEquals(1, view.getGraphicList().size());

    view.setSegmentationsVisible(false);
    factory.clearOverlays(view);
    assertTrue(view.getGraphicList().isEmpty());
    assertTrue(factory.applyTo(view, frames, 0).isEmpty());
  }

  @Test
  void segPrefViewPersistsOpacityFillContourAndVisibility() {
    WProperties prefs = new WProperties();
    SegPrefFactory factory = new SegPrefFactory();
    assertInstanceOf(SegPrefView.class, factory.createInstance(null));
    assertEquals(Insertable.Type.PREFERENCES, factory.getType());

    SegPrefView page = new SegPrefView(prefs);
    page.setOpacityPercent(80);
    page.setFill(false);
    page.setContour(true);
    page.setOverlayVisible(false);
    page.closeAdditionalWindow();
    assertEquals(0.8f, prefs.getFloatProperty(SegPrefView.PREF_OPACITY, 0f), 1e-5);
    assertFalse(prefs.getBooleanProperty(SegPrefView.PREF_FILL, true));
    assertTrue(prefs.getBooleanProperty(SegPrefView.PREF_CONTOUR, false));
    assertFalse(prefs.getBooleanProperty(SegPrefView.PREF_VISIBLE, true));

    SegPrefView loaded = new SegPrefView(prefs);
    assertEquals(80, loaded.opacityPercent());
    assertFalse(loaded.fill());
    assertTrue(loaded.contour());
    assertFalse(loaded.overlayVisible());
    loaded.resetToDefaultValues();
    assertEquals(50, loaded.opacityPercent());
    assertTrue(loaded.fill());
  }

  @Test
  void view2dContainerWiresSegmentationShowOverlayChrome() {
    View2dContainer container = new View2dContainer();
    SegmentationTool tool = container.getSegmentationTool();
    assertEquals(container.getView2d(), tool.getView());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> SegmentationTool.NAME.equals(b.getComponentName())));
    JCheckBox overlay = (JCheckBox) tool.getComponent(0);
    assertEquals("Show overlay", overlay.getText());
    assertEquals("segOverlay", overlay.getName());
    assertTrue(overlay.isSelected());
    assertTrue(container.getView2d().isSegmentationsVisible());
    overlay.doClick();
    assertFalse(overlay.isSelected());
    assertFalse(container.getView2d().isSegmentationsVisible());
    overlay.doClick();
    assertTrue(container.getView2d().isSegmentationsVisible());
  }
}
