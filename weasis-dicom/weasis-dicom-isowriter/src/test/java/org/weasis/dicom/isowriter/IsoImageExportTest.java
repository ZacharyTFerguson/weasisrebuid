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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class IsoImageExportTest {

  @TempDir Path temp;

  @Test
  void manifestListsSources() throws Exception {
    Path dicom = temp.resolve("image.dcm");
    Files.writeString(dicom, "SYNTH");
    IsoImageExport export = new ExportIsoFactory().createExport();
    export.addSource(dicom);
    Path manifest = export.writeManifest(temp.resolve("out.iso"));
    String text = Files.readString(manifest);
    assertTrue(export.isAvailable());
    assertTrue(text.contains("DICOMDIR"));
    assertTrue(text.contains("image.dcm"));
  }

  @Test
  void isoWriteDicomdirMapSetsNamedState() throws Exception {
    Path dicom = temp.resolve("chest.dcm");
    Files.writeString(dicom, "SYNTH");
    IsoImageExport export = new ExportIsoFactory().createExport();
    export.addSource(dicom);
    export.setTarget(temp.resolve("out.iso"));
    assertEquals("iso-write", export.writeButton().getName());
    assertEquals("iso-dicomdir", export.dicomdirButton().getName());
    assertEquals("iso-state", export.stateLabel().getName());
    assertEquals("none", export.stateText());
    export.writeButton().doClick();
    assertEquals("manifest", export.stateText());
    assertTrue(Files.readString(export.lastManifest()).contains("chest.dcm"));
    export.dicomdirButton().doClick();
    assertEquals("DICOMDIR", export.stateText());
    assertTrue(Files.readString(export.lastManifest()).contains("DICOMDIR"));
    assertTrue(export.includeDicomDir());
  }
}
