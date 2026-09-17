/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.sr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.codec.DicomSpecialElement;

class SRReaderTest {

  @Test
  void walksContentSequenceIntoDisplayTextAndContainer() {
    Attributes sr = basicTextSr();
    SRDocumentContentModule module = new SRReader().read(sr);
    assertEquals("Findings", module.getTitle());
    assertEquals(2, module.getContents().size());
    assertEquals("TEXT", module.getContents().get(0).getValueType());
    assertEquals("No acute process", module.getContents().get(0).getValue());
    assertEquals("12.5 mm", module.getContents().get(1).getValue());
    String text = new SRReader().displayText(sr);
    assertTrue(text.contains("Findings"));
    assertTrue(text.contains("Finding: No acute process"));
    assertTrue(text.contains("Length: 12.5 mm"));

    SRView view = new SRView();
    view.display(sr);
    assertTrue(view.displayedText().contains("No acute process"));

    DicomMediaIO io = new DicomMediaIO(sr, UID.ExplicitVRLittleEndian);
    assertInstanceOf(DicomSpecialElement.class, io.getPreview());
    SRSpecialElement element = new SRSpecialElement(io);
    Series<MediaElement> series = new Series<>();
    series.setMimeType(DicomMime.SR_DICOM);
    series.addMedia(element);
    SRContainer container = new SRContainer();
    container.addSeries(series);
    assertTrue(container.getSRView().displayedText().contains("Finding"));
    assertInstanceOf(SRSpecialElement.class, new SRFactory().buildInstance(io));
  }

  static Attributes basicTextSr() {
    Attributes sr = new Attributes();
    sr.setString(Tag.SOPClassUID, VR.UI, UID.BasicTextSRStorage);
    sr.setString(Tag.Modality, VR.CS, "SR");
    Sequence title = sr.newSequence(Tag.ConceptNameCodeSequence, 1);
    Attributes titleCode = new Attributes();
    titleCode.setString(Tag.CodeValue, VR.SH, "121070");
    titleCode.setString(Tag.CodingSchemeDesignator, VR.SH, "DCM");
    titleCode.setString(Tag.CodeMeaning, VR.LO, "Findings");
    title.add(titleCode);
    Sequence content = sr.newSequence(Tag.ContentSequence, 2);
    Attributes text = new Attributes();
    text.setString(Tag.RelationshipType, VR.CS, "CONTAINS");
    text.setString(Tag.ValueType, VR.CS, "TEXT");
    Sequence textName = text.newSequence(Tag.ConceptNameCodeSequence, 1);
    Attributes finding = new Attributes();
    finding.setString(Tag.CodeMeaning, VR.LO, "Finding");
    textName.add(finding);
    text.setString(Tag.TextValue, VR.UT, "No acute process");
    content.add(text);
    Attributes num = new Attributes();
    num.setString(Tag.RelationshipType, VR.CS, "CONTAINS");
    num.setString(Tag.ValueType, VR.CS, "NUM");
    Sequence numName = num.newSequence(Tag.ConceptNameCodeSequence, 1);
    Attributes length = new Attributes();
    length.setString(Tag.CodeMeaning, VR.LO, "Length");
    numName.add(length);
    Sequence measured = num.newSequence(Tag.MeasuredValueSequence, 1);
    Attributes value = new Attributes();
    value.setString(Tag.NumericValue, VR.DS, "12.5");
    Sequence units = value.newSequence(Tag.MeasurementUnitsCodeSequence, 1);
    Attributes mm = new Attributes();
    mm.setString(Tag.CodeMeaning, VR.LO, "mm");
    units.add(mm);
    measured.add(value);
    content.add(num);
    return sr;
  }
}
