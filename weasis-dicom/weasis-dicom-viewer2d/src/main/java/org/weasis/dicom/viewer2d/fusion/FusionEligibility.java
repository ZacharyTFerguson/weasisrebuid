/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

/**
 * Fusion controls stay disabled unless same StudyInstanceUID, ≥2 slices with IPP/IOP/Pixel Spacing,
 * and FoR match or overlap in patient space. No registration.
 */
public final class FusionEligibility {

  private FusionEligibility() {}

  public static boolean enabled(
      String studyA,
      String studyB,
      int slicesWithGeometry,
      String forA,
      String forB,
      boolean patientSpaceOverlap) {
    if (studyA == null || !studyA.equals(studyB)) {
      return false;
    }
    if (slicesWithGeometry < 2) {
      return false;
    }
    boolean forMatch = forA != null && forA.equals(forB);
    return forMatch || patientSpaceOverlap;
  }
}
