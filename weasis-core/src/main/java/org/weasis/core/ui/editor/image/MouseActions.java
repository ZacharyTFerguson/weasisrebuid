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
 * Left / middle / right / wheel bindings. {@code draw} is the Gogo token; prefs may say {@code
 * drawings}.
 */
public final class MouseActions {

  public static final String WINLEVEL = "winLevel";
  public static final String PAN = "pan";
  public static final String ZOOM = "zoom";
  public static final String SCROLL = "scroll";
  public static final String DRAW = "draw";
  public static final String DRAWINGS = "drawings";

  /** Multi-vertex path measure; distinct from two-point {@link #DRAW} line caliper. */
  public static final String POLYLINE = "polyline";

  public static final String CROSSHAIR = "crosshair";

  private String left = WINLEVEL;
  private String middle = PAN;
  private String right = ZOOM;
  private String wheel = SCROLL;

  public String getLeft() {
    return left;
  }

  public void setLeft(String left) {
    this.left = normalize(left);
  }

  public String getMiddle() {
    return middle;
  }

  public void setMiddle(String middle) {
    this.middle = normalize(middle);
  }

  public String getRight() {
    return right;
  }

  public void setRight(String right) {
    this.right = normalize(right);
  }

  public String getWheel() {
    return wheel;
  }

  public void setWheel(String wheel) {
    this.wheel = normalize(wheel);
  }

  public static String normalize(String action) {
    if (action == null || action.isBlank()) {
      return WINLEVEL;
    }
    if (DRAWINGS.equalsIgnoreCase(action) || "drawing".equalsIgnoreCase(action)) {
      return DRAW;
    }
    return action;
  }
}
