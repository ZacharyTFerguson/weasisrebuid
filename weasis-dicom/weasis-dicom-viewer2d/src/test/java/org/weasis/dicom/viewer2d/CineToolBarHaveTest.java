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
import org.weasis.core.api.gui.util.SliderCineListener;
import org.weasis.core.ui.editor.image.SynchData;
import org.weasis.core.ui.editor.image.SynchView;

class CineToolBarHaveTest {

  @Test
  void playTicksAdvanceSelectedViewAndWrap() {
    View2d view = new View2d();
    view.setFrameCount(4);
    CineToolBar bar = new CineToolBar();
    bar.setClockEnabled(false);
    bar.bind(view);
    assertEquals("Cine", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    bar.play();
    assertTrue(bar.isPlaying());
    assertTrue(bar.cine().isCineRunning());
    bar.tick();
    assertEquals(1, view.getFrameIndex());
    bar.tick();
    assertEquals(2, view.getFrameIndex());
    bar.tick();
    assertEquals(3, view.getFrameIndex());
    bar.tick();
    assertEquals(0, view.getFrameIndex());
    bar.stop();
    assertFalse(bar.isPlaying());
    bar.tick();
    assertEquals(0, view.getFrameIndex());
  }

  @Test
  void cinePropagatesManualSynchPeer() {
    DicomSynchManager mgr = new DicomSynchManager();
    View2d a = new View2d();
    View2d b = new View2d();
    a.setFrameCount(3);
    b.setFrameCount(3);
    a.getSynchData().setKind(SynchData.Kind.MANUAL);
    b.getSynchData().setKind(SynchData.Kind.MANUAL);
    a.setSynch(SynchView.STACK);
    b.setSynch(SynchView.STACK);
    a.setSynchManager(mgr);
    b.setSynchManager(mgr);
    mgr.add(a);
    mgr.add(b);
    CineToolBar bar = new CineToolBar();
    bar.setClockEnabled(false);
    bar.bind(a);
    bar.play();
    bar.tick();
    assertEquals(1, a.getFrameIndex());
    assertEquals(1, b.getFrameIndex());
    assertEquals(SliderCineListener.DEFAULT_SPEED, bar.cine().getSpeed());
  }
}
