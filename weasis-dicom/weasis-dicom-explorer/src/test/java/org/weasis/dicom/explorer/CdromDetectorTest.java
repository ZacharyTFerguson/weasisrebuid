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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CdromDetectorTest {

  @Test
  void findsDicomdirUnderInjectedRoot(@TempDir Path dir) throws Exception {
    Path media = dir.resolve("cdrom");
    Files.createDirectories(media);
    Path dicomdir = media.resolve("DICOMDIR");
    Files.writeString(dicomdir, "DICOMDIR");
    Optional<File> found = CdromDetector.detectDicomdir(List.of(dir.toFile()));
    assertTrue(found.isPresent());
    assertEquals(dicomdir.toFile().getCanonicalFile(), found.get().getCanonicalFile());
  }

  @Test
  void emptyRootsYieldEmpty() {
    assertTrue(CdromDetector.detectDicomdir(List.of()).isEmpty());
  }
}
