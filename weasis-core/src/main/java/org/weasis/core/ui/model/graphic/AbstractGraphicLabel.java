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

public abstract class AbstractGraphicLabel implements GraphicLabel {

  private String[] labels = new String[0];
  private double offsetX;
  private double offsetY;

  @Override
  public String[] getLabels() {
    return labels.clone();
  }

  @Override
  public void setLabels(String[] labels) {
    this.labels = labels == null ? new String[0] : labels.clone();
  }

  @Override
  public double getOffsetX() {
    return offsetX;
  }

  @Override
  public double getOffsetY() {
    return offsetY;
  }

  public void setOffset(double x, double y) {
    this.offsetX = x;
    this.offsetY = y;
  }
}
