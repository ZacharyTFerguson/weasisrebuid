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

public final class Ultrasound {
  private Ultrasound() {}

  public static boolean isUltrasound(Attributes dcm) {
    if (dcm == null) {
      return false;
    }
    String m = dcm.getString(Tag.Modality, "");
    return "US".equalsIgnoreCase(m);
  }

  public static double[] regionPixelSpacing(Attributes dcm) {
    if (dcm == null) {
      return new double[] {1.0, 1.0};
    }
    double[] ps = dcm.getDoubles(Tag.PixelSpacing);
    if (ps != null && ps.length >= 2) {
      return ps;
    }
    return new double[] {1.0, 1.0};
  }
}
