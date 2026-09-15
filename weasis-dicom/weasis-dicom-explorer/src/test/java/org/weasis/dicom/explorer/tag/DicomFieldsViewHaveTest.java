/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.explorer.tag.AbstractTagSearchPanel.TagRow;

class DicomFieldsViewHaveTest {

  @Test
  void limitedModeOmitsPixelDataAndKeepsClinicalTags() {
    DicomFieldsView view = new DicomFieldsView();
    view.changeDicomInfo(sample());
    assertTrue(view.isLimited());
    assertEquals("DICOM Fields", view.getName());
    List<TagRow> rows = view.allItems();
    assertTrue(containsKeyword(rows, "PatientID"));
    assertTrue(containsKeyword(rows, "SOPInstanceUID"));
    assertTrue(containsKeyword(rows, "Modality"));
    assertFalse(containsKeyword(rows, "PixelData"));
    assertTrue(valueOf(rows, "PatientID").contains("P-42"));
    assertEquals(view.tablePanel().rowCount(), rows.size());
    assertTrue(view.documentPanel().plainText().contains("P-42"));
    assertFalse(view.documentPanel().plainText().contains("PixelData"));
  }

  @Test
  void allModeListsPixelDataAsBulkAndExpandsSequence() {
    DicomFieldsView view = new DicomFieldsView();
    view.changeDicomInfo(sample());
    view.setLimited(false);
    List<TagRow> rows = view.allItems();
    assertTrue(containsKeyword(rows, "PixelData"));
    assertTrue(valueOf(rows, "PixelData").startsWith("["));
    assertFalse(valueOf(rows, "PixelData").contains("1\\2\\3\\4"));
    assertTrue(containsKeyword(rows, "ReferencedImageSequence"));
    assertTrue(containsKeyword(rows, "ReferencedSOPInstanceUID"));
    assertTrue(valueOf(rows, "ReferencedSOPInstanceUID").contains("9.9.9"));
  }

  @Test
  void searchFiltersTableAndDocumentByKeywordHexAndValue() {
    DicomFieldsView view = new DicomFieldsView();
    view.changeDicomInfo(sample());
    view.setLimited(false);
    view.setQuery("Patient");
    List<TagRow> filtered = view.visibleItems();
    assertFalse(filtered.isEmpty());
    assertTrue(filtered.stream().allMatch(r -> AbstractTagSearchPanel.matches(r, "Patient")));
    assertTrue(containsKeyword(filtered, "PatientID"));
    assertTrue(containsKeyword(filtered, "PatientName"));
    assertFalse(containsKeyword(filtered, "Modality"));
    assertEquals(filtered.size(), view.tablePanel().rowCount());
    assertTrue(view.documentPanel().plainText().contains("PatientID"));
    assertFalse(view.documentPanel().plainText().contains("Modality"));

    view.setQuery("(0010,0020)");
    assertEquals(1, view.visibleItems().size());
    assertEquals("PatientID", view.visibleItems().get(0).keyword());

    view.setQuery("P-42");
    assertEquals(1, view.visibleItems().size());
    assertEquals("P-42", view.visibleItems().get(0).value());

    view.setQuery("");
    assertTrue(view.visibleItems().size() > 3);
  }

  @Test
  void standaloneTableAndDocumentPanelsShareFilterContract() {
    List<TagRow> rows =
        List.of(
            new TagRow(Tag.PatientID, "PatientID", "(0010,0020)", "LO", "A", 0),
            new TagRow(Tag.Modality, "Modality", "(0008,0060)", "CS", "CT", 0));
    TagSearchTablePanel table = new TagSearchTablePanel();
    TagSearchDocumentPanel doc = new TagSearchDocumentPanel();
    table.setItems(rows);
    doc.setItems(rows);
    table.setQuery("ct");
    doc.setQuery("ct");
    assertEquals(1, table.rowCount());
    assertEquals("Modality", table.valueAt(0, 0).trim());
    assertTrue(doc.plainText().contains("Modality"));
    assertFalse(doc.plainText().contains("PatientID"));
    assertTrue(doc.html().contains("Modality"));
    assertFalse(AbstractTagSearchPanel.matches(null, "x"));
  }

  static Attributes sample() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, "1.2.3.4.5");
    dcm.setString(Tag.PatientName, VR.PN, "SYNTHETIC^A");
    dcm.setString(Tag.PatientID, VR.LO, "P-42");
    dcm.setString(Tag.Modality, VR.CS, "CT");
    dcm.setInt(Tag.Rows, VR.US, 2);
    dcm.setInt(Tag.Columns, VR.US, 2);
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.PixelData, VR.OW, 1, 2, 3, 4);
    Sequence refs = dcm.newSequence(Tag.ReferencedImageSequence, 1);
    Attributes item = new Attributes();
    item.setString(Tag.ReferencedSOPClassUID, VR.UI, UID.CTImageStorage);
    item.setString(Tag.ReferencedSOPInstanceUID, VR.UI, "9.9.9");
    refs.add(item);
    return dcm;
  }

  static boolean containsKeyword(List<TagRow> rows, String keyword) {
    return rows.stream().anyMatch(r -> keyword.equals(r.keyword()));
  }

  static String valueOf(List<TagRow> rows, String keyword) {
    return rows.stream()
        .filter(r -> keyword.equals(r.keyword()))
        .map(TagRow::value)
        .findFirst()
        .orElse("");
  }
}
