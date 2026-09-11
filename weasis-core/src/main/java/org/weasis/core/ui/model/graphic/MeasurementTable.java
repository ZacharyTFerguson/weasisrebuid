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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Selected Measurement table D. Copy/paste uses full precision, not on-screen rounding. Units in
 * square brackets. Decorative graphics are omitted.
 */
public final class MeasurementTable {

  private final List<Map<String, String>> rows = new ArrayList<>();

  public void addMeasurement(Graphic graphic, String unit) {
    if (graphic == null) {
      return;
    }
    GraphicKind kind = GraphicKind.of(graphic);
    if (kind != null && !kind.measurement()) {
      return;
    }
    Map<String, String> row = new LinkedHashMap<>();
    row.put("type", kind == null ? graphic.getClass().getSimpleName() : kind.xmlName());
    String u = unit == null || unit.isBlank() ? "px" : unit;
    row.put("unit", u);
    if (graphic instanceof LineGraphicAdapter line) {
      row.put("length[" + u + "]", Double.toString(line.length()));
    }
    rows.add(row);
  }

  public void addRow(Map<String, String> row) {
    if (row != null) {
      rows.add(new LinkedHashMap<>(row));
    }
  }

  public List<Map<String, String>> rows() {
    return Collections.unmodifiableList(rows);
  }

  /** Full precision, tab-separated. */
  public String copy() {
    StringJoiner out = new StringJoiner("\n");
    for (Map<String, String> row : rows) {
      StringJoiner line = new StringJoiner("\t");
      for (Map.Entry<String, String> e : row.entrySet()) {
        line.add(e.getKey() + "=" + e.getValue());
      }
      out.add(line.toString());
    }
    return out.toString();
  }

  /** Spatial unit follows image calibration; uncalibrated → pixels. */
  public static String unitFor(Double mmPerPixel) {
    if (mmPerPixel == null || mmPerPixel <= 0) {
      return "px";
    }
    return "mm";
  }

  @FunctionalInterface
  public interface LineGraphicAdapter {
    double length();
  }
}
