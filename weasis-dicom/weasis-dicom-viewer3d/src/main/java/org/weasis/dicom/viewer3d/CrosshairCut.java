/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d;

/** No cut plus 18 directional half/quarter/eighth clips (L/R, A/P, S/I). */
public enum CrosshairCut {
  NONE,
  HALF_L,
  HALF_R,
  HALF_A,
  HALF_P,
  HALF_S,
  HALF_I,
  QUARTER_LA,
  QUARTER_LP,
  QUARTER_RA,
  QUARTER_RP,
  QUARTER_LS,
  QUARTER_RS,
  QUARTER_AS,
  QUARTER_AI,
  EIGHTH_LAS,
  EIGHTH_LAI,
  EIGHTH_LPS,
  EIGHTH_RAS;

  public static int directionalCount() {
    return values().length - 1;
  }
}
