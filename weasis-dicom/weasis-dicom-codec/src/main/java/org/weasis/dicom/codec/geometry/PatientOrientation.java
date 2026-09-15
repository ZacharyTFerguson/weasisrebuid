/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.geometry;

public enum PatientOrientation {
  L,
  R,
  A,
  P,
  H,
  F,
  UNKNOWN;

  public static PatientOrientation opposite(PatientOrientation o) {
    return switch (o) {
      case L -> R;
      case R -> L;
      case A -> P;
      case P -> A;
      case H -> F;
      case F -> H;
      default -> UNKNOWN;
    };
  }
}
