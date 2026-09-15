/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.measure.MeasurementsAdapter;
import org.weasis.core.api.image.util.Unit;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

class MeasurementHaveTest {

  @Test
  void lineLengthUsesCalibration() {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(3, 4));
    assertEquals(5.0, line.getLength(), 1e-9);
    List<MeasureItem> items =
        line.computeMeasurements(new MeasurementsAdapter(2.0, Unit.MILLIMETER));
    assertEquals(1, items.size());
    MeasureItem item = items.getFirst();
    assertEquals(LineGraphic.LENGTH, item.getMeasurement());
    assertTrue(item.getMeasurement().isComputed());
    assertEquals(10.0, item.getValue().doubleValue(), 1e-9);
    assertEquals("mm", item.getUnit());
    List<MeasureItem> pixels = line.computeMeasurements(null);
    assertEquals(5.0, pixels.getFirst().getValue().doubleValue(), 1e-9);
    assertEquals("px", pixels.getFirst().getUnit());
  }
}
