/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr.cmpr;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.weasis.dicom.viewer2d.mpr.Volume;

/** Samples a volume along a curve (CPR). */
public class CurvedMprBuilder {

  public double[] sample(Volume volume, List<Point2D.Double> curve, int z) {
    if (volume == null || curve == null || curve.isEmpty()) {
      return new double[0];
    }
    List<Double> values = new ArrayList<>();
    for (Point2D.Double p : curve) {
      values.add(volume.value((int) Math.round(p.x), (int) Math.round(p.y), z));
    }
    double[] out = new double[values.size()];
    for (int i = 0; i < values.size(); i++) {
      out[i] = values.get(i);
    }
    return out;
  }
}
