/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;

/** File helpers used by the launcher (WP-0 tagged equivalent of FileUtilTest). */
public final class FileUtil {

  private FileUtil() {}

  public static boolean delete(File file) {
    if (file == null || !file.exists()) {
      return true;
    }
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      if (children != null) {
        for (File child : children) {
          if (!delete(child)) {
            return false;
          }
        }
      }
    }
    return file.delete();
  }

  public static String nameWithoutExtension(String filename) {
    if (filename == null || filename.isBlank()) {
      return "";
    }
    int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
    String base = slash >= 0 ? filename.substring(slash + 1) : filename;
    int dot = base.lastIndexOf('.');
    if (dot <= 0) {
      return base;
    }
    return base.substring(0, dot);
  }

  public static String extension(String filename) {
    if (filename == null) {
      return "";
    }
    int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
    String base = slash >= 0 ? filename.substring(slash + 1) : filename;
    int dot = base.lastIndexOf('.');
    if (dot < 0 || dot == base.length() - 1) {
      return "";
    }
    return base.substring(dot + 1);
  }

  public static boolean hasExtension(String filename, String... extensions) {
    String ext = extension(filename);
    if (ext.isEmpty() || extensions == null) {
      return false;
    }
    for (String candidate : extensions) {
      if (candidate == null) {
        continue;
      }
      String normalized = candidate.startsWith(".") ? candidate.substring(1) : candidate;
      if (ext.equalsIgnoreCase(normalized)) {
        return true;
      }
    }
    return false;
  }

  public static Path write(InputStream in, Path out) throws IOException {
    Objects.requireNonNull(in, "in");
    Objects.requireNonNull(out, "out");
    Path parent = out.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
    return out;
  }

  public static String humanReadableByteCount(long bytes) {
    if (bytes < 1024) {
      return bytes + " B";
    }
    double value = bytes;
    String[] units = {"KB", "MB", "GB", "TB"};
    int unit = -1;
    while (value >= 1024 && unit < units.length - 1) {
      value /= 1024;
      unit++;
    }
    return String.format(Locale.ROOT, "%.1f %s", value, units[unit]);
  }
}
