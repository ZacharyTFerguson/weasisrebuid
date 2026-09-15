/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.UICore;

class DicomQrViewTest {

  @Test
  void factoryCreatesImportPageAndRegisters() {
    DicomQrFactory factory = new DicomQrFactory();
    factory.activate();
    try {
      assertTrue(UICore.getInstance().getDicomImportFactories().contains(factory));
      assertInstanceOf(DicomQrView.class, factory.createDicomImportPage(new Hashtable<>()));
    } finally {
      factory.deactivate();
    }
  }

  @Test
  void searchFieldsBindCFindIdentifierAndInjectedResultsPlanMove() {
    DicomQrView view = new DicomQrView();
    view.setPatientId("SYNTH");
    view.setPatientName("Test^Patient");
    view.setModality("CT");
    view.applySearchFields();
    Attributes keys = view.searchParameters().buildCFindIdentifier();
    assertEquals("SYNTH", keys.getString(Tag.PatientID));
    assertEquals("Test^Patient", keys.getString(Tag.PatientName));
    assertEquals("CT", keys.getString(Tag.ModalitiesInStudy));
    assertEquals("STUDY", keys.getString(Tag.QueryRetrieveLevel));

    Attributes studyA = study("1.2.STUDY.A", "SER");
    Attributes seriesB = series("1.2.STUDY.B", "1.2.SERIES.B");
    view.loadFindResults(List.of(studyA, seriesB));
    assertEquals(List.of("1.2.STUDY.A", "1.2.STUDY.B"), view.tree().retrieveModel().studyUids());
    assertEquals(List.of("1.2.SERIES.B"), view.tree().retrieveModel().seriesUids("1.2.STUDY.B"));

    view.tree().checkStudy("1.2.STUDY.A");
    view.tree().checkSeries("1.2.STUDY.A", "ignored");
    view.tree().checkSeries("1.2.STUDY.B", "1.2.SERIES.B");
    RetrieveTask task = view.startRetrieve();
    assertEquals(2, task.identifiers().size());
    RetrieveTask.Identifier first = task.identifiers().get(0);
    assertEquals(SearchParameters.QueryRetrieveLevel.STUDY, first.level());
    assertEquals("1.2.STUDY.A", first.studyUid());
    assertEquals(RetrieveContext.RetrieveMethod.C_MOVE, first.method());
    assertEquals(UID.StudyRootQueryRetrieveInformationModelMove, first.sopClassUid());
    assertEquals("STUDY", first.keys().getString(Tag.QueryRetrieveLevel));
    RetrieveTask.Identifier second = task.identifiers().get(1);
    assertEquals(SearchParameters.QueryRetrieveLevel.SERIES, second.level());
    assertEquals("1.2.STUDY.B", second.studyUid());
    assertEquals("1.2.SERIES.B", second.seriesUid());
    assertEquals("1.2.SERIES.B", second.keys().getString(Tag.SeriesInstanceUID));
  }

  @Test
  void gracefulCancelStopsRetrievePlanAndCGetUsesGetSopClass() {
    DicomQrView view = new DicomQrView();
    view.loadFindResults(List.of(study("1.2.STUDY.A", "P")));
    view.tree().checkStudy("1.2.STUDY.A");
    view.retrieveContext().setMethod(RetrieveContext.RetrieveMethod.C_GET);
    view.cancelRetrieve();
    RetrieveTask cancelled = view.startRetrieve();
    assertTrue(cancelled.identifiers().isEmpty());
    assertTrue(cancelled.isCancelled());

    view.resetToDefaultValues();
    view.loadFindResults(List.of(study("1.2.STUDY.A", "P")));
    view.tree().checkStudy("1.2.STUDY.A");
    view.setRetrieveMethod(RetrieveContext.RetrieveMethod.C_GET);
    RetrieveTask get = view.startRetrieve();
    assertEquals(1, get.identifiers().size());
    assertEquals(RetrieveContext.RetrieveMethod.C_GET, get.identifiers().get(0).method());
    assertEquals(
        UID.StudyRootQueryRetrieveInformationModelGet, get.identifiers().get(0).sopClassUid());
  }

  static Attributes study(String studyUid, String patientId) {
    Attributes attrs = new Attributes();
    attrs.setString(Tag.PatientID, VR.LO, patientId);
    attrs.setString(Tag.StudyInstanceUID, VR.UI, studyUid);
    attrs.setString(Tag.QueryRetrieveLevel, VR.CS, "STUDY");
    return attrs;
  }

  static Attributes series(String studyUid, String seriesUid) {
    Attributes attrs = study(studyUid, "SER");
    attrs.setString(Tag.SeriesInstanceUID, VR.UI, seriesUid);
    attrs.setString(Tag.QueryRetrieveLevel, VR.CS, "SERIES");
    return attrs;
  }
}
