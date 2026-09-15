/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.image.PseudoColorOp;
import org.weasis.core.api.service.UICore;
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

  static KeyEvent tab(View2d view, int mods) {
    return new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, mods, KeyEvent.VK_TAB, '\t');
  }
}
