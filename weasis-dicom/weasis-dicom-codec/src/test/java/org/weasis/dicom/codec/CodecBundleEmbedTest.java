/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** WP-2: Felix must not import {@code org.dcm4che3.image}; it is embedded in the codec bundle. */
class CodecBundleEmbedTest {

  @Test
  void classesEmbedDcm4cheImage() {
    Path clazz = Path.of("target/classes/org/dcm4che3/image/BufferedImageUtils.class");
    assertTrue(Files.isRegularFile(clazz), clazz.toString());
  }

  @Test
  void manifestEmbedsDcm4cheImageNotImport() throws Exception {
    Path manifest = Path.of("target/classes/META-INF/MANIFEST.MF");
    assertTrue(Files.isRegularFile(manifest), manifest.toString());
    String text = Files.readString(manifest);
    String importLine = "";
    String privateLine = "";
    for (String line : text.split("\n")) {
      if (line.startsWith("Import-Package:")) {
        importLine = line;
      } else if (line.startsWith("Private-Package:")) {
        privateLine = line;
      }
    }
    assertTrue(
        privateLine.contains("org.dcm4che3.image"),
        "Private-Package must embed org.dcm4che3.image: " + privateLine);
    assertFalse(
        importLine.contains("org.dcm4che3.image"),
        "Import-Package must not wire org.dcm4che3.image: " + importLine);
  }
}
