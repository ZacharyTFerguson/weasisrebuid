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

import org.junit.jupiter.api.Test;

class ManifestParserTest {

  @Test
  void parsesJsonSeriesArray() throws Exception {
    String json =
        "{\"series\":[{\"seriesUID\":\"2.25.10\",\"studyUID\":\"2.25.11\",\"patientID\":\"SYN-1\",\"instances\":[{},{}]}]}";
    var list = ManifestParser.parseJson(json);
    assertEquals(1, list.size());
    assertEquals("2.25.10", list.get(0).seriesUid());
    assertEquals(2, list.get(0).instanceCount());
  }

  @Test
  void parsesXmlSeriesAttributes() {
    String xml =
        "<wado><Patient patientID=\"SYN-1\"><Study studyUID=\"2.25.11\"><Series seriesUID=\"2.25.10\"/></Study></Patient></wado>";
    var list = ManifestParser.parseXml(xml);
    assertEquals(1, list.size());
    assertEquals("2.25.10", list.get(0).seriesUid());
  }
}
