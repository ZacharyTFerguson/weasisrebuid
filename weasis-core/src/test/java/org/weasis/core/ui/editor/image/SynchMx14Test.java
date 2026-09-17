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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class SynchMx14Test {

  @Test
  void forAndManualAreDistinctKinds() {
    SynchData forData = new SynchData();
    forData.setKind(SynchData.Kind.FRAME_OF_REFERENCE);
    forData.setMode(SynchData.Mode.STACK);
    SynchData manual = new SynchData();
    manual.setKind(SynchData.Kind.MANUAL);
    manual.setMode(SynchData.Mode.STACK);
    assertNotEquals(forData.getKind(), manual.getKind());
    assertEquals(SynchData.Mode.STACK, forData.getMode());
    assertEquals(SynchData.Mode.STACK, manual.getMode());
  }

  @Test
  void buttonsSelectDifferentKinds() {
    DefaultView2d<?> view = new DefaultView2d<>();
    SynchViewButton forBtn = new SynchViewButton();
    ManualSynchViewButton manual = new ManualSynchViewButton();
    assertEquals("synch-for", forBtn.getName());
    assertEquals("synch-manual", manual.getName());
    forBtn.apply(view);
    assertEquals(SynchData.Kind.FRAME_OF_REFERENCE, view.getSynchData().getKind());
    manual.apply(view);
    assertEquals(SynchData.Kind.MANUAL, view.getSynchData().getKind());
  }
}
