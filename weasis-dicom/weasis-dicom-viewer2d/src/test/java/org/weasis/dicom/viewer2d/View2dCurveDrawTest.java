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
import org.weasis.core.ui.model.graphic.imp.line.CurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * Interactive open curve on {@link View2d}: image-space handles create a {@link CurveGraphic} whose
 * visible label is {@link View2d#formatCurveMeasureLabel} — not a recomputed mm, hypot, or
 * Catmull–Rom sample sum in the mouse path.
 *
 * <p><b>Why (0028,0030) via landed APIs:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing#resolve} on the loaded instance; millimetres on the
 * label use {@link CurveGraphic#getLengthMm} only through {@link MeasurementLabel#formatCurve}. The
 * draw handler must not read spacing or resample the spline itself.
 *
 * <p><b>Why ≥3 handles:</b> landed {@link CurveGraphic#getLengthMm} and the Catmull–Rom contract
 * require at least three image-space vertices; finishing with fewer removes the draft graphic.
 *
 * <p><b>Why honesty vs polyline:</b> an L-shaped handle triple must bind a curve label that differs
 * from {@link View2d#formatPolylineMeasureLabel} on the same handles — a polyline-alias would lie
 * about the smooth path length proved in {@link View2dCurveMeasureLabelTest}.
 *
 * <p><b>Why fail-closed px:</b> when spacing is missing, the label uses sampled pixel path length
 * from {@link CurveGraphic#getLength} via {@link MeasurementLabel#formatCurve} — no silent mm
 * guess.
 *
 * <p><b>Why not copy Weasis:</b> upstream spline measure tools mix free-hand scribble, closure, and
 * paint buffers; this slice adds image-space click-to-add beside existing polyline — no
 * measure-tool port and no change to landed {@code sampleOpenCatmullRom} / {@code formatCurve}.
 */
class View2dCurveDrawTest {

  @Test
  void curveCaliperUsesFormatCurveLabelOnCt(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addCurveCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    assertEquals(1, view.getGraphicList().size());
    Graphic g = view.getGraphicList().getFirst();
    assertInstanceOf(CurveGraphic.class, g);
    CurveGraphic curve = (CurveGraphic) g;
    PolylineGraphic poly = polylineFromSameHandles(curve);
    assertEquals("10.0 mm", view.formatPolylineMeasureLabel(poly));
    assertNotEquals(view.formatPolylineMeasureLabel(poly), view.formatCurveMeasureLabel(curve));
    assertEquals(view.formatCurveMeasureLabel(curve), curve.getLabel()[0]);
  }

  @Test
  void curveCaliperFailClosedPixelsWithoutSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("nosp.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addCurveCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    CurveGraphic curve = (CurveGraphic) view.getGraphicList().getFirst();
    String label = view.formatCurveMeasureLabel(curve);
    assertEquals(label, curve.getLabel()[0]);
    assertTrue(label.endsWith(" px"));
  }

  @Test
  void clickDrawCurveMatchesImageSpaceApi(@TempDir Path dir) throws Exception {
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
    CurveGraphic curve = (CurveGraphic) view.getGraphicList().getFirst();
    assertEquals(3, curve.getPts().size());
    assertEquals(view.formatCurveMeasureLabel(curve), curve.getLabel()[0]);
    PolylineGraphic poly = polylineFromSameHandles(curve);
    assertNotEquals(view.formatPolylineMeasureLabel(poly), curve.getLabel()[0]);
  }

  @Test
  void polylineDrawUnchangedWhenLeftActionIsPolyline(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.POLYLINE);
    view.simulatePolylineDrawClick(24, 24, 1);
    view.simulatePolylineDrawClick(34, 24, 1);
    view.simulatePolylineDrawClick(34, 34, 2);
    assertEquals(1, view.getGraphicList().size());
    assertInstanceOf(PolylineGraphic.class, view.getGraphicList().getFirst());
  }

  private static PolylineGraphic polylineFromSameHandles(CurveGraphic curve) {
    PolylineGraphic poly = new PolylineGraphic();
    List<Point2D.Double> pts = curve.getPts();
    for (int i = 0; i < pts.size(); i++) {
      poly.setHandlePoint(i, pts.get(i));
    }
    return poly;
  }
}
