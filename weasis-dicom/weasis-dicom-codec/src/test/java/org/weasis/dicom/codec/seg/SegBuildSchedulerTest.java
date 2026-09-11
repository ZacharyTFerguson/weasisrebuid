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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class SegBuildSchedulerTest {

  @Test
  void neverExceedsMaxConcurrentBuilds() throws Exception {
    SegBuildScheduler scheduler = SegBuildScheduler.getInstance();
    int cap = scheduler.getMaxConcurrentBuilds();
    ExecutorService pool = Executors.newFixedThreadPool(8);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(8);
    for (int i = 0; i < 8; i++) {
      pool.submit(
          () -> {
            try {
              start.await();
              scheduler.runBuild(
                  () -> {
                    try {
                      Thread.sleep(20);
                    } catch (InterruptedException e) {
                      Thread.currentThread().interrupt();
                    }
                  });
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            } finally {
              done.countDown();
            }
          });
    }
    start.countDown();
    assertTrue(done.await(5, TimeUnit.SECONDS));
    pool.shutdownNow();
    assertTrue(scheduler.peakConcurrent() <= cap);
  }
}
