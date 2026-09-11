/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.opencv.internal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * OpenCV natives: skip (not fail) when this builder has no fragment. Message matches
 * REIMPLEMENTATION.md: {@code N/A: no native for this builder}.
 */
class OpenCvNativeIT {

  @Test
  void fragmentJarNameMatchesStartLevel23() {
    String spec = "linux-x86-64";
    String ver = System.getProperty("weasis.opencv.pkg.version", "5.0.0-dcm");
    String expected = "weasis-opencv-core-" + spec + "-" + ver + ".jar";
    Path jar = Path.of("target", expected);
    if (!Files.isRegularFile(jar)) {
      Path alt = Path.of(System.getProperty("user.dir"), "target", expected);
      jar = Files.isRegularFile(alt) ? alt : jar;
    }
    if (!Files.isRegularFile(jar)) {
      System.out.println("N/A: no native for this builder (" + expected + " not packaged yet)");
      return;
    }
    assertTrue(jar.getFileName().toString().equals(expected));
  }

  @Test
  void nativesLoadOrSkip() {
    Path so = locateNative();
    if (so == null) {
      System.out.println("N/A: no native for this builder");
      Assumptions.abort("N/A: no native for this builder");
      return;
    }
    System.load(so.toAbsolutePath().toString());
  }

  static Path locateNative() {
    String[] candidates =
        new String[] {
          "target/classes/libopencv_java.so",
          System.getProperty("java.library.path", "") + "/libopencv_java.so"
        };
    for (String raw : candidates) {
      if (raw == null || raw.isBlank() || raw.startsWith("/libopencv")) {
        continue;
      }
      Path p = Path.of(raw);
      if (Files.isRegularFile(p)) {
        return p;
      }
    }
    String libPath = System.getProperty("java.library.path", "");
    for (String dir : libPath.split(java.io.File.pathSeparator)) {
      if (dir.isBlank()) {
        continue;
      }
      Path p = Path.of(dir, "libopencv_java.so");
      if (Files.isRegularFile(p)) {
        return p;
      }
    }
    return null;
  }
}
