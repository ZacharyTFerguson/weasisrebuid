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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.EventQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.codec.seg.MaskFrames;
import org.weasis.dicom.viewer2d.mpr.SegVolumeBuilder;
import org.weasis.dicom.viewer2d.mpr.Volume;
import org.weasis.dicom.viewer2d.mpr.VolumeByte;

class UISegmentationVolumeBuildExecutorHaveTest {

  @Test
  void rasterizesSegVolumeOffEdt() throws Exception {
    byte[] axial = new byte[] {0, 3, 0, 0};
    byte[] next = new byte[] {0, 0, 7, 0};
    MaskFrames frames = new MaskFrames(2, 2, new byte[][] {axial, next});
    AtomicReference<Thread> worker = new AtomicReference<>();
    AtomicBoolean onEdt = new AtomicBoolean(true);
    CountDownLatch submitted = new CountDownLatch(1);
    AtomicReference<Future<Volume>> futureRef = new AtomicReference<>();
    try (UISegmentationVolumeBuildExecutor exec =
        new UISegmentationVolumeBuildExecutor(new SegVolumeBuilder()::rasterize)) {
      SwingUtilities.invokeAndWait(
          () -> {
            assertTrue(EventQueue.isDispatchThread());
            futureRef.set(
                exec.submit(
                    frames,
                    mask -> {
                      worker.set(Thread.currentThread());
                      onEdt.set(EventQueue.isDispatchThread());
                      return new SegVolumeBuilder().rasterize(mask);
                    }));
            submitted.countDown();
          });
      assertTrue(submitted.await(5, TimeUnit.SECONDS));
      Volume volume = futureRef.get().get(5, TimeUnit.SECONDS);
      assertInstanceOf(VolumeByte.class, volume);
      assertEquals(3.0, volume.value(1, 0, 0), 1e-9);
      assertEquals(7.0, volume.value(0, 1, 1), 1e-9);
      assertFalse(onEdt.get());
      assertNotEquals(Thread.currentThread(), worker.get());
    }
  }
}
