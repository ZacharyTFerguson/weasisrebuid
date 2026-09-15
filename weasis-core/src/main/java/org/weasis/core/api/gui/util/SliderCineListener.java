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
  private final AtomicBoolean running = new AtomicBoolean();

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
}
