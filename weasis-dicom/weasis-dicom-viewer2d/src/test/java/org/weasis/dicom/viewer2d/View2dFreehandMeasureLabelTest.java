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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.imp.line.ClosedCurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.CurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.FreehandGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;
import org.weasis.dicom.codec.utils.InstanceSpacing;

/**
 * View2d free-hand scribble measurement labels: same binding rules as {@link
 * View2dPolylineMeasureLabelTest}, but millimetres come from {@link FreehandGraphic#getLengthMm}
 * on consecutive dense samples — not from Catmull–Rom open/closed interpolants or sparse handle
 * polylines alone.
 *
 * <p><b>Why UI after {@code getLengthMm}:</b> {@link MeasurementLabel#formatFreehand} only formats
 * values already proved in {@link org.weasis.core.ui.model.graphic.imp.line.FreehandGraphicMmTest}
 * and instance spacing in {@link org.weasis.dicom.codec.utils.InstanceSpacingTest}. A label that
 * resampled a spline, closed the stroke, or summed only corner handles would disagree with the
 * landed open-sample API.
 *
 * <p><b>Why bind to {@code Resolved} + {@link FreehandGraphic#getLengthMm} only:</b> spacing comes
 * from {@link InstanceSpacing.Resolved#spacing()} on the current {@link View2d} dataset; the
 * formatter has no {@code BufferedImage}, no {@link View2d}, and no dataset parameter — it cannot
 * read paint or re-resolve tags when window/level changes.
 *
 * <p><b>Why {@code px} never coexists with {@code mm}:</b> when resolve is empty or spacing is
 * unusable, the label uses summed pixel sample length only; no imager fallback on CT and no dual
 * unit string.
 *
 * <p><b>Why the DX warning travels with the number:</b> {@code (detector plane)} / {@code
 * (estimate)} suffixes follow {@link org.weasis.dicom.codec.utils.InstanceSpacing.Source} on the
 * same {@code Resolved} value as line, polyline, and curve labels.
 *
 * <p><b>Why polyline / open-curve / closed-curve aliases must fail:</b> the same handle triple or
 * dense stroke can yield different physical lengths depending on whether the formatter walks dense
 * samples, an open spline, a closed loop, or corner-only segments — {@link
 * #denseSamplesMatchPolylineLabel}, {@link #lShapeLabelDiffersFromOpenCurveLabel}, {@link
 * #lShapeLabelDiffersFromClosedCurveLabel}, and {@link
 * #denseOffChordDiffersFromSparseCornerPolylineLabel} guard against copying the wrong formatter.
 *
 * <p><b>Why not copy Weasis:</b> upstream free-hand measure tools mix mouse drag, paint buffers,
 * and mixed ROI types; this slice adds {@code formatFreehand} beside existing length formatters
 * without porting draw handlers or {@code MouseActions} tokens.
 */
class View2dFreehandMeasureLabelTest {

