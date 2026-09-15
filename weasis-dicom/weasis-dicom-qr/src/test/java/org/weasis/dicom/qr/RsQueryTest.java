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

import java.util.Map;
import org.junit.jupiter.api.Test;

class RsQueryTest {

  @Test
  void qidoAcceptIsDicomJson() {
    RsQuery qido = new RsQuery();
    assertEquals("application/dicom+json", qido.qidoHeaders().get("Accept"));
    qido.setRetrieveAccept("application/dicom");
    assertEquals("application/dicom", qido.retrieveHeaders().get("Accept"));
    assertFalse(qido.qidoHeaders().get("Accept").equals("application/dicom"));
  }

  @Test
  void buildsStudiesUrlWithEncodedParams() {
    RsQuery qido = new RsQuery();
    String url =
        qido.buildStudiesUrl(
            "https://pacs.example/dicom-web", Map.of("PatientID", "SYNTH-001", "limit", "25"));
    assertTrue(url.startsWith("https://pacs.example/dicom-web/studies?"));
    assertTrue(url.contains("PatientID=SYNTH-001"));
    assertTrue(url.contains("limit=25"));
  }
}
