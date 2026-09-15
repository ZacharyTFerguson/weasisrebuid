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
 * Headless agents inspect renderer/version strings; they do not tick tutorial Pass.
 */
public final class OpenGLInfo {

  public static final int MIN_MAJOR = 3;
  public static final int MIN_MINOR = 3;
  public static final String MIN_VERSION = "3.3";
  public static final String LLVMPIPE = "llvmpipe";

  public enum Verdict {
    OK,
    REFUSED_LLVMPIPE,
    NA_NO_GPU,
    BELOW_MIN_VERSION
  }

  public record Caps(String renderer, String version, Verdict verdict) {
    public boolean canRenderVolume() {
      return verdict == Verdict.OK;
    }
  }

  public record Version(int major, int minor) {
    public boolean meetsMinimum() {
      return major > MIN_MAJOR || (major == MIN_MAJOR && minor >= MIN_MINOR);
    }
  }

  private OpenGLInfo() {}

  public static boolean isSoftwareRenderer(String renderer) {
    return renderer != null && renderer.toLowerCase().contains(LLVMPIPE);
  }

  public static boolean hasGpu(String renderer) {
    return renderer != null && !renderer.isBlank();
  }

  public static Version parseVersion(String version) {
    if (version == null || version.isBlank()) {
      return null;
    }
    String v = version.replace("OpenGL", "").replace("ES", "").trim();
    String num = v.split("\\s+")[0];
    String[] parts = num.split("\\.");
    try {
      int major = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
      int minor = 0;
      if (parts.length > 1) {
        String rest = parts[1].replaceAll("[^0-9].*", "");
        if (!rest.isEmpty()) {
          minor = Integer.parseInt(rest);
        }
      }
      return new Version(major, minor);
    } catch (RuntimeException e) {
      return null;
    }
  }

  public static boolean meetsMinVersion(String version) {
    Version parsed = parseVersion(version);
    return parsed != null && parsed.meetsMinimum();
  }

  public static boolean canRenderVolume(String renderer, String version) {
    return inspect(renderer, version) == Verdict.OK;
  }

  public static Caps describe(String renderer, String version) {
    return new Caps(renderer, version, inspect(renderer, version));
  }

  public static Verdict inspect(String renderer, String version) {
    if (!hasGpu(renderer)) {
      return Verdict.NA_NO_GPU;
    }
    if (isSoftwareRenderer(renderer)) {
      return Verdict.REFUSED_LLVMPIPE;
    }
    if (version == null || version.isBlank()) {
      return Verdict.NA_NO_GPU;
    }
    Version parsed = parseVersion(version);
    if (parsed == null) {
      return Verdict.NA_NO_GPU;
    }
    if (!parsed.meetsMinimum()) {
      return Verdict.BELOW_MIN_VERSION;
    }
    return Verdict.OK;
  }
}
