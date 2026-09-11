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

  public static String photometricInterpretation(Attributes dcm) {
    return dcm == null ? "" : dcm.getString(Tag.PhotometricInterpretation, "");
  }

  public static WindLevelParameters windowLevel(
      Attributes dcm, double defaultWindow, double defaultLevel) {
    if (dcm == null) {
      return new WindLevelParameters(defaultWindow, defaultLevel);
    }
    double window = dcm.getDouble(Tag.WindowWidth, defaultWindow);
    double level = dcm.getDouble(Tag.WindowCenter, defaultLevel);
    if (window <= 0) {
      window = defaultWindow;
    }
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
