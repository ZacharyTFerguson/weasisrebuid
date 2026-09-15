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

class RetrieveSelectionTest {

  @Test
  void checkedStudyWinsOverSeriesRegardlessOfOrder() {
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

  @Test
  void loadQrSeriesPreemptMx12() {
    LoadQrSeries load = new LoadQrSeries(new SeriesDownloadManager());
    load.start("a");
    load.start("b");
    load.start("c");
    load.preempt("d");
    assertEquals(3, load.inFlight());
    assertEquals("a", load.preempted());
  }

  @Test
  void cgetTransferSyntaxesFromResource() throws Exception {
    assertTrue(
        RetrieveContext.loadCGetTransferSyntaxes().keySet().stream()
            .anyMatch(uid -> uid.startsWith("1.2.")));
  }
}
