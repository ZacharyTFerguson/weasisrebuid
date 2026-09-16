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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.ClosedCurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.CurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * Interactive closed curve on {@link View2d}: image-space handles create a {@link
 * ClosedCurveGraphic} whose visible label is {@link View2d#formatClosedCurveMeasureLabel} — not a
 * recomputed mm, hypot, or Catmull–Rom sample sum in the mouse path.
 *
 * <p><b>Why (0028,0030) via landed APIs:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing#resolve} on the loaded instance; millimetres on the
 * label use {@link ClosedCurveGraphic#getLengthMm} only through {@link
 * MeasurementLabel#formatClosedCurve}. The draw handler must not read spacing or resample the
 * spline itself.
 *
 * <p><b>Why ≥3 handles:</b> landed {@link ClosedCurveGraphic#getLengthMm} and the closed
 * Catmull–Rom contract require at least three image-space vertices; finishing with fewer removes
 * the draft graphic.
 *
 * <p><b>Why honesty vs open curve and polyline:</b> an L-shaped handle triple must bind a closed
 * curve label that differs from both {@link View2d#formatCurveMeasureLabel} and {@link
 * View2d#formatPolylineMeasureLabel} on the same handles — aliasing either would lie about the
 * closed smooth loop length proved in {@link View2dClosedCurveMeasureLabelTest}.
 *
 * <p><b>Why fail-closed px:</b> when spacing is missing, the label uses sampled closed pixel path
 * length from {@link ClosedCurveGraphic#getLength} via {@link MeasurementLabel#formatClosedCurve} —
 * no silent mm guess.
 *
 * <p><b>Why not copy Weasis:</b> upstream closed-spline measure tools mix free-hand scribble,
 * closure, and paint buffers; this slice adds image-space click-to-add beside existing open curve
 * and polyline — no measure-tool port and no change to landed closed-curve sampling or {@code
 * formatClosedCurve}.
 */
class View2dClosedCurveDrawTest {

  @Test
  void closedCurveCaliperUsesFormatClosedCurveLabelOnCt(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addClosedCurveCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    assertEquals(1, view.getGraphicList().size());
    Graphic g = view.getGraphicList().getFirst();
    assertInstanceOf(ClosedCurveGraphic.class, g);
    ClosedCurveGraphic closed = (ClosedCurveGraphic) g;
    CurveGraphic open = openFromSameHandles(closed);
    PolylineGraphic poly = polylineFromSameHandles(closed);
    assertNotEquals(view.formatCurveMeasureLabel(open), view.formatClosedCurveMeasureLabel(closed));
    assertNotEquals(
        view.formatPolylineMeasureLabel(poly), view.formatClosedCurveMeasureLabel(closed));
    assertEquals(view.formatClosedCurveMeasureLabel(closed), closed.getLabel()[0]);
  }

  @Test
  void closedCurveCaliperFailClosedPixelsWithoutSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("nosp.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addClosedCurveCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    ClosedCurveGraphic closed = (ClosedCurveGraphic) view.getGraphicList().getFirst();
    String label = view.formatClosedCurveMeasureLabel(closed);
    assertEquals(label, closed.getLabel()[0]);
    assertTrue(label.endsWith(" px"));
  }

  @Test
  void clickDrawClosedCurveMatchesImageSpaceApi(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.CLOSED_CURVE);
    view.simulateClosedCurveDrawClick(24, 24, 1);
    view.simulateClosedCurveDrawClick(34, 24, 1);
    view.simulateClosedCurveDrawClick(34, 34, 2);
    assertEquals(1, view.getGraphicList().size());
    ClosedCurveGraphic closed = (ClosedCurveGraphic) view.getGraphicList().getFirst();
    assertEquals(3, closed.getPts().size());
    assertEquals(view.formatClosedCurveMeasureLabel(closed), closed.getLabel()[0]);
    CurveGraphic open = openFromSameHandles(closed);
    PolylineGraphic poly = polylineFromSameHandles(closed);
    assertNotEquals(view.formatCurveMeasureLabel(open), closed.getLabel()[0]);
    assertNotEquals(view.formatPolylineMeasureLabel(poly), closed.getLabel()[0]);
  }

  @Test
  void curveDrawUnchangedWhenLeftActionIsCurve(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.CURVE);
    view.simulateCurveDrawClick(24, 24, 1);
    view.simulateCurveDrawClick(34, 24, 1);
    view.simulateCurveDrawClick(34, 34, 2);
    assertEquals(1, view.getGraphicList().size());
    assertInstanceOf(CurveGraphic.class, view.getGraphicList().getFirst());
  }

  private static PolylineGraphic polylineFromSameHandles(ClosedCurveGraphic closed) {
    PolylineGraphic poly = new PolylineGraphic();
    List<Point2D.Double> pts = closed.getPts();
    for (int i = 0; i < pts.size(); i++) {
      poly.setHandlePoint(i, pts.get(i));
    }
    return poly;
  }

  private static CurveGraphic openFromSameHandles(ClosedCurveGraphic closed) {
    CurveGraphic curve = new CurveGraphic();
    List<Point2D.Double> pts = closed.getPts();
    for (int i = 0; i < pts.size(); i++) {
      curve.setHandlePoint(i, pts.get(i));
    }
    return curve;
  }
}
