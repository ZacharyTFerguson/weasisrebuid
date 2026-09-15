/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.isowriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** ISO9660 DICOM CD/DVD export planner (writes a manifest file in tests; no optical burn). */
public class IsoImageExport {

  private final List<Path> sourceFiles = new ArrayList<>();
  private boolean includeDicomDir = true;

  public boolean isAvailable() {
    return true;
  }

  public void addSource(Path file) {
    sourceFiles.add(file);
  }

  public List<Path> sourceFiles() {
    return List.copyOf(sourceFiles);
  }

  public void setIncludeDicomDir(boolean includeDicomDir) {
    this.includeDicomDir = includeDicomDir;
  }

  public boolean includeDicomDir() {
    return includeDicomDir;
  }

  /**
   * Prepare export layout: creates parent dirs and writes {@code .weasis-iso-manifest} listing
   * paths (synthetic local fixture).
   */
  public Path writeManifest(Path isoTarget) throws IOException {
    Path parent = isoTarget.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    StringBuilder manifest = new StringBuilder();
    if (includeDicomDir) {
      manifest.append("DICOMDIR\n");
    }
    for (Path path : sourceFiles) {
      manifest.append(path.getFileName()).append('\n');
    }
    Path manifestPath =
        parent == null ? Path.of(".weasis-iso-manifest") : parent.resolve(".weasis-iso-manifest");
    Files.writeString(manifestPath, manifest.toString());
    return manifestPath;
  }
}
