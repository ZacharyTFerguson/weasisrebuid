/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer3d.OpenGLInfo;

class VolumeCanvasGpuGateTest {

  @Test
  void canvasRefusesSoftwareRasterizer() {
    VolumeCanvas canvas = new VolumeCanvas(OpenGLInfo.describe("llvmpipe (LLVM 15)", "4.5"));
    assertFalse(canvas.isVolumeRenderingAvailable());
    assertEquals(OpenGLInfo.Verdict.REFUSED_LLVMPIPE, canvas.gpuCaps().verdict());
    canvas.onRotate(0.2, 0.1);
    assertEquals(0.0, canvas.getCamera().getEyeX());
  }

  @Test
  void canvasNaWithoutGpu() {
    VolumeCanvas canvas = new VolumeCanvas();
    assertEquals(OpenGLInfo.Verdict.NA_NO_GPU, canvas.gpuCaps().verdict());
    assertTrue(canvas.statusText().contains("N/A"));
  }

  @Test
  void volumeBuilderStacksSlices() {
    short[][] slices = new short[2][4];
    slices[1][3] = 42;
    DicomVolTexture texture = new VolumeBuilder().build(slices, 2, 2);
    assertEquals(42, texture.getData().getVoxel(1, 1, 1));
    assertEquals(2, texture.getData().getDepth());
  }
}
