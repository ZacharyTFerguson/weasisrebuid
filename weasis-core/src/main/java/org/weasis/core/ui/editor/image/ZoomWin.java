/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

/**
 * 2D lens / magnifier (WP-6). Factor multiplies the view's resolved scale; default 2× around the
 * pointer.
 */
public class ZoomWin {

  public static final double DEFAULT_FACTOR = 2.0;

  private double factor = DEFAULT_FACTOR;
  private double originX;
  private double originY;

  public double getFactor() {
    return factor;
  }

  public void setFactor(double factor) {
    this.factor = factor <= 0 ? DEFAULT_FACTOR : factor;
  }

  public void setOrigin(double x, double y) {
    this.originX = x;
    this.originY = y;
  }

  public double originX() {
    return originX;
  }

  public double originY() {
    return originY;
  }

  public double magnifiedScale(DefaultView2d<?> view, int viewW, int viewH) {
    double base = view == null ? 1.0 : view.resolvedScale(viewW, viewH);
    return base * factor;
  }
}
