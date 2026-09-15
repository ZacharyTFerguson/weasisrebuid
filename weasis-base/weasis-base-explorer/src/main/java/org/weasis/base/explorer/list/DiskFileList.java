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
    List<Path> out = new ArrayList<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path path : stream) {
        String name = path.getFileName() == null ? "" : path.getFileName().toString();
        if (name.startsWith(".")) {
          continue;
        }
        if (directories ? Files.isDirectory(path) : Files.isRegularFile(path)) {
          out.add(path);
        }
      }
    }
    out.sort(Comparator.comparing(path -> path.getFileName().toString()));
    return List.copyOf(out);
  }
}
