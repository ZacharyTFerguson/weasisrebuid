/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/** One Channel Definition Sequence item. */
public class ChannelDefinition {

  private final int index;
  private final String label;
  private final Lead lead;
  private final double sensitivity;
  private final double correctionFactor;
  private final double baseline;
  private final Unit unit;

  public ChannelDefinition(
      int index,
      String label,
      Lead lead,
      double sensitivity,
      double correctionFactor,
      double baseline,
      Unit unit) {
    this.index = index;
    this.label = label == null || label.isBlank() ? "CH" + index : label;
    this.lead = lead == null ? Lead.UNKNOWN : lead;
    this.sensitivity = sensitivity;
    this.correctionFactor = correctionFactor == 0 ? 1.0 : correctionFactor;
    this.baseline = baseline;
    this.unit = unit == null ? new Unit(Unit.MILLIVOLT) : unit;
  }

  public static ChannelDefinition from(Attributes item, int index) {
    Attributes src = item == null ? new Attributes() : item;
    String label = src.getString(Tag.ChannelLabel, "");
    if (label.isBlank()) {
      label = codeMeaning(src.getSequence(Tag.ChannelSourceSequence));
    }
    double sensitivity = src.getDouble(Tag.ChannelSensitivity, 1.0);
    double corr = src.getDouble(Tag.ChannelSensitivityCorrectionFactor, 1.0);
    double baseline = src.getDouble(Tag.ChannelBaseline, 0);
    String unitCode = codeMeaning(src.getSequence(Tag.ChannelSensitivityUnitsSequence));
    return new ChannelDefinition(
        index, label, Lead.fromLabel(label), sensitivity, corr, baseline, new Unit(unitCode));
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

  public int index() {
    return index;
  }

  public String label() {
    return label;
  }

  public Lead lead() {
    return lead;
  }

  public double sensitivity() {
    return sensitivity;
  }

  public Unit unit() {
    return unit;
  }

  public double toMillivolt(int rawSample) {
    double scaled = (rawSample - baseline) * sensitivity * correctionFactor;
    return unit.toMillivolt(scaled);
  }
}
