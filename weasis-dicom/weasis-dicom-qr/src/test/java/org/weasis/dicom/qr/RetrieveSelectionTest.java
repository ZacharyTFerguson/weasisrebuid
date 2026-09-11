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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.dicom.explorer.SeriesDownloadManager;
import org.weasis.dicom.export.OriginalImageExport;

class RetrieveSelectionTest {

  @Test
  void aCheckedStudyWinsOverItsSeriesWhateverTheOrder() {
    RetrieveSelection sel = new RetrieveSelection();
    sel.checkSeries("S", "a");
    sel.checkStudy("S");
    sel.checkSeries("S", "b");
    assertTrue(sel.retrievesAllSeries("S"));
    assertEquals(RetrieveSelection.Level.STUDY, sel.levelFor("S"));
    RetrieveSelection onlySeries = new RetrieveSelection();
    onlySeries.checkSeries("S", "a");
    assertFalse(onlySeries.retrievesAllSeries("S"));
  }
}

class NetworkHaveTest {

  @Test
  void qidoAcceptAndCgetTcsAndMx05Mx12() throws Exception {
    QidoClient qido = new QidoClient();
    assertEquals("application/dicom+json", qido.qidoHeaders().get("Accept"));
    qido.setRetrieveAccept("application/dicom");
    assertEquals("application/dicom", qido.retrieveHeaders().get("Accept"));
    assertFalse(qido.qidoHeaders().get("Accept").equals("application/dicom"));
    assertTrue(
        DicomResource.loadStoreTcs().keySet().stream().anyMatch(k -> k.startsWith("CGET_SOP_UID")));
    assertEquals("ISO_IR 192", new DimseClient().charset());
    OriginalImageExport exp = new OriginalImageExport();
    assertTrue(exp.usesModalityLutHu());
    exp.setPreserve16Bit(false);
    assertTrue(exp.usesHeaderVoi());
    LoadQrSeries load = new LoadQrSeries(new SeriesDownloadManager());
    load.start("a");
    load.start("b");
    load.start("c");
    load.preempt("d");
    assertEquals(3, load.inFlight());
    assertEquals("a", load.preempted());
    DicomNodeList list = new DicomNodeList();
    list.add(new DicomNodeList.Node("pacs", "WEASIS", "127.0.0.1", 11112, false));
    assertEquals(1, list.sendDestinations().size());
  }
}
