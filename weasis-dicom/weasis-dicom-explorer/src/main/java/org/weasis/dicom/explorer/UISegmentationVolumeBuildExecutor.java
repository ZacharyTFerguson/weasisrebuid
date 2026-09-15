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

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.weasis.dicom.codec.seg.MaskFrames;

/**
 * Runs SEG volume rasterization off the EDT. The default work reflects {@code
 * org.weasis.dicom.viewer2d.mpr.SegVolumeBuilder} so explorer does not compile against viewer2d;
 * tests inject that builder and assert a {@code Future} of {@code Volume}.
 */
public class UISegmentationVolumeBuildExecutor implements AutoCloseable {

  private static final String SEG_VOLUME_BUILDER = "org.weasis.dicom.viewer2d.mpr.SegVolumeBuilder";

  private final ExecutorService workers;
  private final VolumeWork<?> defaultWork;

  public UISegmentationVolumeBuildExecutor() {
    this(defaultSegVolumeWork());
  }

  public UISegmentationVolumeBuildExecutor(VolumeWork<?> defaultWork) {
    this.defaultWork = defaultWork == null ? defaultSegVolumeWork() : defaultWork;
    this.workers = Executors.newSingleThreadExecutor(new DaemonFactory());
  }

  public Future<?> submit(MaskFrames frames) {
    return submit(frames, defaultWork);
  }

  public <V> Future<V> submit(MaskFrames frames, VolumeWork<V> work) {
    VolumeWork<V> raster = work == null ? asWork(defaultWork) : work;
    return submit(() -> raster.rasterize(frames));
  }

  public <V> Future<V> submit(Callable<V> work) {
    return workers.submit(
        () -> {
          if (EventQueue.isDispatchThread()) {
            throw new IllegalStateException("SEG volume build must not run on the EDT");
          }
          if (work == null) {
            return null;
          }
          return work.call();
        });
  }

  @Override
  public void close() {
    workers.shutdownNow();
  }

  @FunctionalInterface
  public interface VolumeWork<V> {
    V rasterize(MaskFrames frames) throws Exception;
  }

  @SuppressWarnings("unchecked")
  private static <V> VolumeWork<V> asWork(VolumeWork<?> work) {
    return frames -> (V) work.rasterize(frames);
  }

  static VolumeWork<Object> defaultSegVolumeWork() {
    return frames -> {
      Class<?> type = Class.forName(SEG_VOLUME_BUILDER);
      Object builder = type.getDeclaredConstructor().newInstance();
      return type.getMethod("rasterize", MaskFrames.class).invoke(builder, frames);
    };
  }

  private static final class DaemonFactory implements ThreadFactory {
    private final AtomicInteger n = new AtomicInteger();

    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, "seg-volume-build-" + n.incrementAndGet());
      thread.setDaemon(true);
      return thread;
    }
  }
}
