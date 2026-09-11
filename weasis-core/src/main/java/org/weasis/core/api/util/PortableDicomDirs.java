/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Portable / CD sibling directories beside the native binary. Pref {@link #PREF} default {@link
 * #DEFAULT} ({@code dicom,DICOM,IMAGES,images}). {@code $dicom:get -p} walks those folders.
 */
public final class PortableDicomDirs {

  public static final String PREF = "weasis.portable.dicom.directory";
  public static final String DEFAULT = "dicom,DICOM,IMAGES,images";

  private PortableDicomDirs() {}

  public static List<String> names(String csv) {
    String raw = csv == null || csv.isBlank() ? DEFAULT : csv;
    List<String> names = new ArrayList<>();
    for (String token : raw.split(",")) {
      String name = token.trim();
      if (!name.isEmpty() && !names.contains(name)) {
        names.add(name);
      }
    }
    return List.copyOf(names);
  }

  public static Path root(Path explicit) {
    if (explicit != null) {
      return explicit.toAbsolutePath().normalize();
    }
    String prop = System.getProperty("weasis.portable.root");
    if (prop != null && !prop.isBlank()) {
      return Path.of(prop).toAbsolutePath().normalize();
    }
    return Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
  }

  public static List<Path> existing(Path root, String csv) {
    Path base = root(root);
    List<Path> found = new ArrayList<>();
    for (String name : names(csv)) {
      Path dir = base.resolve(name);
      if (Files.isDirectory(dir)) {
        found.add(dir);
      }
    }
    return List.copyOf(found);
  }

  public static String normalizePref(String csv) {
    List<String> n = names(csv);
    return n.isEmpty() ? DEFAULT : String.join(",", n);
  }
}
