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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ViewerHandlerHaveTest {

  @Test
  void sequenceScrollsFrameAndFocusSelectsView() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setFrameIndex(2);
    assertEquals(3, view.getSequenceHandler().apply(view, 5));
    assertEquals(2, view.getSequenceHandler().apply(view, -3));
    assertEquals(0, view.getSequenceHandler().step(0));
    DefaultView2d<?> other = new DefaultView2d<>();
    FocusHandler focus = new FocusHandler();
    focus.focus(view);
    assertTrue(focus.isFocused(view));
    focus.focus(other);
    assertFalse(focus.isFocused(view));
    assertSame(other, focus.focused());
    view.selectInFocus();
    assertTrue(view.getFocusHandler().isFocused(view));
  }

  @Test
  void gridCellAtTwoByTwoAndOneByTwo() {
    GridMouseHandler grid = new GridMouseHandler();
    assertEquals(1, grid.cellAt(200, 200, 2, 2, 150, 50));
    assertEquals(2, grid.cellAt(200, 200, 2, 2, 50, 150));
    assertEquals(3, grid.cellAt(200, 200, 2, 2, 150, 150));
    assertEquals(1, grid.cellAt(200, 100, 1, 2, 150, 10));
    assertEquals(-1, grid.cellAt(0, 200, 2, 2, 10, 10));
  }

  @Test
  void showPopupBuildsViewerMenuAndZoomFires() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.getShowPopup().show(view, 4, 4);
    assertNotNull(view.getContextMenuHandler().lastMenu());
    assertTrue(
        view.getContextMenuHandler().lastMenu().getComponentCount() > ViewerToolBar.ACTIONS.length);
    view.setZoom(2.0);
    assertTrue(view.getPropertyChangeHandler().saw("zoom"));
    assertEquals(2.0, (Double) view.getPropertyChangeHandler().last().getNewValue(), 1e-9);
  }
}
