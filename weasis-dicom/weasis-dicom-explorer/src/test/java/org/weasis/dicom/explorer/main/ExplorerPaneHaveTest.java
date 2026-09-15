/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.dicom.explorer.DicomExplorer;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportedInstance;

class ExplorerPaneHaveTest {

  @Test
  void patientAndStudyPanesGroupDicomModelHierarchy() {
    DicomModel model = new DicomModel();
    model.addInstance(inst("A", "1", "2.25.1", 1));
    model.addInstance(inst("A", "1", "2.25.1", 2));
    model.addInstance(inst("A", "1", "2.25.2", 3));
    model.addInstance(inst("B", "2", "2.25.9", 1));
    PatientPane patients = new PatientPane(model);
    PatientSelectionManager selection = patients.getSelectionManager();
    assertEquals(2, selection.patientKeys().size());
    StudyPane studies = new StudyPane(selection);
    assertEquals(2, selection.studyUids().size());
    assertEquals(2, studies.getSeriesPane().getSelectionModel().getItems().size());
    selection.selectStudyIndex(1);
    studies.refresh();
    assertEquals(1, studies.getSeriesPane().getSelectionModel().getItems().size());
    selection.selectPatientIndex(1);
    studies.refresh();
    assertEquals(1, selection.studyUids().size());
    assertEquals(1, studies.getSeriesPane().getSelectionModel().getItems().size());
  }

  @Test
  void explorerRefreshFiltersSeriesToSelectedPatientAndStudy() {
    DicomModel model = new DicomModel();
    model.addInstance(inst("A", "1", "2.25.1", 1));
    model.addInstance(inst("A", "1", "2.25.1", 2));
    model.addInstance(inst("B", "2", "2.25.9", 1));
    DicomExplorer explorer = new DicomExplorer(model);
    assertEquals(2, explorer.patientPane().getSelectionManager().patientKeys().size());
    assertEquals(2, explorer.seriesSelection().getItems().size());
    explorer.patientPane().getSelectionManager().selectPatientIndex(1);
    explorer.refresh();
    assertEquals(1, explorer.seriesSelection().getItems().size());
    assertTrue(explorer.seriesSelection().getItems().get(0).startsWith("SYNTHETIC^B"));
  }

  static ImportedInstance inst(String name, String id, String study, int series) {
    return new ImportedInstance(
        "SYNTHETIC^" + name,
        "SYN-" + id,
        study,
        study + "." + series,
        study + "." + series + ".1",
        "1.2.840.10008.10.0.2.2.1.2",
        "CT",
        name + series,
        "20260101",
        series,
        1,
        null,
        "image/dicom");
  }
}
