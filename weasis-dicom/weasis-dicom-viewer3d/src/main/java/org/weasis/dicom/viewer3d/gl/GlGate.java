/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.gl;

import java.util.Locale;
import java.util.Optional;

/**
 * OpenGL 3.3+ required. Software rasterizers (llvmpipe) are refused. Headless builders without a
 * GPU return empty — CHECKLIST §6 is {@code N/A: no GPU}, not Pass.
 */
public final class GlGate {

  public static final double MIN_VERSION = 3.3;

  private GlGate() {}

  public static boolean usable(String renderer, String glVersion) {
    if (renderer == null || glVersion == null) {
      return false;
    }
    String r = renderer.toLowerCase(Locale.ROOT);
    if (r.contains("llvmpipe")
        || r.contains("software rasterizer")
        || r.contains("mesa offscreen")) {
      return false;
    }
    return parseVersion(glVersion) + 1e-9 >= MIN_VERSION;
  }

  public static double parseVersion(String glVersion) {
    if (glVersion == null) {
      return 0;
    }
    String digits = glVersion.replaceAll("[^0-9.]", " ").trim();
    if (digits.isEmpty()) {
      return 0;
    }
    String first = digits.split("\\s+")[0];
    int dot = first.indexOf('.');
    try {
      if (dot < 0) {
        return Double.parseDouble(first);
      }
      String majorMinor = first.substring(0, Math.min(first.length(), dot + 2));
      return Double.parseDouble(majorMinor);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /** Probe is empty on this headless Cloud Agent (no GPU / no JOGL context). */
  public static Optional<GlInfo> probe() {
    String forced = System.getProperty("weasis.gl.renderer");
    if (forced != null && !forced.isBlank()) {
      String ver = System.getProperty("weasis.gl.version", "0");
      return Optional.of(new GlInfo(forced, ver, usable(forced, ver)));
    }
    return Optional.empty();
  }

  public record GlInfo(String renderer, String version, boolean usable) {}
}
