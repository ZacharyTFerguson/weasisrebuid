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

import org.junit.jupiter.api.Test;

class OpenGLInfoTest {

  @Test
  void refuseLlvmpipe() {
    assertEquals(
        OpenGLInfo.Verdict.REFUSED_LLVMPIPE, OpenGLInfo.inspect("llvmpipe (LLVM 15)", "4.5"));
  }

  @Test
  void naWithoutGpu() {
    assertEquals(OpenGLInfo.Verdict.NA_NO_GPU, OpenGLInfo.inspect(null, null));
  }

  @Test
  void okModernGl() {
    assertEquals(OpenGLInfo.Verdict.OK, OpenGLInfo.inspect("NVIDIA GeForce", "4.6.0"));
  }
}
