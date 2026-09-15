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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.image.PseudoColorOp;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.dicom.viewer2d.mpr.MprContainer;

class ViewerChromeHaveTest {

  @Test
  void resetToolsApplyCommandTokens() {
    View2d view = new View2d();
    view.setZoom(2.0);
    view.setPan(4, 5);
    view.setRotation(90);
    view.setFlip(true);
    ResetTools bar = new ResetTools();
    assertEquals("Reset", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    bar.bind(view);
    bar.apply(org.weasis.core.ui.editor.image.ResetTools.ZOOM);
    assertEquals(-200.0, view.getZoom(), 1e-9);
    assertEquals(4.0, view.getPanX(), 1e-9);
    bar.apply(org.weasis.core.ui.editor.image.ResetTools.ALL);
    assertEquals(0.0, view.getPanX(), 1e-9);
    assertEquals(0.0, view.getRotation(), 1e-9);
    assertFalse(view.isFlip());
  }

  @Test
  void lutToolBarSetsPseudoColorAndInvert() {
    View2d view = new View2d();
    LutToolBar bar = new LutToolBar();
    assertEquals("LUT", bar.getComponentName());
    bar.bind(view);
    assertEquals(PseudoColorOp.GRAY, view.getLut());
    bar.setLut("HotIron");
    assertEquals("HotIron", view.getLut());
    bar.toggleInvert();
    assertTrue(view.isInverseLut());
    bar.toggleInvert();
    assertFalse(view.isInverseLut());
  }

  @Test
  void basic3DToolBarOpensMprContainer() {
    UICore core = new UICore();
    Basic3DToolBar bar = new Basic3DToolBar();
    assertEquals("Basic 3D", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    MprContainer opened = bar.openMpr(core);
    assertInstanceOf(MprContainer.class, opened);
    assertSame(opened, core.getSelectedViewerPlugin());
  }

  @Test
  void view2dContainerExposesViewerLutResetAndCineToolbars() {
    View2dContainer container = new View2dContainer();
    assertTrue(container.getToolBars().getComponentCount() >= 8);
    assertEquals("Viewer", container.getViewerToolBar().getComponentName());
    assertEquals("LUT", container.getLutToolBar().getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, container.getViewerToolBar().getType());
    assertSame(container.getView2d(), container.getViewerToolBar().boundView());
    assertSame(container.getView2d(), container.getLutToolBar().boundView());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> "LUT".equals(b.getComponentName())));
  }

  @Test
  void tabCyclesLayoutViewsWhenMoreThanOne() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(3);
    View2d first = container.getView2d();
    first.getEventManager().keyPressed(tab(first, 0));
    assertEquals(1, container.getLayoutIndex());
    first.getEventManager().keyPressed(tab(first, InputEvent.SHIFT_DOWN_MASK));
    assertEquals(0, container.getLayoutIndex());
    first.getEventManager().keyPressed(tab(first, InputEvent.CTRL_DOWN_MASK));
    assertEquals(0, container.getLayoutIndex());
  }

  @Test
  void viewMenuResetClearsZoomPanRotation() {
    View2dContainer container = new View2dContainer();
    container.getView2d().setZoom(2.0);
    container.getView2d().setPan(4, 5);
    container.getView2d().setRotation(90);
    container.setLayoutCount(2);
    assertEquals(2, container.getLayoutCount());
    container.resetDisplay();
    assertEquals(0.0, container.getView2d().getPanX(), 1e-9);
    assertEquals(0.0, container.getView2d().getRotation(), 1e-9);
  }

  @Test
  void oneByTwoAndTwoByTwoSplitTheSelectedPlugin() {
    View2dContainer container = new View2dContainer();
    assertEquals(1, container.getViewGrid().getComponentCount());
    container.setLayoutCount(2);
    assertEquals(2, container.getViewGrid().getComponentCount());
    GridLayout oneByTwo = (GridLayout) container.getViewGrid().getLayout();
    assertEquals(1, oneByTwo.getRows());
    assertEquals(2, oneByTwo.getColumns());
    assertSame(container.getLayoutViews().get(0), container.getViewGrid().getComponent(0));
    assertSame(container.getLayoutViews().get(1), container.getViewGrid().getComponent(1));
    container.setLayoutCount(4);
    assertEquals(4, container.getViewGrid().getComponentCount());
    GridLayout twoByTwo = (GridLayout) container.getViewGrid().getLayout();
    assertEquals(2, twoByTwo.getRows());
    assertEquals(2, twoByTwo.getColumns());
    container.setLayoutCount(1);
    assertEquals(1, container.getViewGrid().getComponentCount());
  }

  @Test
  void editSelectAllSelectsDrawingsOnFocusedView() {
    View2dContainer container = new View2dContainer();
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(4, 0));
    container.getView2d().addGraphic(line);
    container.selectAllGraphics();
    assertTrue(Boolean.TRUE.equals(line.getSelected()));
    container.deselectAllGraphics();
    assertTrue(container.getView2d().getSelectedGraphics().isEmpty());
  }

  static KeyEvent tab(View2d view, int mods) {
    return new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, mods, KeyEvent.VK_TAB, '\t');
  }
}
