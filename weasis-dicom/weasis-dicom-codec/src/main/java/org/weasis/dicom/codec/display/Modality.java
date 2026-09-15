/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.display;

import java.util.Locale;

public enum Modality {
  AU,
  BI,
  CD,
  CR,
  CT,
  DD,
  DG,
  DX,
  ECG,
  EPS,
  ES,
  GM,
  HC,
  HD,
  IO,
  IVOCT,
  IVUS,
  KO,
  LS,
  MG,
  MR,
  NM,
  OT,
  PR,
  PT,
  PX,
  REG,
  RF,
  RG,
  RTDOSE,
  RTIMAGE,
  RTPLAN,
  RTRECORD,
  RTSTRUCT,
  SEG,
  SM,
  SMER,
  SR,
  ST,
  TG,
  US,
  XA,
  XC,
  DEFAULT;

  public static Modality getModality(String code) {
    if (code == null || code.isBlank()) {
      return DEFAULT;
    }
    try {
      return Modality.valueOf(code.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      return DEFAULT;
    }
  }
}
