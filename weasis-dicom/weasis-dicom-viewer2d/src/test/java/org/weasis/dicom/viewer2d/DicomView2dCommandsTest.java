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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.MouseActions;
import org.weasis.core.ui.editor.image.SynchView;

class DicomView2dCommandsTest {

  @Test
  void zoomSetAndMagicValues() {
    View2d view = new View2d();
    DicomView2dCommands cmd = new DicomView2dCommands(view);
    cmd.zoom("set", "-200.0");
    assertEquals(-200.0, view.getZoom());
    cmd.zoom("-s", "--set=-100.0");
    assertEquals(-100.0, view.getZoom());
  }

  @Test
  void wlAndMoveRequireDashDashForNegatives() {
    View2d view = new View2d();
    view.setWindowLevel(400, 40);
    DicomView2dCommands cmd = new DicomView2dCommands(view);
    assertThrows(IllegalArgumentException.class, () -> cmd.wl("-10", "20"));
    cmd.wl("--", "-10", "20");
    assertEquals(-10.0, view.getWindow());
    assertEquals(20.0, view.getLevel());
    assertThrows(IllegalArgumentException.class, () -> cmd.move("-5", "3"));
    cmd.move("--", "-5", "3");
    assertEquals(-5.0, view.getPanX());
    assertEquals(3.0, view.getPanY());
  }

  @Test
  void mouseLeftActionDrawAlias() {
    View2d view = new View2d();
    DicomView2dCommands cmd = new DicomView2dCommands(view);
    assertEquals(MouseActions.DRAW, cmd.mouseLeftAction("drawings"));
    assertTrue(DicomView2dCommands.helpMouse().contains("draw"));
  }

  @Test
  void scrollResetSynch() {
    View2d view = new View2d();
    DicomView2dCommands cmd = new DicomView2dCommands(view);
    cmd.scroll("-s", "4");
    assertEquals(4, view.getFrameIndex());
    cmd.scroll("-i");
    assertEquals(5, view.getFrameIndex());
    cmd.scroll("-d");
    assertEquals(4, view.getFrameIndex());
    cmd.synch("Tile");
    assertEquals(SynchView.TILE, view.getSynch());
    cmd.reset("-a");
    assertEquals(-200.0, view.getZoom());
    assertEquals(0.0, view.getPanX());
  }

  @Test
  void layoutNandI() {
    View2dContainer container = new View2dContainer();
    DicomView2dCommands cmd = new DicomView2dCommands(container.getView2d());
    assertEquals("layout -n 2", cmd.layout("-n", "2"));
    assertEquals(2, container.getLayoutViews().size());
    cmd.layout("-i", "1");
    assertEquals(1, container.getLayoutIndex());
  }

  @Test
  void layoutNGluedTokenGrowsHost() {
    View2dContainer container = new View2dContainer();
    assertEquals("layout -n 4", new DicomView2dCommands(container.getView2d()).layout("-n4"));
    assertEquals(4, container.getLayoutCount());
  }
}
