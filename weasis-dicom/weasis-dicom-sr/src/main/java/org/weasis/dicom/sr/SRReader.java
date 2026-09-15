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

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/** Walks DICOM SR Content Sequence into {@link SRDocumentContent} nodes. */
public class SRReader {

  public SRDocumentContentModule read(Attributes dataset) {
    SRDocumentContentModule module = new SRDocumentContentModule();
    if (dataset == null) {
      return module;
    }
    module.setTitle(codeMeaning(dataset.getSequence(Tag.ConceptNameCodeSequence)));
    Sequence content = dataset.getSequence(Tag.ContentSequence);
    if (content != null) {
      for (Attributes item : content) {
        module.add(readItem(item));
      }
    }
    return module;
  }

  public String displayText(Attributes dataset) {
    return read(dataset).asText();
  }

  SRDocumentContent readItem(Attributes item) {
    SRDocumentContent node = new SRDocumentContent();
    if (item == null) {
      return node;
    }
    node.setValueType(item.getString(Tag.ValueType, ""));
    node.setRelationshipType(item.getString(Tag.RelationshipType, ""));
    node.setConceptName(codeMeaning(item.getSequence(Tag.ConceptNameCodeSequence)));
    node.setValue(valueOf(item, node.getValueType()));
    Sequence nested = item.getSequence(Tag.ContentSequence);
    if (nested != null) {
      for (Attributes child : nested) {
        node.add(readItem(child));
      }
    }
    return node;
  }

  static String valueOf(Attributes item, String valueType) {
    String type = valueType == null ? "" : valueType;
    return switch (type) {
      case "TEXT" -> item.getString(Tag.TextValue, "");
      case "NUM" -> numericValue(item);
      case "CODE" -> codeMeaning(item.getSequence(Tag.ConceptCodeSequence));
      case "PNAME" -> item.getString(Tag.PersonName, "");
      case "DATE" -> item.getString(Tag.Date, "");
      case "TIME" -> item.getString(Tag.Time, "");
      case "DATETIME" -> item.getString(Tag.DateTime, "");
      case "UIDREF" -> item.getString(Tag.UID, "");
      case "IMAGE", "COMPOSITE", "WAVEFORM" -> referencedSop(item);
      default -> "";
    };
  }

  static String numericValue(Attributes item) {
    Sequence measured = item.getSequence(Tag.MeasuredValueSequence);
    if (measured == null || measured.isEmpty()) {
      return item.getString(Tag.NumericValue, "");
    }
    Attributes first = measured.get(0);
    String number = first.getString(Tag.NumericValue, "");
    String unit = codeMeaning(first.getSequence(Tag.MeasurementUnitsCodeSequence));
    if (unit.isBlank()) {
      return number;
    }
    return number + " " + unit;
  }

  static String referencedSop(Attributes item) {
    Sequence refs = item.getSequence(Tag.ReferencedSOPSequence);
    if (refs == null || refs.isEmpty()) {
      return item.getString(Tag.ReferencedSOPInstanceUID, "");
    }
    return refs.get(0).getString(Tag.ReferencedSOPInstanceUID, "");
  }

  static String codeMeaning(Sequence sequence) {
    if (sequence == null || sequence.isEmpty()) {
      return "";
    }
    Attributes code = sequence.get(0);
    String meaning = code.getString(Tag.CodeMeaning, "");
    if (!meaning.isBlank()) {
      return meaning;
    }
    return code.getString(Tag.CodeValue, "");
  }
}
