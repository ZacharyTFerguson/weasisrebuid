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

/** Image-space point recorded by pan / navigator actions. */
public class PanPoint {

  public enum State {
    CENTER,
    MOVE,
    DRAG
  }

  private final State state;
  private double x;
  private double y;

  public PanPoint(State state) {
    this(state, 0, 0);
  }

  public PanPoint(State state, double x, double y) {
    this.state = state == null ? State.MOVE : state;
    this.x = x;
    this.y = y;
  }

  public State getState() {
    return state;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public void setLocation(double x, double y) {
    this.x = x;
    this.y = y;
  }
}
