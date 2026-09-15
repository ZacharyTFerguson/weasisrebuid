/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.util;

/** Integer slider used by brightness / contrast / orientation. */
public class AbstractSliderComponent {

  private final int min;
  private final int max;
  private int value;

  public AbstractSliderComponent() {
    this(0, 100, 0);
  }

  public AbstractSliderComponent(int min, int max, int value) {
    this.min = Math.min(min, max);
    this.max = Math.max(min, max);
    setValue(value);
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = Math.max(min, Math.min(max, value));
  }
}
