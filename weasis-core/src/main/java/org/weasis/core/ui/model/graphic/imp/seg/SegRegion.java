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

/** SEG overlay region (label, number, color, fill opacity). */
public class SegRegion {

  private String label = "";
  private int number = 1;
  private boolean visible = true;
  private Color color = Color.RED;
  private float opacity = 0.5f;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label == null ? "" : label;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color == null ? Color.RED : color;
  }

  public float getOpacity() {
    return opacity;
  }

  public void setOpacity(float opacity) {
    this.opacity = Math.max(0f, Math.min(1f, opacity));
  }
}
