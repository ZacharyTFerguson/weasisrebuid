/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.net;

import java.util.concurrent.atomic.AtomicLong;

public class StallWatchdog {
  private final long timeoutMillis;
  private final AtomicLong last = new AtomicLong(System.currentTimeMillis());

  public StallWatchdog(long timeoutMillis) {
    this.timeoutMillis = Math.max(1, timeoutMillis);
  }

  public void tick() {
    last.set(System.currentTimeMillis());
  }

  public boolean stalled() {
    return System.currentTimeMillis() - last.get() > timeoutMillis;
  }
}
