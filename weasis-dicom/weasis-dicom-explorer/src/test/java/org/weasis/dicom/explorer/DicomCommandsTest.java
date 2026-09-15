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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DicomCommandsTest {

  private final DicomCommands commands = new DicomCommands();

  @AfterEach
  void resetSharedModel() {
    LocalPersistence.reset();
  }

  @Test
  void rsBuildsQidoUrl() {
    String out =
        commands.rs("--url", "https://demo.orthanc-server.com/dicom-web", "-r", "patientID=5Yp0E");
    assertTrue(out.contains("/studies"));
    assertTrue(out.contains("patientID=5Yp0E"));
  }

  @Test
  void closeAll() {
    DicomModel model = LocalPersistence.getDicomModel();
    model.addInstance(inst("P1", "2.25.st1", "2.25.se1"));
    assertEquals(1, model.getInstances().size());
    assertEquals("close-all", commands.close("--all"));
    assertEquals(0, model.getInstances().size());
    model.addInstance(inst("P1", "2.25.st1", "2.25.se1"));
    assertEquals("close-all", commands.close("-a"));
    assertEquals(0, model.getInstances().size());
  }

  @Test
  void closePatientStudySeriesRemovesOnlyMatching() {
    DicomModel model = LocalPersistence.getDicomModel();
    model.addInstance(inst("P1", "2.25.st1", "2.25.se1"));
    model.addInstance(inst("P2", "2.25.st2", "2.25.se2"));
    assertEquals("close-patient P1", commands.close("-p", "P1"));
    assertEquals(1, model.getInstances().size());
    assertEquals("P2", model.getInstances().get(0).patientId());
    assertEquals("close-study 2.25.st2", commands.close("-y", "2.25.st2"));
    assertEquals(0, model.getInstances().size());
    model.addInstance(inst("P3", "2.25.st3", "2.25.se3"));
    model.addInstance(inst("P3", "2.25.st3", "2.25.se4"));
    assertEquals("close-series 2.25.se3", commands.close("-s", "2.25.se3"));
    assertEquals(1, model.getInstances().size());
    assertEquals("2.25.se4", model.getInstances().get(0).seriesUid());
  }

  static ImportedInstance inst(String patientId, String studyUid, String seriesUid) {
    return new ImportedInstance(
        "SYNTHETIC^" + patientId,
        patientId,
        studyUid,
        seriesUid,
        seriesUid + ".1",
        "1.2.840.10008.10.0.2.2.1.2",
        "CT",
        "chest",
        "20260101",
        1,
        1,
        null,
        "image/dicom");
  }

  @Test
  void getRemoteIsParsedWithoutNetwork() {
    String out =
        commands.get("-r", "https://example.invalid/a.dcm", "-w", "https://example.invalid/mf.xml");
    assertTrue(out.contains("remote https://example.invalid/a.dcm"));
    assertTrue(out.contains("manifest https://example.invalid/mf.xml"));
  }

  @Test
  void getLocalLoadsSharedModel(@TempDir Path dir) throws Exception {
    LocalPersistence.reset();
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomExplorer explorer = new DicomExplorer(LocalPersistence.getDicomModel());
    String out = commands.get("-l", ct.getAbsolutePath());
    assertTrue(out.contains("imported=1"));
    assertEquals(1, LocalPersistence.getDicomModel().getInstances().size());
    assertEquals(1, explorer.patientPane().getSelectionManager().patientKeys().size());
    assertFalse(explorer.studyPane().getSeriesPane().thumbnails().isEmpty());
  }

  @Test
  void rsHeadersAndQueryExt() {
    String out =
        commands.rs(
            "--url",
            "https://demo.orthanc-server.com/dicom-web",
            "-r",
            "patientID=5Yp0E",
            "--query-ext",
            "&includedefaults=false",
            "-H",
            "Authorization: Bearer token");
    assertTrue(out.contains("/studies"));
    assertTrue(out.contains("patientID=5Yp0E"));
    assertTrue(out.contains("includedefaults=false"));
    assertTrue(out.contains("header Authorization: Bearer token"));
  }

  @Test
  void rsWadoRsInstancePath() {
    String out =
        commands.rs(
            "--url",
            "https://pacs.example/dicom-web",
            "-r",
            "studyUID=2.25.11",
            "-r",
            "seriesUID=2.25.10",
            "-r",
            "objectUID=2.25.100");
    assertTrue(
        out.contains(
            "wado-rs https://pacs.example/dicom-web/studies/2.25.11/series/2.25.10/instances/2.25.100"));
  }
}
