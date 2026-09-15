/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.weasis.acquire.explorer.StillFormats;

/** {@link MediaSource} backed by a filesystem directory of stills. */
public class FileSystemDrive extends MediaSource {

  private Path path;

  public FileSystemDrive(Path path) {
    super(idOf(path), displayOf(path));
    this.path = path;
  }

  public Path getPath() {
    return path;
  }

  public void setPath(Path path) {
    this.path = path;
    setID(idOf(path));
    setDisplayName(displayOf(path));
  }

  /** Immediate children that {@link StillFormats#isStill(Path)} accepts, sorted by file name. */
  public List<Path> listStills() {
    if (path == null) {
      return List.of();
    }
    if (Files.isRegularFile(path)) {
      return StillFormats.isStill(path) ? List.of(path) : List.of();
    }
    if (!Files.isDirectory(path)) {
      return List.of();
    }
    try (Stream<Path> stream = Files.list(path)) {
      return stream
          .filter(Files::isRegularFile)
          .filter(p -> !p.getFileName().toString().startsWith("."))
          .filter(StillFormats::isStill)
          .sorted(Comparator.comparing(p -> p.getFileName().toString()))
          .toList();
    } catch (IOException e) {
      return List.of();
    }
  }

  static String idOf(Path path) {
    return path == null ? "" : path.toAbsolutePath().normalize().toString();
  }

  static String displayOf(Path path) {
    if (path == null) {
      return "";
    }
    Path name = path.getFileName();
    return name == null ? path.toString() : name.toString();
  }
}
