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
import org.weasis.dicom.codec.PhotometricInterpretation;
import org.weasis.dicom.geom.ImageOrientation;
import org.weasis.dicom.geom.Vector3;

/** Helpers over a Part-10 dataset (tagged-equivalent of {@code DicomMediaUtils}). */
public final class DicomMediaUtils {

  private DicomMediaUtils() {}

  public static boolean isSignedPixel(Attributes dcm) {
    return dcm != null && dcm.getInt(Tag.PixelRepresentation, 0) == 1;
  }

  /**
   * Interprets a dcm4che {@code getInts(PixelData)} sample using {@code PixelRepresentation}, {@code
   * BitsAllocated}, and {@code BitsStored}. Unsigned 8-bit values must not sign-wrap.
   */
  public static int storedPixel(Attributes dcm, int raw) {
    int allocated = dcm == null ? 16 : dcm.getInt(Tag.BitsAllocated, 16);
    int stored = dcm == null ? allocated : dcm.getInt(Tag.BitsStored, allocated);
    if (stored <= 0) {
      stored = allocated <= 0 ? 16 : allocated;
    }
    if (stored > 32) {
      stored = 32;
    }
    int mask = stored >= 32 ? 0xffffffff : (1 << stored) - 1;
    if (!isSignedPixel(dcm)) {
      int unsigned = allocated <= 8 ? raw & 0xff : raw & 0xffff;
      return unsigned & mask;
    }
    int u = raw & mask;
    int sign = 1 << (stored - 1);
    if ((u & sign) != 0) {
      return u | ~mask;
    }
    return u;
  }

  public static String photometricInterpretation(Attributes dcm) {
    return dcm == null ? "" : dcm.getString(Tag.PhotometricInterpretation, "");
  }

  public static WindLevelParameters windowLevel(
      Attributes dcm, double defaultWindow, double defaultLevel) {
    if (dcm == null) {
      return new WindLevelParameters(defaultWindow, defaultLevel);
    }
    if (dcm.containsValue(Tag.WindowWidth) && dcm.containsValue(Tag.WindowCenter)) {
      double window = dcm.getDouble(Tag.WindowWidth, defaultWindow);
      double level = dcm.getDouble(Tag.WindowCenter, defaultLevel);
      if (window <= 0) {
        window = defaultWindow;
      }
      return new WindLevelParameters(window, level);
    }
    WindLevelParameters fromData = dataRangeWindowLevel(dcm);
    if (fromData != null) {
      return fromData;
    }
    return new WindLevelParameters(defaultWindow, defaultLevel);
  }

  static WindLevelParameters dataRangeWindowLevel(Attributes dcm) {
    int[] pixels = dcm.getInts(Tag.PixelData);
    if (pixels == null || pixels.length == 0) {
      return null;
    }
    int pad = pixelPaddingValue(dcm);
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int raw : pixels) {
      int stored = storedPixel(dcm, raw);
      if (stored == pad) {
        continue;
      }
      double modality = LutPipeline.modalityValue(dcm, stored);
      if (modality < min) {
        min = modality;
      }
      if (modality > max) {
        max = modality;
      }
    }
    if (!Double.isFinite(min) || !Double.isFinite(max)) {
      return null;
    }
    double window = Math.max(1.0, max - min);
    double level = min + window / 2.0;
    return new WindLevelParameters(window, level);
  }

  public static int pixelPaddingValue(Attributes dcm) {
    if (dcm == null || !dcm.contains(Tag.PixelPaddingValue)) {
      return Integer.MIN_VALUE;
    }
    return dcm.getInt(Tag.PixelPaddingValue, Integer.MIN_VALUE);
  }

  public static boolean isMonochrome2(Attributes dcm) {
    return PhotometricInterpretation.MONOCHROME2
        == PhotometricInterpretation.parse(photometricInterpretation(dcm)).orElse(null);
  }

  /**
   * Uses weasis-dicom-tools {@link ImageOrientation} when row/col direction cosines are present.
   */
  public static String planLabel(Attributes dcm) {
    if (dcm == null || !dcm.contains(Tag.ImageOrientationPatient)) {
      return "";
    }
    double[] iop = dcm.getDoubles(Tag.ImageOrientationPatient);
    if (iop == null || iop.length < 6) {
      return "";
    }
    Vector3 row = new Vector3(iop[0], iop[1], iop[2]);
    Vector3 col = new Vector3(iop[3], iop[4], iop[5]);
    return ImageOrientation.getPlan(row, col).name();
  }
}
