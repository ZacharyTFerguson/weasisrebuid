/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

/**
 * SUVbw factor for PET ({@code BQML} units). SUV = stored × factor. Tagged-equivalent of {@code
 * SuvFactor}.
 */
public final class SuvFactor {

  private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  private SuvFactor() {}

  public static double factor(Attributes dcm) {
    if (dcm == null) {
      return 1.0;
    }
    String units = dcm.getString(Tag.Units, "");
    if (!"BQML".equalsIgnoreCase(units)) {
      return 1.0;
    }
    double weightKg = dcm.getDouble(Tag.PatientWeight, 0);
    Attributes radio = firstSequenceItem(dcm, Tag.RadiopharmaceuticalInformationSequence);
    if (radio == null || weightKg <= 0) {
      return 1.0;
    }
    double doseBq = radio.getDouble(Tag.RadionuclideTotalDose, 0);
    double halfLife = radio.getDouble(Tag.RadionuclideHalfLife, 0);
    if (doseBq <= 0) {
      return 1.0;
    }
    double decayed = doseBq;
    if (halfLife > 0) {
      LocalDateTime start = parse(radio.getString(Tag.RadiopharmaceuticalStartDateTime, null));
      if (start == null) {
        start =
            parse(
                dcm.getString(Tag.SeriesDate, "")
                    + trimTime(radio.getString(Tag.RadiopharmaceuticalStartTime, "000000")));
      }
      LocalDateTime acq = parse(dcm.getString(Tag.AcquisitionDateTime, null));
      if (acq == null) {
        acq =
            parse(
                dcm.getString(Tag.AcquisitionDate, "")
                    + trimTime(dcm.getString(Tag.AcquisitionTime, "000000")));
      }
      if (start != null && acq != null) {
        double seconds = ChronoUnit.MILLIS.between(start, acq) / 1000.0;
        decayed = doseBq * Math.pow(2.0, -seconds / halfLife);
      }
    }
    if (decayed <= 0) {
      return 1.0;
    }
    return (weightKg * 1000.0) / decayed;
  }

  static Attributes firstSequenceItem(Attributes dcm, int tag) {
    var seq = dcm.getSequence(tag);
    if (seq == null || seq.isEmpty()) {
      return null;
    }
    return seq.get(0);
  }

  static String trimTime(String time) {
    if (time == null || time.isBlank()) {
      return "000000";
    }
    String digits = time.replace(":", "");
    if (digits.length() >= 6) {
      return digits.substring(0, 6);
    }
    return (digits + "000000").substring(0, 6);
  }

  static LocalDateTime parse(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    String digits = raw.replaceAll("[^0-9]", "");
    if (digits.length() < 14) {
      return null;
    }
    try {
      return LocalDateTime.parse(digits.substring(0, 14), DT);
    } catch (DateTimeParseException e) {
      return null;
    }
  }
}
