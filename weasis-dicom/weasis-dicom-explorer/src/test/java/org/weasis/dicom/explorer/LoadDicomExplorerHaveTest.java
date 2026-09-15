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

import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LoadDicomExplorerHaveTest {

  @AfterEach
  void resetSharedModel() {
    LocalPersistence.reset();
  }

  @Test
  void loadDicomOnSharedModelPutsSeriesInExplorer(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomModel model = LocalPersistence.getDicomModel();
    DicomExplorer explorer = new DicomExplorer(model);
    new LoadDicom(model, ct).load();
    assertEquals(1, explorer.seriesSelection().getItems().size());
  }
}
