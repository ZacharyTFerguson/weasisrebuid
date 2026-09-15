/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.weasis.acquire.explorer.core.bean.Global;
import org.weasis.core.api.media.data.TagW;

class AcquirePatientXmlHaveTest {

  /**
   * COMMANDS.md {@code acquire:patient} payload shape from the Dicomizer tutorial: {@code <tags>}
   * DICOM keywords plus nested {@code IssuerOfAccessionNumberSequence}.
   */
  static final String TAGS_XML =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <tags>
        <PatientID>97032168</PatientID>
        <PatientName>TEST^TEST</PatientName>
        <PatientBirthDate>19580703</PatientBirthDate>
        <PatientSex>M</PatientSex>
        <OperatorsName>SYNTHETIC^OP</OperatorsName>
        <AccessionNumber>000000003712</AccessionNumber>
        <IssuerOfAccessionNumberSequence>
          <LocalNamespaceEntityID>411713364</LocalNamespaceEntityID>
        </IssuerOfAccessionNumberSequence>
        <StudyID>411713364</StudyID>
      </tags>
      """;

  @Test
  void documentedTagsXmlFillsGlobalIncludingNestedIssuer() throws Exception {
    AcquirePatientStore store = new AcquirePatientStore();
    assertEquals("ok 97032168", new AcquirePatientCommand(store).patient("-x", TAGS_XML));
    PatientDemographics demo = store.get();
    assertEquals("TEST^TEST", demo.patientName());
    assertEquals("97032168", demo.patientId());
    assertEquals("19580703", demo.birthDate());
    assertEquals("M", demo.sex());
    assertEquals("000000003712", demo.accessionNumber());
    assertEquals("SYNTHETIC^OP", demo.operatorsName());
    assertEquals("411713364", demo.studyId());
    assertEquals("411713364", demo.issuerOfAccessionNumber());

    AcquireManager manager = new AcquireManager();
    manager.loadPatientContext(TAGS_XML);
    Global global = manager.getGlobal();
    assertEquals("TEST^TEST", global.getTagValue(TagW.PatientName));
    assertEquals("97032168", global.getTagValue(TagW.PatientID));
    assertEquals("19580703", global.getTagValue(TagW.PatientBirthDate));
    assertEquals("M", global.getTagValue(TagW.PatientSex));
    assertEquals("000000003712", global.getTagValue(TagW.AccessionNumber));
    assertEquals("SYNTHETIC^OP", global.getTagValue(TagW.OperatorsName));
    assertEquals("411713364", global.getTagValue(TagW.StudyID));
    assertEquals("411713364", global.getTagValue(TagW.LocalNamespaceEntityID));
  }
}
