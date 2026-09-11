/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.imp.area.SelectGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

class MeasurementTableTest {

  @Test
  void copyUsesFullPrecisionAndSkipsSelect() {
    MeasurementTable table = new MeasurementTable();
    Map<String, String> row = new LinkedHashMap<>();
    row.put("length[px]", Double.toString(1.23456789));
    table.addRow(row);
    String copy = table.copy();
    assertTrue(copy.contains("1.23456789"));
    assertFalse(copy.contains("1.23 "));
    table.addMeasurement(new SelectGraphic(), "px");
    assertEquals(1, table.rows().size());
    LineGraphic line = new LineGraphic();
    table.addMeasurement(line, null);
    assertEquals("px", MeasurementTable.unitFor(null));
    assertEquals("mm", MeasurementTable.unitFor(0.2));
  }
}
