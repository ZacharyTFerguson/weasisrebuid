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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.ClosedCurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.CurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.FreehandGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * Interactive free-hand scribble on {@link View2d}: dense image-space samples create a {@link
 * FreehandGraphic} whose visible label is {@link View2d#formatFreehandMeasureLabel} — not a
 * recomputed mm, hypot, or sample sum in the mouse path.
 *
 * <p><b>Why (0028,0030) via landed APIs:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing#resolve} on the loaded instance; millimetres on the
 * label use {@link FreehandGraphic#getLengthMm} only through {@link MeasurementLabel#formatFreehand}.
 * The draw handler must not read spacing or sum segments itself.
 *
 * <p><b>Why press-drag-release:</b> landed {@link FreehandGraphic#getLengthMm} and {@link
 * FreehandGraphic#getLength} require at least two image-space samples along the stroke; a single
 * click must not commit a graphic.
 *
 * <p><b>Why honesty vs polyline, open curve, and closed curve:</b> the stored graphic must be a
 * free-hand stroke, not a sparse-handle polyline or spline alias — dense samples that match a
 * polyline sum must still differ from corner-only polylines and from open/closed Catmull–Rom labels
 * on the same handle triple, as proved in {@link View2dFreehandMeasureLabelTest}.
 *
 * <p><b>Why fail-closed px:</b> when spacing is missing, the label uses summed pixel path length
 * from {@link FreehandGraphic#getLength} via {@link MeasurementLabel#formatFreehand} — no silent mm
 * guess.
 *
 * <p><b>Why not copy Weasis:</b> upstream measure tools mix painted-buffer scribble with spline
 * tools; this slice adds image-space press-drag beside existing line and polyline — no measure-tool
 * port and no change to landed free-hand sampling or {@code formatFreehand}.
 */
class View2dFreehandDrawTest {

  @Test
  void freehandCaliperUsesFormatFreehandLabelOnCt(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addFreehandCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    assertEquals(1, view.getGraphicList().size());
    Graphic g = view.getGraphicList().getFirst();
    assertInstanceOf(FreehandGraphic.class, g);
    FreehandGraphic scribble = (FreehandGraphic) g;
    PolylineGraphic poly = polylineFromSameSamples(scribble);
    CurveGraphic open = openFromSameHandles(scribble);
    ClosedCurveGraphic closed = closedFromSameHandles(scribble);
    assertEquals(view.formatPolylineMeasureLabel(poly), view.formatFreehandMeasureLabel(scribble));
    assertNotEquals(view.formatCurveMeasureLabel(open), view.formatFreehandMeasureLabel(scribble));
    assertNotEquals(
        view.formatClosedCurveMeasureLabel(closed), view.formatFreehandMeasureLabel(scribble));
    assertEquals(view.formatFreehandMeasureLabel(scribble), scribble.getLabel()[0]);
  }

  @Test
  void freehandCaliperFailClosedPixelsWithoutSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("nosp.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addFreehandCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    FreehandGraphic scribble = (FreehandGraphic) view.getGraphicList().getFirst();
    String label = view.formatFreehandMeasureLabel(scribble);
    assertEquals(label, scribble.getLabel()[0]);
    assertTrue(label.endsWith(" px"));
  }

  @Test
  void dragDrawFreehandMatchesImageSpaceApi(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.FREEHAND);
    view.simulateFreehandDrawStroke(24, 24, 29, 24, 34, 24, 34, 34);
    assertEquals(1, view.getGraphicList().size());
    FreehandGraphic scribble = (FreehandGraphic) view.getGraphicList().getFirst();
    assertTrue(scribble.getPts().size() >= 2);
    assertEquals(view.formatFreehandMeasureLabel(scribble), scribble.getLabel()[0]);
    PolylineGraphic poly = polylineFromSameSamples(scribble);
    assertEquals(view.formatPolylineMeasureLabel(poly), scribble.getLabel()[0]);
  }

  @Test
  void denseOffChordStrokeDiffersFromSparseCornerPolyline(@TempDir Path dir) throws Exception {
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
    view.addFreehandCaliper(dense);
    FreehandGraphic scribble = (FreehandGraphic) view.getGraphicList().getFirst();
    PolylineGraphic sparse = polyline(p0, p1, p2);
    assertNotEquals(
        view.formatPolylineMeasureLabel(sparse), view.formatFreehandMeasureLabel(scribble));
    assertEquals(view.formatFreehandMeasureLabel(scribble), scribble.getLabel()[0]);
  }

  @Test
  void lineDrawUnchangedWhenLeftActionIsDraw(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.DRAW);
    view.simulateLineDrawTwoClick(24, 24, 34, 24);
    assertEquals(1, view.getGraphicList().size());
    assertInstanceOf(LineGraphic.class, view.getGraphicList().getFirst());
  }

  private static PolylineGraphic polylineFromSameSamples(FreehandGraphic scribble) {
    return polyline(scribble.getPts().toArray(Point2D.Double[]::new));
  }

  private static PolylineGraphic polyline(Point2D.Double... points) {
    PolylineGraphic poly = new PolylineGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }

  private static CurveGraphic openFromSameHandles(FreehandGraphic scribble) {
    List<Point2D.Double> pts = scribble.getPts();
    CurveGraphic curve = new CurveGraphic();
    curve.setHandlePoint(0, pts.get(0));
    curve.setHandlePoint(1, pts.get(1));
    curve.setHandlePoint(2, pts.get(2));
    return curve;
  }

  private static ClosedCurveGraphic closedFromSameHandles(FreehandGraphic scribble) {
    List<Point2D.Double> pts = scribble.getPts();
    ClosedCurveGraphic closed = new ClosedCurveGraphic();
    closed.setHandlePoint(0, pts.get(0));
    closed.setHandlePoint(1, pts.get(1));
    closed.setHandlePoint(2, pts.get(2));
    return closed;
  }
}
