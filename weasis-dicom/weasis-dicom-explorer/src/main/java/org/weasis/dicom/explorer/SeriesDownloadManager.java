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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import org.weasis.core.api.service.WProperties;

/**
 * Slot mutex for retrieve (MX-10 / MX-11). Keys {@code download.concurrent.series} (3) and {@code
 * download.concurrent.series.images} (4).
 */
public final class SeriesDownloadManager {

  public static final String PREF_SERIES = "download.concurrent.series";
  public static final String PREF_IMAGES = "download.concurrent.series.images";
  public static final int CONCURRENT_SERIES = 3;
  public static final int CONCURRENT_DOWNLOADS_IN_SERIES = 4;

  private final Semaphore seriesSlots;
  private final int seriesCap;
  private final int imageCap;
  private final AtomicInteger peakSeries = new AtomicInteger();
  private final AtomicInteger inFlightSeries = new AtomicInteger();
  private final AtomicInteger peakImages = new AtomicInteger();

  public SeriesDownloadManager() {
    this(CONCURRENT_SERIES, CONCURRENT_DOWNLOADS_IN_SERIES);
  }

  public SeriesDownloadManager(int seriesCap, int imageCap) {
    this.seriesCap = Math.max(1, seriesCap);
    this.imageCap = Math.max(1, imageCap);
    this.seriesSlots = new Semaphore(this.seriesCap);
  }

  public static SeriesDownloadManager fromPrefs(WProperties prefs) {
    int series =
        prefs == null ? CONCURRENT_SERIES : prefs.getIntProperty(PREF_SERIES, CONCURRENT_SERIES);
    int images =
        prefs == null
            ? CONCURRENT_DOWNLOADS_IN_SERIES
            : prefs.getIntProperty(PREF_IMAGES, CONCURRENT_DOWNLOADS_IN_SERIES);
    return new SeriesDownloadManager(series, images);
  }

  public int seriesCap() {
    return seriesCap;
  }

  public int imageCap() {
    return imageCap;
  }

  public int peakConcurrentSeries() {
    return peakSeries.get();
  }

  public int peakConcurrentImages() {
    return peakImages.get();
  }

  public void runSeries(Runnable task) {
    Objects.requireNonNull(task, "task");
    seriesSlots.acquireUninterruptibly();
    int now = inFlightSeries.incrementAndGet();
    peakSeries.accumulateAndGet(now, Math::max);
    try {
      task.run();
    } finally {
      inFlightSeries.decrementAndGet();
      seriesSlots.release();
    }
  }

  public void runImagesInSeries(int count, Runnable each) {
    Objects.requireNonNull(each, "each");
    Semaphore images = new Semaphore(imageCap);
    AtomicInteger inFlight = new AtomicInteger();
    ExecutorService pool = Executors.newFixedThreadPool(Math.max(1, Math.min(count, imageCap * 2)));
    List<Future<?>> futures = new ArrayList<>();
    try {
      for (int i = 0; i < count; i++) {
        futures.add(
            pool.submit(
                () -> {
                  images.acquireUninterruptibly();
                  int now = inFlight.incrementAndGet();
                  peakImages.accumulateAndGet(now, Math::max);
                  try {
                    each.run();
                  } finally {
                    inFlight.decrementAndGet();
                    images.release();
                  }
                }));
      }
      for (Future<?> future : futures) {
        try {
          future.get();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(e);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        }
      }
    } finally {
      pool.shutdownNow();
    }
  }
}
