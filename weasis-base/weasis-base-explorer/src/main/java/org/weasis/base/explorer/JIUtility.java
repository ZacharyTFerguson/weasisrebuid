/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

/** Path helpers for the non-DICOM explorer ({@code weasis://} / thumbnail listing). */
public final class JIUtility {

  private static final Set<String> IMAGE_EXT =
      Set.of("png", "jpg", "jpeg", "gif", "bmp", "tif", "tiff");

  private JIUtility() {}

  public static String fileName(Path path) {
    if (path == null || path.getFileName() == null) {
      return "";
    }
    return path.getFileName().toString();
  }

  public static String extension(Path path) {
    String name = fileName(path);
    int dot = name.lastIndexOf('.');
    if (dot < 0 || dot >= name.length() - 1) {
      return "";
    }
    return name.substring(dot + 1).toLowerCase(Locale.ROOT);
  }

  public static boolean isImageFile(Path path) {
    return IMAGE_EXT.contains(extension(path));
  }

  public static String formatSize(long bytes) {
    if (bytes < 1024L) {
      return bytes + " B";
    }
    if (bytes < 1024L * 1024L) {
      return oneDecimal(bytes / 1024.0) + " KB";
    }
    if (bytes < 1024L * 1024L * 1024L) {
      return oneDecimal(bytes / (1024.0 * 1024.0)) + " MB";
    }
    return oneDecimal(bytes / (1024.0 * 1024.0 * 1024.0)) + " GB";
  }

  static String oneDecimal(double value) {
    return String.format(Locale.ROOT, "%.1f", value);
  }

  public static JIExplorerContext newContext() {
    return new JIExplorerContext();
  }
}
