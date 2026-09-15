/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** One ROI: name, number, display color, contours. */
public class StructRegion {

  private final int number;
  private final String name;
  private Color color;
  private final List<StructContour> contours = new ArrayList<>();
  private boolean visible = true;

  public StructRegion(int number, String name, Color color) {
    this.number = number;
    this.name = name == null || name.isBlank() ? "ROI " + number : name;
    this.color = color == null ? Color.RED : color;
  }

  public int number() {
    return number;
  }

  public String name() {
    return name;
  }

  public Color color() {
    return color;
  }

  public void setColor(Color color) {
    if (color != null) {
      this.color = color;
    }
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public void addContour(StructContour contour) {
    if (contour != null) {
      contours.add(contour);
    }
  }

  public List<StructContour> contours() {
    return Collections.unmodifiableList(contours);
  }
}
