/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image.util;

/** Window / level pair used by {@code WindowOp}. */
public class WindLevelParameters {

  private double window;
  private double level;

  public WindLevelParameters(double window, double level) {
    this.window = window;
    this.level = level;
  }

  public double getWindow() {
    return window;
  }

  public void setWindow(double window) {
    this.window = window;
  }

  public double getLevel() {
    return level;
  }

  public void setLevel(double level) {
    this.level = level;
  }

  public double getLower() {
    return level - window / 2.0;
  }

  public double getUpper() {
    return level + window / 2.0;
  }
}
