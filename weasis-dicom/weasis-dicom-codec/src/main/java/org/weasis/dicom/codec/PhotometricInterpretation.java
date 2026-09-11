/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/** SRS §4.13 photometric catalog — exactly these ten. */
public enum PhotometricInterpretation {
  MONOCHROME1,
  MONOCHROME2,
  PALETTE_COLOR("PALETTE COLOR"),
  RGB,
  YBR_FULL,
  YBR_FULL_422,
  YBR_PARTIAL_422,
  YBR_PARTIAL_420,
  YBR_ICT,
  YBR_RCT;

  private final String dicomCode;

  PhotometricInterpretation() {
    this.dicomCode = name();
  }

  PhotometricInterpretation(String dicomCode) {
    this.dicomCode = dicomCode;
  }

  public String dicomCode() {
    return dicomCode;
  }

  public boolean isMonochrome() {
    return this == MONOCHROME1 || this == MONOCHROME2;
  }

  public static Optional<PhotometricInterpretation> parse(String raw) {
    if (raw == null || raw.isBlank()) {
      return Optional.empty();
    }
    String code = raw.trim().toUpperCase(Locale.ROOT);
    return Arrays.stream(values()).filter(p -> p.dicomCode.equals(code)).findFirst();
  }
}
