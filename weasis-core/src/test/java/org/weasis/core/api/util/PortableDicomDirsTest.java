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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PortableDicomDirsTest {

  @Test
  void defaultNamesAndExistingSiblings(@TempDir Path root) throws Exception {
    assertEquals(
        List.of("dicom", "DICOM", "IMAGES", "images"), PortableDicomDirs.names(null));
    Files.createDirectory(root.resolve("dicom"));
    Files.createDirectory(root.resolve("IMAGES"));
    Files.createDirectory(root.resolve("other"));
    List<Path> found = PortableDicomDirs.existing(root, PortableDicomDirs.DEFAULT);
    assertEquals(2, found.size());
    assertTrue(found.contains(root.resolve("dicom").toAbsolutePath().normalize()));
    assertTrue(found.contains(root.resolve("IMAGES").toAbsolutePath().normalize()));
  }
}
