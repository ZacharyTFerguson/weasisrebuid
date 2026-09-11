/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class EcgHaveTest {

  @Test
  void layoutsAndCaliper() {
    EcgView v = new EcgView();
    assertEquals(EcgView.Layout.L12x1, v.displayLayout());
    v.setDisplayLayout(EcgView.Layout.L3x4_RHYTHM);
    v.click(false, false, 0.2);
    v.click(true, false, 1.2);
    assertEquals(1.0, v.duration(), 1e-9);
    v.click(false, false, 0.5);
    assertNull(v.end());
    v.click(false, true, 0);
    assertNull(v.start());
    assertEquals(EcgView.Scale.AUTO, v.timeScale());
    assertEquals(EcgView.Scale.AUTO, v.voltageScale());
  }
}
