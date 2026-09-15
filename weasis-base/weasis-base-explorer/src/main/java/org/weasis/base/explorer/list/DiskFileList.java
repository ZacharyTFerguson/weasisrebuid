/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer.list;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.weasis.base.explorer.JIUtility;

/** Lists a directory for the non-DICOM explorer (files for thumbnails, folders for the tree). */
public class DiskFileList {

  public List<Path> list(Path directory) throws IOException {
    return listFiles(directory);
  }

  public List<Path> listFiles(Path directory) throws IOException {
    return list(directory, false);
  }

  public List<Path> listDirectories(Path directory) throws IOException {
    return list(directory, true);
  }

  private List<Path> list(Path directory, boolean directories) throws IOException {
    if (directory == null || !Files.isDirectory(directory)) {
      return List.of();
    }
    return sorted(collect(directory, directories));
  }

  List<Path> collect(Path directory, boolean directories) throws IOException {
    List<Path> out = new ArrayList<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path path : stream) {
        addVisible(out, path, directories);
      }
    }
    return out;
  }

  static void addVisible(List<Path> out, Path path, boolean directories) {
    if (JIUtility.fileName(path).startsWith(".")) {
      return;
    }
    if (matchesKind(path, directories)) {
      out.add(path);
    }
  }

  static boolean matchesKind(Path path, boolean directories) {
    return directories ? Files.isDirectory(path) : Files.isRegularFile(path);
  }

  static List<Path> sorted(List<Path> out) {
    out.sort(Comparator.comparing(JIUtility::fileName));
    return List.copyOf(out);
  }
}
