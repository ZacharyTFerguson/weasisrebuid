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
import org.weasis.dicom.viewer2d.mip.MipView;
import org.weasis.dicom.viewer2d.mpr.AxisDirection;
import org.weasis.dicom.viewer2d.mpr.Volume;

/** Samples the plane perpendicular to a CPR curve tangent. */
public class CrossSectionImageIO {

  public double[][] slice(Volume volume, CrossSectionParams params) {
    if (volume == null || params == null || params.getCurve().isEmpty()) {
      return new double[0][0];
    }
    CurveSampler sampler = new CurveSampler();
    Point2D.Double p = sampler.atParameter(params.getCurve(), params.getT());
    Point2D.Double tan = sampler.tangent(params.getCurve(), params.getT());
    AxisDirection tangent = new AxisDirection(tan.x, tan.y, 0).normalized();
    AxisDirection u = new AxisDirection(-tan.y, tan.x, 0).normalized();
    AxisDirection v = tangent.cross(u).normalized();
    int width = params.getWidth();
    int height = params.getHeight();
    int halfT = (params.getThickness() - 1) / 2;
    double[][] out = new double[height][width];
    double cx = (width - 1) / 2.0;
    double cy = (height - 1) / 2.0;
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        double di = i - cx;
        double dj = j - cy;
        out[j][i] =
            alongTangent(
                volume,
                p.x + di * u.x() + dj * v.x(),
                p.y + di * u.y() + dj * v.y(),
                params.getZ() + di * u.z() + dj * v.z(),
                tangent,
                halfT,
                params.getMip());
      }
    }
    return out;
  }

  private static double alongTangent(
      Volume volume,
      double x,
      double y,
      double z,
      AxisDirection tangent,
      int half,
      MipView.Type mip) {
    if (half <= 0 || mip == null || mip == MipView.Type.NONE) {
      return volume.sampleNearest(x, y, z);
    }
    if (mip == MipView.Type.MIN) {
      double m = Double.POSITIVE_INFINITY;
      for (int k = -half; k <= half; k++) {
        m = Math.min(m, sampleK(volume, x, y, z, tangent, k));
      }
      return m;
    }
    if (mip == MipView.Type.MAX) {
      double m = Double.NEGATIVE_INFINITY;
      for (int k = -half; k <= half; k++) {
        m = Math.max(m, sampleK(volume, x, y, z, tangent, k));
      }
      return m;
    }
    double s = 0;
    int n = 0;
    for (int k = -half; k <= half; k++) {
      s += sampleK(volume, x, y, z, tangent, k);
      n++;
    }
    return n == 0 ? 0 : s / n;
  }

  private static double sampleK(
      Volume volume, double x, double y, double z, AxisDirection tangent, int k) {
    return volume.sampleNearest(x + k * tangent.x(), y + k * tangent.y(), z + k * tangent.z());
  }
}
