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

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

public final class ImageOrientation {
  private ImageOrientation() {}

  public static Orientation getOrientation(Attributes dcm) {
    if (dcm == null) {
      return Orientation.UNKNOWN;
    }
    return getOrientation(dcm.getDoubles(Tag.ImageOrientationPatient));
  }

  public static Orientation getOrientation(double[] iop) {
    if (iop == null || iop.length < 6) {
      return Orientation.UNKNOWN;
    }
    double[] row = VectorUtils.normalize(new double[] {iop[0], iop[1], iop[2]});
    double[] col = VectorUtils.normalize(new double[] {iop[3], iop[4], iop[5]});
    double[] normal = VectorUtils.normalize(VectorUtils.cross(row, col));
    double ax = Math.abs(normal[0]);
    double ay = Math.abs(normal[1]);
    double az = Math.abs(normal[2]);
    if (az >= ax && az >= ay && az > 0.8) {
      return Orientation.AXIAL;
    }
    if (ax >= ay && ax >= az && ax > 0.8) {
      return Orientation.SAGITTAL;
    }
    if (ay >= ax && ay >= az && ay > 0.8) {
      return Orientation.CORONAL;
    }
    return Orientation.OBLIQUE;
  }
}