  @Test
  void freehandLabelOnSyntheticCtUsesInstanceSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct_iso_050.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    FreehandGraphic scribble = horizontalScribble(10);
    assertEquals("5.0 mm", view.formatFreehandMeasureLabel(scribble));
  }

  @Test
  void freehandLabelAfterPagingFollowsNewInstance(@TempDir Path dir) throws Exception {
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
    FreehandGraphic scribble = horizontalScribble(10);
    assertEquals("8.0 mm", view.formatFreehandMeasureLabel(scribble));

    view.setFrameIndex(1);
    assertEquals("5.0 mm", view.formatFreehandMeasureLabel(scribble));
  }

  @Test
  void freehandLabelOnDxCarriesDetectorWarning(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxImager020(dir.resolve("dx_imager_020.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    assertEquals(
        "2.0 mm (detector plane)", view.formatFreehandMeasureLabel(horizontalScribble(10)));
  }

  @Test
  void freehandLabelOnDxEstimateCarriesCaveat(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxErmf12(dir.resolve("dx_ermf_12.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    String label = view.formatFreehandMeasureLabel(horizontalScribble(10));
    assertTrue(label.contains("1.25 mm"));
    assertTrue(label.toLowerCase().contains("estimate"));
  }

  @Test
  void freehandLabelWithoutSpacingIsPixelsOnly(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("ct_nospacing.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    FreehandGraphic scribble = lShapeScribble();
    String label = view.formatFreehandMeasureLabel(scribble);
    assertEquals("20.0 px", label);
    assertFalse(label.toLowerCase().contains("mm"));
  }

  @Test
  void freehandLabelSumsDenseSamplesLikePolyline(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    assertEquals("10.0 mm", view.formatFreehandMeasureLabel(lShapeScribble()));
  }

  @Test
  void denseSamplesMatchPolylineLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    List<Point2D.Double> samples =
        List.of(
            new Point2D.Double(0, 0),
            new Point2D.Double(4, 0),
            new Point2D.Double(7, 0),
            new Point2D.Double(10, 0));
    FreehandGraphic scribble = scribble(samples);
    PolylineGraphic poly = polyline(samples.toArray(Point2D.Double[]::new));
    assertEquals(view.formatPolylineMeasureLabel(poly), view.formatFreehandMeasureLabel(scribble));
  }

  @Test
  void lShapeLabelDiffersFromOpenCurveLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    FreehandGraphic scribble = scribble(p0, p1, p2);
    CurveGraphic open = openCurve(p0, p1, p2);
    assertNotEquals(view.formatCurveMeasureLabel(open), view.formatFreehandMeasureLabel(scribble));
  }

  @Test
  void lShapeLabelDiffersFromClosedCurveLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    FreehandGraphic scribble = scribble(p0, p1, p2);
    ClosedCurveGraphic closed = closed(p0, p1, p2);
    assertNotEquals(
        view.formatClosedCurveMeasureLabel(closed), view.formatFreehandMeasureLabel(scribble));
  }

  @Test
  void denseOffChordDiffersFromSparseCornerPolylineLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    List<Point2D.Double> dense = new ArrayList<>();
    dense.add(p0);
    for (int i = 1; i <= 8; i++) {
      dense.add(new Point2D.Double(5, i));
    }
    dense.add(p1);
    dense.add(p2);
    FreehandGraphic scribble = scribble(dense);
    PolylineGraphic sparse = polyline(p0, p1, p2);
    assertNotEquals(
        view.formatPolylineMeasureLabel(sparse), view.formatFreehandMeasureLabel(scribble));
  }

  @Test
  void manualCalibrationDoublesDoNotChangeFreehandLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    FreehandGraphic scribble = horizontalScribble(10);
    String before = view.formatFreehandMeasureLabel(scribble);
    view.setMonitorCalibrationMmPerPixel(9.9);
    view.setSessionManualCalibrationMmPerPixel(9.9);
    assertEquals(before, view.formatFreehandMeasureLabel(scribble));
  }

  private static FreehandGraphic horizontalScribble(double pixels) {
    return scribble(new Point2D.Double(0, 0), new Point2D.Double(pixels, 0));
  }

  private static FreehandGraphic lShapeScribble() {
    return scribble(
        new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
  }

  private static FreehandGraphic scribble(Point2D.Double... points) {
    return scribble(List.of(points));
  }

  private static FreehandGraphic scribble(List<Point2D.Double> samples) {
    FreehandGraphic g = new FreehandGraphic();
    g.setSamples(samples);
    return g;
  }

  private static PolylineGraphic polyline(Point2D.Double... points) {
    PolylineGraphic poly = new PolylineGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }

  private static CurveGraphic openCurve(Point2D.Double... points) {
    CurveGraphic c = new CurveGraphic();
    for (int i = 0; i < points.length; i++) {
      c.setHandlePoint(i, points[i]);
    }
    return c;
  }

  private static ClosedCurveGraphic closed(Point2D.Double... points) {
    ClosedCurveGraphic c = new ClosedCurveGraphic();
    for (int i = 0; i < points.length; i++) {
      c.setHandlePoint(i, points[i]);
    }
    return c;
  }
}
