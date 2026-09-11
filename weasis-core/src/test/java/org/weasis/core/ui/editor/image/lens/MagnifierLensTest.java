/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image.lens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.Crosshair2d;

class MagnifierLensTest {

  @Test
  void freezeParametersIsNotFreezeImageAndWheelAndMatchParent() {
    MagnifierLens lens = new MagnifierLens();
    lens.setFreezeParameters(true);
    lens.setFreezeImage(false);
    assertNotEquals(lens.isFreezeParameters(), lens.isFreezeImage());
    double before = lens.getZoom();
    lens.wheel(-1);
    assertTrue(lens.getZoom() > before);
    lens.doubleClickMatchParent(1.0);
    assertEquals(1.0, lens.getZoom(), 1e-9);
    lens.resetFreeze();
    assertFalse(lens.isFreezeParameters());
    assertFalse(lens.isFreezeImage());
  }

  @Test
  void crosshairFollowsSameFoR() {
    Crosshair2d a = new Crosshair2d();
    Crosshair2d b = new Crosshair2d();
    a.setFrameOfReferenceUid("1.2.840");
    b.setFrameOfReferenceUid("1.2.840");
    a.addPeer(b);
    a.moveTo(10, 20);
    assertEquals(10, b.x(), 1e-9);
    assertEquals(20, b.y(), 1e-9);
    Crosshair2d other = new Crosshair2d();
    other.setFrameOfReferenceUid("9.9");
    a.addPeer(other);
    a.moveTo(1, 2);
    assertEquals(0, other.x(), 1e-9);
  }
}
