/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** MX-15 Have: OpenGL 3.3+; refuse llvmpipe; N/A when no GPU. */
class OpenGLInfoTest {

  @Test
  void refuseLlvmpipeEvenWithModernVersion() {
    assertEquals(
        OpenGLInfo.Verdict.REFUSED_LLVMPIPE, OpenGLInfo.inspect("llvmpipe (LLVM 15)", "4.5"));
    assertTrue(OpenGLInfo.isSoftwareRenderer("Mesa llvmpipe"));
    assertFalse(OpenGLInfo.canRenderVolume("llvmpipe", "4.6.0"));
  }

  @Test
  void naWithoutGpu() {
    assertEquals(OpenGLInfo.Verdict.NA_NO_GPU, OpenGLInfo.inspect(null, null));
    assertEquals(OpenGLInfo.Verdict.NA_NO_GPU, OpenGLInfo.inspect("  ", "4.6"));
    assertEquals(OpenGLInfo.Verdict.NA_NO_GPU, OpenGLInfo.inspect("NVIDIA GeForce", null));
    assertFalse(OpenGLInfo.hasGpu(null));
  }

  @Test
  void requiresOpenGl33() {
    assertEquals(OpenGLInfo.Verdict.BELOW_MIN_VERSION, OpenGLInfo.inspect("NVIDIA GeForce", "3.2"));
    assertEquals(OpenGLInfo.Verdict.BELOW_MIN_VERSION, OpenGLInfo.inspect("Intel", "2.1"));
    assertEquals(OpenGLInfo.Verdict.OK, OpenGLInfo.inspect("NVIDIA GeForce", "3.3"));
    assertEquals(OpenGLInfo.Verdict.OK, OpenGLInfo.inspect("AMD Radeon", "OpenGL 3.3.0"));
    assertEquals(3, OpenGLInfo.MIN_MAJOR);
    assertEquals(3, OpenGLInfo.MIN_MINOR);
  }

  @Test
  void okModernGl() {
    assertEquals(OpenGLInfo.Verdict.OK, OpenGLInfo.inspect("NVIDIA GeForce", "4.6.0"));
    assertTrue(OpenGLInfo.canRenderVolume("NVIDIA GeForce", "4.6.0"));
    assertTrue(OpenGLInfo.describe("NVIDIA GeForce", "4.6.0").canRenderVolume());
  }
}
