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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;

class SearchParametersTest {

  @Test
  void cfindIdentifierHasCharsetLevelAndReturnKeys() {
    SearchParameters params = new SearchParameters();
    params.setPatientId("SYNTH");
    params.setPatientName("Test^Patient");
    params.setModality("CT");
    Attributes keys = params.buildCFindIdentifier();
    assertEquals(SearchParameters.FIND_CHARSET, keys.getString(Tag.SpecificCharacterSet));
    assertEquals("STUDY", keys.getString(Tag.QueryRetrieveLevel));
    assertEquals("SYNTH", keys.getString(Tag.PatientID));
    assertEquals("Test^Patient", keys.getString(Tag.PatientName));
    assertEquals("CT", keys.getString(Tag.ModalitiesInStudy));
    assertNotNull(keys.getValue(Tag.StudyDate));
    assertEquals(UID.StudyRootQueryRetrieveInformationModelFind, params.findSopClassUid());
  }
}
