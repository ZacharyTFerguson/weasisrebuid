/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

import java.util.List;

/** Selects the contour with the most points (typical “largest” ROI slice). */
public class LargestContour {

  public static StructContour of(List<StructContour> contours) {
    if (contours == null || contours.isEmpty()) {
      return null;
    }
    StructContour best = contours.get(0);
    for (StructContour contour : contours) {
      if (contour.pointCount() > best.pointCount()) {
        best = contour;
      }
    }
    return best;
  }
}
