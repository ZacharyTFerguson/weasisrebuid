/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Album import grouping: Do not group / Group by date (max gap) / Group by name. */
public enum ImportGrouping {
  NONE,
  DATE,
  NAME;

  public static List<List<Path>> group(List<Path> files, ImportGrouping mode, Duration maxGap) {
    if (files == null || files.isEmpty()) {
      return List.of();
    }
    if (mode == null || mode == NONE) {
      List<List<Path>> out = new ArrayList<>();
      for (Path p : files) {
        out.add(List.of(p));
      }
      return out;
    }
    if (mode == NAME) {
      Map<String, List<Path>> by = new LinkedHashMap<>();
      for (Path p : files) {
        String name = p.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String stem = dot < 0 ? name : name.substring(0, dot);
        String key = stem.replaceAll("\\d+$", "").toLowerCase(Locale.ROOT);
        by.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
      }
      return List.copyOf(by.values());
    }
    List<Path> sorted = new ArrayList<>(files);
    sorted.sort(Comparator.comparingLong(ImportGrouping::mtime));
    Duration gap = maxGap == null ? Duration.ofHours(1) : maxGap;
    List<List<Path>> groups = new ArrayList<>();
    List<Path> cur = new ArrayList<>();
    long prev = -1;
    for (Path p : sorted) {
      long t = mtime(p);
      if (prev >= 0 && t - prev > gap.toMillis()) {
        groups.add(List.copyOf(cur));
        cur = new ArrayList<>();
      }
      cur.add(p);
      prev = t;
    }
    if (!cur.isEmpty()) {
      groups.add(List.copyOf(cur));
    }
    return groups;
  }

  static long mtime(Path p) {
    try {
      return java.nio.file.Files.getLastModifiedTime(p).toMillis();
    } catch (Exception e) {
      return 0L;
    }
  }
}
