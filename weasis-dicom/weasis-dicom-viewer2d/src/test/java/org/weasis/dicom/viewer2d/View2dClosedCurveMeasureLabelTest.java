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
import org.weasis.core.ui.model.graphic.imp.line.ClosedCurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.CurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;
import org.weasis.dicom.codec.utils.InstanceSpacing;

/**
 * View2d closed-curve measurement labels: same binding rules as {@link
 * View2dCurveMeasureLabelTest}, but millimetres come from {@link ClosedCurveGraphic#getLengthMm} on
 * the sampled closed interpolating path — not from an open curve length or vertex polyline sums.
 *
 * <p><b>Why UI after {@code getLengthMm}:</b> {@link MeasurementLabel#formatClosedCurve} only
 * formats values already proved in {@link
 * org.weasis.core.ui.model.graphic.imp.line.ClosedCurveGraphicMmTest} and instance spacing in {@link
 * org.weasis.dicom.codec.utils.InstanceSpacingTest}. A label that reused {@link
 * CurveGraphic#getLengthMm} or {@link PolylineGraphic#getLengthMm} on the same handles would
 * disagree with the landed closed-curve API.
 *
 * <p><b>Why bind to {@code Resolved} + {@link ClosedCurveGraphic#getLengthMm} only:</b> spacing
 * comes from {@link InstanceSpacing.Resolved#spacing()} on the current {@link View2d} dataset; the
 * formatter has no {@code BufferedImage}, no {@link View2d}, and no dataset parameter — it cannot
 * read paint or re-resolve tags when window/level changes.
 *
 * <p><b>Why {@code px} never coexists with {@code mm}:</b> when resolve is empty or spacing is
 * unusable, the label uses sampled closed pixel path length only; no imager fallback on CT and no
 * dual unit string.
 *
 * <p><b>Why the DX warning travels with the number:</b> {@code (detector plane)} / {@code
 * (estimate)} suffixes follow {@link org.weasis.dicom.codec.utils.InstanceSpacing.Source} on the
 * same {@code Resolved} value as line, polyline, and open-curve labels.
 *
 * <p><b>Why closed loop, not open spline or handle polyline:</b> the same handle triple yields a
 * longer closed smooth loop than either the open interpolating path (no wrap segment) or the broken
 * chord path — {@link #closedCurveLabelDiffersFromOpenCurveForSameHandles} and {@link
 * #closedCurveLabelDiffersFromPolylineForSameHandles} must fail if the formatter aliases those
 * labels.
 *
 * <p><b>Why not copy Weasis:</b> upstream closed-spline measure tools mix interactive draw,
 * closure, and paint buffers; this slice adds {@code formatClosedCurve} beside existing line /
 * polyline / open-curve formatters without porting draw handlers or {@code MouseActions} tokens.
 */
class View2dClosedCurveMeasureLabelTest {

  @Test
  void closedCurveLabelOnSyntheticCtUsesInstanceSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct_iso_050.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    ClosedCurveGraphic closed = lShapeClosed();
    String expected =
        MeasurementLabel.formatMm(
            closed
                .getLengthMm(
                    view.getResolvedInstanceSpacing()
                        .map(InstanceSpacing.Resolved::spacing)
                        .orElse(null))
                .orElseThrow());
    assertEquals(expected, view.formatClosedCurveMeasureLabel(closed));
  }

  @Test
  void closedCurveLabelAfterPagingFollowsNewInstance(@TempDir Path dir) throws Exception {
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
    ClosedCurveGraphic closed = lShapeClosed();
    String onS01 = view.formatClosedCurveMeasureLabel(closed);

    view.setFrameIndex(1);
    String onIso = view.formatClosedCurveMeasureLabel(closed);
    assertNotEquals(onS01, onIso);
    assertTrue(onIso.contains("mm"));
  }

  @Test
  void closedCurveLabelOnDxCarriesDetectorWarning(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxImager020(dir.resolve("dx_imager_020.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    ClosedCurveGraphic closed = lShapeClosed();
    String label = view.formatClosedCurveMeasureLabel(closed);
    assertTrue(label.contains("mm"));
    assertTrue(label.contains("(detector plane)"));
  }

  @Test
  void closedCurveLabelOnDxEstimateCarriesCaveat(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxErmf12(dir.resolve("dx_ermf_12.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    String label = view.formatClosedCurveMeasureLabel(lShapeClosed());
    assertTrue(label.toLowerCase().contains("estimate"));
    assertTrue(label.contains("mm"));
  }

  @Test
  void closedCurveLabelWithoutSpacingIsPixelsOnly(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("ct_nospacing.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    ClosedCurveGraphic closed = lShapeClosed();
    String label = view.formatClosedCurveMeasureLabel(closed);
    assertTrue(label.endsWith(" px"));
    assertFalse(label.toLowerCase().contains("mm"));
  }

  @Test
  void closedCurveLabelDiffersFromOpenCurveForSameHandles(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    CurveGraphic open = openCurve(p0, p1, p2);
    ClosedCurveGraphic closed = closed(p0, p1, p2);
    assertNotEquals(view.formatCurveMeasureLabel(open), view.formatClosedCurveMeasureLabel(closed));
  }

  @Test
  void closedCurveLabelDiffersFromPolylineForSameHandles(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    PolylineGraphic poly = polyline(p0, p1, p2);
    ClosedCurveGraphic closed = closed(p0, p1, p2);
    assertEquals("10.0 mm", view.formatPolylineMeasureLabel(poly));
    assertNotEquals(view.formatPolylineMeasureLabel(poly), view.formatClosedCurveMeasureLabel(closed));
  }

  @Test
  void manualCalibrationDoublesDoNotChangeClosedCurveLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    ClosedCurveGraphic closed = lShapeClosed();
    String before = view.formatClosedCurveMeasureLabel(closed);
    view.setMonitorCalibrationMmPerPixel(9.9);
    view.setSessionManualCalibrationMmPerPixel(9.9);
    assertEquals(before, view.formatClosedCurveMeasureLabel(closed));
  }

  private static ClosedCurveGraphic lShapeClosed() {
    return closed(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
  }

  private static ClosedCurveGraphic closed(Point2D.Double... points) {
    ClosedCurveGraphic c = new ClosedCurveGraphic();
    for (int i = 0; i < points.length; i++) {
      c.setHandlePoint(i, points[i]);
    }
    return c;
  }

  private static CurveGraphic openCurve(Point2D.Double... points) {
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
