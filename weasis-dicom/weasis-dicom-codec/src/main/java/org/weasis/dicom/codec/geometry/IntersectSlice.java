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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectSlice extends LocalizerPoster {
  public IntersectSlice(GeometryOfSlice localizerGeometry) {
    super(localizerGeometry);
  }

  @Override
  public List<Point2D.Double> getOutlineOnLocalizerForThisGeometry(GeometryOfSlice src) {
    List<Point2D.Double> pts = new ArrayList<>();
    if (src == null || localizerGeometry == null) {
      return pts;
    }
    double[] nLoc = localizerGeometry.getNormal();
    double[] nSrc = src.getNormal();
    if (Math.abs(VectorUtils.dot(nLoc, nSrc)) > 0.99) {
      return pts;
    }
    pts.add(new Point2D.Double(0, 0));
    pts.add(new Point2D.Double(src.getDimensions()[0], src.getDimensions()[1]));
    return pts;
  }
}
