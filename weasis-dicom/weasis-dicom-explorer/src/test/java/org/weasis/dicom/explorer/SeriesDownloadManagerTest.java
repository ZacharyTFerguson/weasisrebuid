/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.WProperties;

class SeriesDownloadManagerTest {

  @Test
  void prefsDefaultToThreeAndFour() {
    WProperties prefs = new WProperties();
    SeriesDownloadManager mgr = SeriesDownloadManager.fromPrefs(prefs);
    assertTrue(mgr.seriesCap() == SeriesDownloadManager.CONCURRENT_SERIES);
    assertTrue(mgr.imageCap() == SeriesDownloadManager.CONCURRENT_DOWNLOADS_IN_SERIES);
  }

  @Test
  void peakConcurrentSeriesNeverExceedsCap() throws Exception {
    SeriesDownloadManager mgr = new SeriesDownloadManager(3, 4);
    AtomicInteger current = new AtomicInteger();
    ExecutorService pool = Executors.newFixedThreadPool(8);
    List<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < 16; i++) {
      futures.add(
          pool.submit(
              () ->
                  mgr.runSeries(
                      () -> {
                        int now = current.incrementAndGet();
                        try {
                          Thread.sleep(15);
                        } catch (InterruptedException e) {
                          Thread.currentThread().interrupt();
                        }
                        current.decrementAndGet();
                        assertTrue(now <= 3);
                      })));
    }
    for (Future<?> f : futures) {
      f.get();
    }
    pool.shutdownNow();
    assertTrue(mgr.peakConcurrentSeries() <= 3);
    assertTrue(mgr.peakConcurrentSeries() >= 1);
  }

  @Test
  void peakConcurrentImagesNeverExceedsCap() {
    SeriesDownloadManager mgr = new SeriesDownloadManager(3, 4);
    AtomicInteger current = new AtomicInteger();
    mgr.runImagesInSeries(
        16,
        () -> {
          int now = current.incrementAndGet();
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          current.decrementAndGet();
          assertTrue(now <= 4);
        });
    assertTrue(mgr.peakConcurrentImages() <= 4);
    assertTrue(mgr.peakConcurrentImages() >= 1);
  }
}
