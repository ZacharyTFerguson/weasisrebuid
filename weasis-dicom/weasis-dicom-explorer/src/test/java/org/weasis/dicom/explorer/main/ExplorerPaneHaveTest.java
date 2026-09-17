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

import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.SeriesThumbnail;
import org.weasis.dicom.codec.DicomMime;
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
    assertEquals("patient-combo", patients.getCombo().getName());
    StudyPane studies = new StudyPane(selection);
    assertEquals("study-combo", studies.getCombo().getName());
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

  @Test
  void overlaySopFamiliesPaintKoPrSegRtCornerIcons() {
    SeriesPane pane = new SeriesPane();
    pane.showThumbnails(
        List.of(
            instMime("2.25.ko", DicomMime.KO_DICOM, 1),
            instMime("2.25.pr", DicomMime.PR_DICOM, 2),
            instMime("2.25.seg", DicomMime.SEG_DICOM, 3),
            instMime("2.25.rt", DicomMime.RT_DICOM, 4),
            instMime("2.25.ct", DicomMime.IMAGE_DICOM, 5)));
    List<SeriesThumbnail> thumbs = pane.thumbnails();
    assertEquals(5, thumbs.size());
    assertEquals("KO", thumbs.get(0).getOverlayIcon());
    assertEquals("PR", thumbs.get(1).getOverlayIcon());
    assertEquals("SEG", thumbs.get(2).getOverlayIcon());
    assertEquals("RT", thumbs.get(3).getOverlayIcon());
    assertEquals("", thumbs.get(4).getOverlayIcon());
  }

  static ImportedInstance instMime(String seriesUid, String mime, int series) {
    return new ImportedInstance(
        "SYNTHETIC^OVL",
        "SYN-OVL",
        "2.25.study",
        seriesUid,
        seriesUid + ".1",
        "1.2.840.10008.10.0.2.2.1.2",
        "OT",
        seriesUid,
        "20260101",
        series,
        1,
        null,
        mime);
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
