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

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.util.WindLevelParameters;

/**
 * LUT pipeline (ARCHITECTURE §6.2): stored → Modality (rescale) → VOI linear → 8-bit. Pixel padding
 * is excluded from auto-window.
 */
public final class LutPipeline {

  public static final String SHAPE_LINEAR = "LINEAR";
  public static final String SHAPE_SIGMOID = "SIGMOID";
  public static final String SHAPE_LOG = "LOG";
  public static final String SHAPE_NON_LINEAR = "NON_LINEAR";

  private LutPipeline() {}

  public static double applyModality(double stored, double slope, double intercept) {
    return stored * slope + intercept;
  }

  public static double modalityValue(Attributes dcm, double stored) {
    if (dcm == null) {
      return stored;
    }
    double slope = dcm.getDouble(Tag.RescaleSlope, 1.0);
    double intercept = dcm.getDouble(Tag.RescaleIntercept, 0.0);
    return applyModality(stored, slope, intercept);
  }

  public static int applyVoiLinear(double modality, double window, double level) {
    if (window <= 0) {
      return 0;
    }
    double low = level - window / 2.0;
    double n = (modality - low) / window;
    int v = (int) Math.round(n * 255.0);
    if (v < 0) {
      return 0;
    }
    if (v > 255) {
      return 255;
    }
    return v;
  }

  public static int applyPresentationIdentity(int voi8) {
    return voi8;
  }

  public static WindLevelParameters autoWindowExcludingPadding(int[] stored, int paddingSentinel) {
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (int v : stored) {
      if (v == paddingSentinel) {
        continue;
      }
      if (v < min) {
        min = v;
      }
      if (v > max) {
        max = v;
      }
    }
    if (min > max) {
      return new WindLevelParameters(1, 0);
    }
    double window = Math.max(1.0, (max - min));
    double level = min + window / 2.0;
    return new WindLevelParameters(window, level);
  }
}
