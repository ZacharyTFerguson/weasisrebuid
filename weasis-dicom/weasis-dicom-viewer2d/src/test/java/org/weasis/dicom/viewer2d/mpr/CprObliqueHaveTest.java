/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer2d.mpr.cmpr.CrossSectionDialog;
import org.weasis.dicom.viewer2d.mpr.cmpr.CrossSectionParams;
import org.weasis.dicom.viewer2d.mpr.cmpr.CurveSampler;
import org.weasis.dicom.viewer2d.mpr.cmpr.CurvedMprAxis;
import org.weasis.dicom.viewer2d.mpr.cmpr.CurvedMprBuilder;
import org.weasis.dicom.viewer2d.mpr.cmpr.CurvedMprView;

class CprObliqueHaveTest {

  @Test
  void resamplesPolylineToUniformArcLength() {
    List<Point2D.Double> poly =
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
    CurveSampler sampler = new CurveSampler();
    assertEquals(20.0, sampler.length(poly), 1e-9);
    List<Point2D.Double> even = sampler.resample(poly, 5);
    assertEquals(5, even.size());
    assertEquals(0.0, even.get(0).distance(new Point2D.Double(0, 0)), 1e-9);
    assertEquals(0.0, even.get(2).distance(new Point2D.Double(10, 0)), 1e-9);
    assertEquals(0.0, even.get(4).distance(new Point2D.Double(10, 10)), 1e-9);
    Point2D.Double mid = sampler.atParameter(poly, 0.25);
    assertEquals(5.0, mid.x, 1e-9);
    assertEquals(0.0, mid.y, 1e-9);
  }

  @Test
  void straightenedCprPutsDiagonalVesselInCenterColumn() {
    VolumeShort vol = new VolumeShort(16, 16, 8);
    int z = 3;
    for (int i = 2; i <= 12; i++) {
      vol.setValue(i, i, z, 77);
    }
    List<Point2D.Double> curve = List.of(new Point2D.Double(2, 2), new Point2D.Double(12, 12));
    double[][] cpr = new CurvedMprBuilder().build(vol, curve, z, 3);
    assertTrue(cpr.length > 4);
    assertEquals(7, cpr[0].length);
    int hits = 0;
    for (double[] row : cpr) {
      if (row[3] == 77.0) {
        hits++;
      }
      assertEquals(0.0, row[0]);
      assertEquals(0.0, row[6]);
    }
    assertTrue(hits >= cpr.length - 2);
    CurvedMprAxis axis = new CurvedMprAxis();
    axis.setCurve(curve);
    axis.setZ(z);
    axis.setHalfWidth(3);
    CurvedMprView view = new CurvedMprView();
    view.setCurvedAxis(axis);
    double[][] fromView = view.rebuild(vol);
    assertEquals(cpr.length, fromView.length);
    assertEquals(77.0, fromView[fromView.length / 2][3]);
  }

  @Test
  void obliqueAlignedPlaneMatchesOrthogonalAxial() {
    VolumeShort vol = new VolumeShort(4, 5, 6);
    vol.setValue(2, 3, 4, 42);
    double[][] axial = MPRGenerator.orthogonal(vol, MprAxis.AXIAL, 4);
    AxesControl axes = new AxesControl();
    axes.setOrigin(0, 0, 4);
    axes.setU(new AxisDirection(1, 0, 0));
    axes.setV(new AxisDirection(0, 1, 0));
    double[][] oblique = new ObliqueMpr().slice(vol, axes, 4, 5);
    assertEquals(5, oblique.length);
    assertEquals(4, oblique[0].length);
    assertEquals(42.0, axial[3][2]);
    assertEquals(42.0, oblique[3][2]);
    AxisDirection n = axes.normal();
    assertEquals(0.0, n.x(), 1e-9);
    assertEquals(0.0, n.y(), 1e-9);
    assertEquals(1.0, n.z(), 1e-9);
  }

  @Test
  void crossSectionPerpendicularToCurveHitsVesselCenter() {
    VolumeShort vol = new VolumeShort(16, 16, 8);
    int z = 4;
    for (int y = 1; y < 15; y++) {
      vol.setValue(8, y, z, 90);
    }
    List<Point2D.Double> curve = List.of(new Point2D.Double(8, 1), new Point2D.Double(8, 14));
    CrossSectionParams params = new CrossSectionParams();
    params.setCurve(curve);
    params.setZ(z);
    params.setT(0.5);
    params.setWidth(5);
    params.setHeight(5);
    CrossSectionDialog dialog = new CrossSectionDialog();
    dialog.setParams(params);
    double[][] plane = dialog.preview(vol);
    assertEquals(5, plane.length);
    assertEquals(5, plane[0].length);
    assertEquals(90.0, plane[2][2]);
    assertEquals(0.0, plane[2][0]);
    assertEquals(0.0, plane[0][2]);
  }
}
