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

/**
 * MX-15: OpenGL 3.3+ required; software rasterizer {@code llvmpipe} is refused; missing GPU is N/A.
 */
public final class OpenGLInfo {

  public enum Verdict {
    OK,
    REFUSED_LLVMPIPE,
    NA_NO_GPU
  }

  private OpenGLInfo() {}

  public static Verdict inspect(String renderer, String version) {
    if (renderer == null || renderer.isBlank()) {
      return Verdict.NA_NO_GPU;
    }
    if (renderer.toLowerCase().contains("llvmpipe")) {
      return Verdict.REFUSED_LLVMPIPE;
    }
    if (version == null || version.isBlank()) {
      return Verdict.NA_NO_GPU;
    }
    String v = version.replace("OpenGL", "").trim();
    String num = v.split("\\s+")[0];
    String[] parts = num.split("\\.");
    try {
      int major = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
      int minor = 0;
      if (parts.length > 1) {
        minor = Integer.parseInt(parts[1].replaceAll("[^0-9].*", ""));
      }
      if (major < 3 || (major == 3 && minor < 3)) {
        return Verdict.NA_NO_GPU;
      }
    } catch (RuntimeException e) {
      return Verdict.NA_NO_GPU;
    }
    return Verdict.OK;
  }
}
