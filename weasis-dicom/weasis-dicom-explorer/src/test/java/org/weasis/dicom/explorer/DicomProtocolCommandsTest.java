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

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.command.DicomRsArgs;

class DicomProtocolCommandsTest {

  @Test
  void getLocalImportsSyntheticCt(@TempDir Path dir) throws Exception {
    LoadLocalDicomTest.writeCt(dir.resolve("a.dcm").toFile());
    DicomModel model = new DicomModel();
    DicomProtocolCommands cmd = new DicomProtocolCommands(model);
    String out = cmd.get("-l", dir.toString());
    assertEquals("imported 1", out);
    assertEquals(1, model.getInstances().size());
    assertEquals("closed 1", cmd.close("-a"));
    assertEquals(0, model.getInstances().size());
  }

  @Test
  void getManifestJsonAndRsRequiresUrl(@TempDir Path dir) throws Exception {
    Path mf = dir.resolve("m.json");
    Files.writeString(
        mf,
        "{\"series\":[{\"seriesUID\":\"2.25.1\",\"studyUID\":\"2.25.2\",\"patientID\":\"SYN-1\",\"instances\":[1]}]}");
    DicomProtocolCommands cmd = new DicomProtocolCommands(new DicomModel());
    assertEquals("manifest 1", cmd.get("-w", mf.toString()));
    assertEquals(DicomRsArgs.MISSING_URL, cmd.rs("-r", "1.2.3"));
    assertTrue(cmd.rs("-u", "https://example/rs").startsWith("rs https://example/rs"));
  }
}
