/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.seg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** SEG region (ARCHITECTURE §5.2). Overlay attaches to an image series; not its own tab. */
public final class SegRegion {

  private String label = "";
  private Color color = Color.RED;
  private final List<SegContour> contours = new ArrayList<>();
  private ByteLutAlpha lut = ByteLutAlpha.identity();

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label == null ? "" : label;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color == null ? Color.RED : color;
  }

  public List<SegContour> getContours() {
    return Collections.unmodifiableList(contours);
  }

  public void addContour(SegContour contour) {
    if (contour != null) {
      contours.add(contour);
    }
  }

  public ByteLutAlpha getLut() {
    return lut;
  }

  public void setLut(ByteLutAlpha lut) {
    this.lut = lut == null ? ByteLutAlpha.identity() : lut;
  }
}
