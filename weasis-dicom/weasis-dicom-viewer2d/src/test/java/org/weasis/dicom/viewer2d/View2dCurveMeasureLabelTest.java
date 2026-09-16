/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.imp.line.CurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * View2d open-curve measurement labels: same binding rules as {@link View2dPolylineMeasureLabelTest},
 * but millimetres come from {@link CurveGraphic#getLengthMm} on the sampled interpolating path — not
 * from vertex-to-vertex polyline sums.
 *
 * <p><b>Why UI after {@code getLengthMm}:</b> {@link MeasurementLabel#formatCurve} only formats values
 * already proved in {@link org.weasis.core.ui.model.graphic.imp.line.CurveGraphicMmTest} and instance
 * spacing in {@link org.weasis.dicom.codec.utils.InstanceSpacingTest}. A label that recomputed mm from
 * handle chords or reused {@link PolylineGraphic#getLengthMm} would disagree with the landed curve API.
 *
 * <p><b>Why bind to {@code Resolved} + {@link CurveGraphic#getLengthMm} only:</b> spacing comes from
 * {@link org.weasis.dicom.codec.utils.InstanceSpacing.Resolved#spacing()} on the current {@link View2d}
 * dataset; the formatter has no {@code BufferedImage}, no {@link View2d}, and no dataset parameter — it
 * cannot read paint or re-resolve tags when window/level changes.
 *
 * <p><b>Why {@code px} never coexists with {@code mm}:</b> when resolve is empty or spacing is unusable,
 * the label uses sampled pixel path length only; no imager fallback on CT and no dual unit string.
 *
 * <p><b>Why the DX warning travels with the number:</b> {@code (detector plane)} / {@code (estimate)}
 * suffixes follow {@link org.weasis.dicom.codec.utils.InstanceSpacing.Source} on the same {@code Resolved}
 * value as line and polyline labels.
 *
 * <p><b>Why smooth path, not handle polyline:</b> an L-shaped handle triple yields a longer smooth corner
 * than the broken-line path through the same handles — the primary discriminating assert is {@link
 * #curveLabelDiffersFromPolylineForLShapedPath}.
 *
 * <p><b>Why not copy Weasis:</b> upstream spline measure tools mix interactive draw, closure, and paint
 * buffers; this slice adds {@code formatCurve} beside existing line/polyline formatters without porting
 * draw handlers or {@code MouseActions} tokens.
 */
class View2dCurveMeasureLabelTest {

  @Test
  void curveLabelOnSyntheticCtUsesInstanceSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct_iso_050.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    CurveGraphic curve = horizontalCurve(10);
    assertEquals("5.0 mm", view.formatCurveMeasureLabel(curve));
  }

  @Test
  void curveLabelAfterPagingFollowsNewInstance(@TempDir Path dir) throws Exception {
    Path pack = View2dMeasureLabelTest.roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File s01 = pack.resolve("ct_brain_ax_s01_256.dcm").toFile();
    Assumptions.assumeTrue(s01.isFile());
    String seriesUid;
    try (org.dcm4che3.io.DicomInputStream in = new org.dcm4che3.io.DicomInputStream(s01)) {
      seriesUid = in.readDataset(-1, -1).getString(org.dcm4che3.data.Tag.SeriesInstanceUID);
    }
    File iso =
        StackPagingFixtures.writeCtInstance(
            dir.resolve("iso050.dcm").toFile(), seriesUid, 2, 0.50, 0.50, 5.0, 100);

    View2d view = new View2d();
    view.loadStack(java.util.List.of(s01, iso));
    CurveGraphic curve = horizontalCurve(10);
    assertEquals("8.0 mm", view.formatCurveMeasureLabel(curve));

    view.setFrameIndex(1);
    assertEquals("5.0 mm", view.formatCurveMeasureLabel(curve));
  }

  @Test
  void curveLabelOnDxCarriesDetectorWarning(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxImager020(dir.resolve("dx_imager_020.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    assertEquals("2.0 mm (detector plane)", view.formatCurveMeasureLabel(horizontalCurve(10)));
  }

  @Test
  void curveLabelOnDxEstimateCarriesCaveat(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxErmf12(dir.resolve("dx_ermf_12.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    String label = view.formatCurveMeasureLabel(horizontalCurve(10));
    assertTrue(label.contains("1.25 mm"));
    assertTrue(label.toLowerCase().contains("estimate"));
  }

  @Test
  void curveLabelWithoutSpacingIsPixelsOnly(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("ct_nospacing.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    CurveGraphic curve = lShapeCurve();
    String label = view.formatCurveMeasureLabel(curve);
    assertTrue(label.endsWith(" px"));
    assertFalse(label.toLowerCase().contains("mm"));
  }

  @Test
  void curveLabelDiffersFromPolylineForLShapedPath(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    PolylineGraphic poly = polyline(p0, p1, p2);
    CurveGraphic curve = curve(p0, p1, p2);
    assertEquals("10.0 mm", view.formatPolylineMeasureLabel(poly));
    assertNotEquals(
        view.formatPolylineMeasureLabel(poly), view.formatCurveMeasureLabel(curve));
  }

  @Test
  void manualCalibrationDoublesDoNotChangeCurveLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    CurveGraphic curve = horizontalCurve(10);
    String before = view.formatCurveMeasureLabel(curve);
    view.setMonitorCalibrationMmPerPixel(9.9);
    view.setSessionManualCalibrationMmPerPixel(9.9);
    assertEquals(before, view.formatCurveMeasureLabel(curve));
  }

  private static CurveGraphic horizontalCurve(double pixels) {
    return curve(
        new Point2D.Double(0, 0),
        new Point2D.Double(pixels / 2.0, 0),
        new Point2D.Double(pixels, 0));
  }

  private static CurveGraphic lShapeCurve() {
    return curve(
        new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
  }

  private static CurveGraphic curve(Point2D.Double... points) {
    CurveGraphic c = new CurveGraphic();
    for (int i = 0; i < points.length; i++) {
      c.setHandlePoint(i, points[i]);
    }
    return c;
  }

  private static PolylineGraphic polyline(Point2D.Double... points) {
    PolylineGraphic poly = new PolylineGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }
}
