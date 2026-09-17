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
 * LUT pipeline (ARCHITECTURE §6.2): stored → Modality (rescale) → VOI (linear / sigmoid / Sequence
 * table) → 8-bit. Pixel padding is excluded from auto-window.
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

  public static String voiFunction(Attributes dcm) {
    if (dcm == null) {
      return SHAPE_LINEAR;
    }
    return normalizeShape(dcm.getString(Tag.VOILUTFunction, SHAPE_LINEAR));
  }

  static String normalizeShape(String function) {
    if (function == null || function.isBlank()) {
      return SHAPE_LINEAR;
    }
    String u = function.trim().toUpperCase();
    return SHAPE_SIGMOID.equals(u) ? SHAPE_SIGMOID : SHAPE_LINEAR;
  }

  public static int applyVoi(double modality, WindLevelParameters voi) {
    if (voi == null) {
      return 0;
    }
    if (voi.hasVoiLut()) {
      return applyVoiLut(modality, voi.getVoiLutFirst(), voi.getVoiLut());
    }
    return applyVoiShape(modality, voi);
  }

  static int applyVoiShape(double modality, WindLevelParameters voi) {
    if (SHAPE_SIGMOID.equals(voi.getLutShape())) {
      return applyVoiSigmoid(modality, voi.getWindow(), voi.getLevel());
    }
    return applyVoiLinear(modality, voi.getWindow(), voi.getLevel());
  }

  public static int applyVoiLinear(double modality, double window, double level) {
    if (window <= 0) {
      return 0;
    }
    double low = level - window / 2.0;
    double n = (modality - low) / window;
    return clamp8((int) Math.round(n * 255.0));
  }

  /** DICOM VOI LUT Function SIGMOID: {@code 255 / (1 + exp(-4 * (x - center) / width))}. */
  public static int applyVoiSigmoid(double modality, double window, double level) {
    if (window <= 0) {
      return 0;
    }
    double n = 1.0 / (1.0 + Math.exp(-4.0 * (modality - level) / window));
    return clamp8((int) Math.round(n * 255.0));
  }

  /** VOI LUT Sequence table: stored/modality value → 8-bit display. */
  public static int applyVoiLut(double value, int firstMapped, int[] lut) {
    if (lut == null || lut.length == 0) {
      return 0;
    }
    int i = (int) Math.floor(value) - firstMapped;
    return lut[clampIndex(i, lut.length - 1)];
  }

  public static int clamp8(int v) {
    if (v < 0) {
      return 0;
    }
    if (v > 255) {
      return 255;
    }
    return v;
  }

  static int clampIndex(int i, int max) {
    if (i < 0) {
      return 0;
    }
    return i > max ? max : i;
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
