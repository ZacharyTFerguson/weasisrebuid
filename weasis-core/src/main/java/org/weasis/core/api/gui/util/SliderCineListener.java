/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui.util;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SliderCineListener extends SliderChangeListener {
  public static final double DEFAULT_SPEED = 15.0;

  private final AtomicBoolean running = new AtomicBoolean();
  private volatile double speed = DEFAULT_SPEED;
  private volatile boolean loop = true;

  protected SliderCineListener(Feature<?> action, int min, int max, int value) {
    super(action, min, max, value);
  }

  public void start() {
    running.set(true);
  }

  public void stop() {
    running.set(false);
  }

  public boolean isCineRunning() {
    return running.get();
  }

  public double getSpeed() {
    return speed;
  }

  public void setSpeed(double speed) {
    this.speed = speed <= 0 ? DEFAULT_SPEED : speed;
  }

  public boolean isLoop() {
    return loop;
  }

  public void setLoop(boolean loop) {
    this.loop = loop;
  }

  public int millisPerFrame() {
    return (int) Math.max(1, Math.round(1000.0 / speed));
  }

  /**
   * Advance one cine frame. Have tests call this directly so playback does not depend on a Swing
   * timer.
   */
  public void tick() {
    if (!isCineRunning()) {
      return;
    }
    int min = getSlider().getMinimum();
    int max = getSlider().getMaximum();
    int next = getSliderValue() + 1;
    if (next > max) {
      if (loop) {
        next = min;
      } else {
        stop();
        return;
      }
    }
    setSliderValue(next);
  }
}
