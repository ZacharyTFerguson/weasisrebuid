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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/** One Contour Sequence item: CLOSED_PLANAR / POINT / OPEN_PLANAR in millimetres. */
public class StructContour {

  private final String geometricType;
  private final List<double[]> points;
  private final double z;
  private final String referencedSopInstanceUid;

  public StructContour(
      String geometricType, List<double[]> points, double z, String referencedSopInstanceUid) {
    this.geometricType = geometricType == null ? "" : geometricType;
    this.points = points == null ? List.of() : List.copyOf(points);
    this.z = z;
    this.referencedSopInstanceUid =
        referencedSopInstanceUid == null ? "" : referencedSopInstanceUid;
  }

  public static StructContour from(Attributes item) {
    Attributes src = item == null ? new Attributes() : item;
    String type = src.getString(Tag.ContourGeometricType, "");
    double[] data = src.getDoubles(Tag.ContourData);
    if (data == null) {
      data = new double[0];
    }
    int declared = src.getInt(Tag.NumberOfContourPoints, data.length / 3);
    int n = Math.min(declared, data.length / 3);
    List<double[]> pts = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      int o = i * 3;
      pts.add(new double[] {data[o], data[o + 1], data[o + 2]});
    }
    double z = pts.isEmpty() ? 0 : pts.get(0)[2];
    return new StructContour(type, pts, z, referencedSop(src));
  }

  static String referencedSop(Attributes item) {
    Sequence images = item.getSequence(Tag.ContourImageSequence);
    if (images == null || images.isEmpty()) {
      return item.getString(Tag.ReferencedSOPInstanceUID, "");
    }
    return images.get(0).getString(Tag.ReferencedSOPInstanceUID, "");
  }

  public String geometricType() {
    return geometricType;
  }

  public List<double[]> points() {
    return Collections.unmodifiableList(points);
  }

  public int pointCount() {
    return points.size();
  }

  public double z() {
    return z;
  }

  public String referencedSopInstanceUid() {
    return referencedSopInstanceUid;
  }

  public boolean closedPlanar() {
    return "CLOSED_PLANAR".equalsIgnoreCase(geometricType);
  }
}
