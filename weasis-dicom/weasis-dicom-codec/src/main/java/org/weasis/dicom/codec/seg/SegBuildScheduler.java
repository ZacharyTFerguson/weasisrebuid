/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.seg;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MX-13: process-wide singleton. Peak concurrency never exceeds {@code getMaxConcurrentBuilds()}.
 * One-sided assert because Surefire {@code parallel=all} shares the JVM.
 */
public final class SegBuildScheduler {

  public static final int CANONICAL_CAP = 2;

  private static final SegBuildScheduler INSTANCE = new SegBuildScheduler(CANONICAL_CAP);

  private final int maxConcurrent;
  private final Semaphore slots;
  private final AtomicInteger inFlight = new AtomicInteger();
  private final AtomicInteger peak = new AtomicInteger();

  SegBuildScheduler(int maxConcurrent) {
    this.maxConcurrent = Math.max(1, maxConcurrent);
    this.slots = new Semaphore(this.maxConcurrent);
  }

  public static SegBuildScheduler getInstance() {
    return INSTANCE;
  }

  public int getMaxConcurrentBuilds() {
    return maxConcurrent;
  }

  public int peakConcurrent() {
    return peak.get();
  }

  public void runBuild(Runnable task) {
    slots.acquireUninterruptibly();
    int now = inFlight.incrementAndGet();
    peak.accumulateAndGet(now, Math::max);
    try {
      task.run();
    } finally {
      inFlight.decrementAndGet();
      slots.release();
    }
  }
}
