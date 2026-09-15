/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.mf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.dicom.explorer.DicomModel;

class DicomModelQueryResultHaveTest {

  @Test
  void applyJsonManifestFillsModelSeriesUids() throws Exception {
    String json =
        "{\"series\":[{\"seriesUID\":\"2.25.10\",\"studyUID\":\"2.25.11\",\"patientID\":\"SYN-1\",\"instances\":[{},{}]}]}";
    DicomModel model = new DicomModel();
    DicomModelQueryResult result = new DicomModelQueryResult(model);
    assertTrue(result.success());
    assertEquals(200, result.httpStatus());
    int added = result.apply(ManifestParser.parseJson(json));
    assertEquals(1, added);
    assertTrue(result.seriesUids().contains("2.25.10"));
    assertEquals(1, model.getInstances().size());
    assertEquals("2.25.10", model.getInstances().getFirst().seriesUid());
    assertEquals("2.25.11", model.getInstances().getFirst().studyUid());
    assertEquals("SYN-1", model.getInstances().getFirst().patientId());
  }

  @Test
  void httpErrorIsNotSuccess() {
    DicomModelQueryResult result = new DicomModelQueryResult(new DicomModel(), 404);
    assertFalse(result.success());
    assertEquals(0, result.apply(null));
    assertTrue(result.seriesUids().isEmpty());
  }
}
