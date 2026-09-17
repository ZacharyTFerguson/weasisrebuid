/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReferencedSeriesHaveTest {

  @Test
  void modelReferencesSopFromSeriesImages() {
    ReferencedImage whole = new ReferencedImage("2.25.i1");
    ReferencedImage frames = new ReferencedImage("2.25.i2", List.of(1, 3));
    ReferencedSeries series = new ReferencedSeries("2.25.s1", List.of(whole, frames));
    AbstractGraphicModel model = new AbstractGraphicModel();
    model.addReferencedSeries(series);
    assertEquals(1, model.getReferencedSeries().size());
    assertTrue(model.referencesSop("2.25.i1"));
    assertTrue(model.referencesSop("2.25.i2"));
    assertFalse(model.referencesSop("2.25.missing"));
    assertTrue(whole.matches("2.25.i1", 9));
    assertTrue(frames.matches("2.25.i2", 3));
    assertFalse(frames.matches("2.25.i2", 2));
    assertFalse(frames.matches("2.25.i1", 1));
  }
}
