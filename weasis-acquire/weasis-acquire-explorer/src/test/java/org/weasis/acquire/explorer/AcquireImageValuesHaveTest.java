/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Rectangle;
import org.junit.jupiter.api.Test;

class AcquireImageValuesHaveTest {

  @Test
  void snapsRotationToNinetyDegreeSteps() {
    AcquireImageValues values = new AcquireImageValues();
    values.setRotation(95);
    assertEquals(90, values.getRotation());
    values.setRotation(-90);
    assertEquals(270, values.getRotation());
    values.setRotation(360);
    assertEquals(0, values.getRotation());
  }

  @Test
  void copiesCropRectangle() {
    AcquireImageValues values = new AcquireImageValues();
    assertNull(values.getCrop());
    Rectangle src = new Rectangle(1, 2, 3, 4);
    values.setCrop(src);
    src.width = 99;
    assertEquals(new Rectangle(1, 2, 3, 4), values.getCrop());
  }

  @Test
  void clampsContrastAboveZero() {
    AcquireImageValues values = new AcquireImageValues();
    values.setContrast(0);
    assertEquals(0.01f, values.getContrast(), 1e-6f);
  }
}
