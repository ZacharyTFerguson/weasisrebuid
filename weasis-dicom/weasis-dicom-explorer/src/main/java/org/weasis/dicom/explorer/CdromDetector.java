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

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * File &gt; Import &gt; DICOM CD — Detect CD-ROM. Walks injectable roots (tests) or typical mount
 * points looking for a {@code DICOMDIR}. Copy-to-temp is {@link ImportDicomPage#copyLocal(File)}.
 */
public final class CdromDetector {

  private static final int MAX_DEPTH = 4;

  private CdromDetector() {}

  public static List<File> defaultSearchRoots() {
    List<File> roots = new ArrayList<>();
    File[] listed = File.listRoots();
    if (listed != null) {
      for (File r : listed) {
        addIfDirectory(roots, r);
      }
    }
    addIfDirectory(roots, new File("/media"));
    addIfDirectory(roots, new File("/run/media"));
    addIfDirectory(roots, new File("/mnt"));
    addIfDirectory(roots, new File("/Volumes"));
    return roots;
  }

  static void addIfDirectory(List<File> roots, File dir) {
    if (dir != null && dir.isDirectory() && !roots.contains(dir)) {
      roots.add(dir);
    }
  }

  public static Optional<File> detectDicomdir(Iterable<File> roots) {
    if (roots == null) {
      return Optional.empty();
    }
    for (File root : roots) {
      File found = findDicomdir(root, 0);
      if (found != null) {
        return Optional.of(found);
      }
    }
    return Optional.empty();
  }

  static File findDicomdir(File dir, int depth) {
    if (dir == null || !dir.isDirectory() || depth > MAX_DEPTH) {
      return null;
    }
    File[] children = dir.listFiles();
    if (children == null) {
      return null;
    }
    for (File child : children) {
      if (child.isFile() && isDicomdirName(child.getName())) {
        return child;
      }
    }
    for (File child : children) {
      if (child.isDirectory() && Files.isReadable(child.toPath())) {
        File nested = findDicomdir(child, depth + 1);
        if (nested != null) {
          return nested;
        }
      }
    }
    return null;
  }

  static boolean isDicomdirName(String name) {
    if (name == null) {
      return false;
    }
    String n = name.toUpperCase(Locale.ROOT);
    return "DICOMDIR".equals(n) || "DICOMDIR.".equals(n);
  }
}
