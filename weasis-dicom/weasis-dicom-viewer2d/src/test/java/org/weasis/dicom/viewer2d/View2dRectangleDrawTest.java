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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

/**
 * Interactive rectangle area on {@link View2d}: two image-space bbox handles on {@link
 * RectangleGraphic} bind the visible label to {@link View2d#formatRectangleMeasureLabel} — not
 * shoelace, bbox product, or mm² recomputed in the mouse path.
 *
 * <p><b>Why landed {@link RectangleGraphic#getAreaMm}:</b> the label string comes only from {@link
 * View2d#formatRectangleMeasureLabel} on the graphic, which delegates to {@link
 * MeasurementLabel#formatRectangle} and instance spacing resolved on the loaded dataset. The draw
 * handler must not apply row/column pitch in view coordinates or format its own unit string.
 *
 * <p><b>Why two-handle bbox, not polygon or ellipse:</b> {@link RectangleGraphic#buildShape}
 * already turns two corner handles into a bbox {@link java.awt.geom.Rectangle2D}; caliper finalize
 * must reuse that graphic type so interactive draw matches headless {@link
 * View2dRectangleMeasureLabelTest} expectations and cannot alias a 2-vertex polygon or ellipse HU
 * mean.
 *
 * <p><b>Why fail-closed px²:</b> when spacing is missing, handles still store in image space but
 * the label is pixel bbox area from {@link RectangleGraphic#getAreaValue}, same as rectangle label
 * tests — no silent mm² guess in the mouse path.
 *
 * <p><b>Why not copy Weasis:</b> upstream measure tools bundle rectangle drawing, calibration
 * prefs, and GSPS; this slice adds image-space bbox handles + {@link
 * View2d#formatRectangleMeasureLabel} beside line, polyline, polygon, and ellipse calipers — no
 * measure-tool port, rectangle ROI HU, or scribble.
 */
class View2dRectangleDrawTest {

  private static final Point2D.Double CORNER_A = new Point2D.Double(0, 0);
  private static final Point2D.Double CORNER_B = new Point2D.Double(10, 5);

  @Test
  void rectangleCaliperUsesFormatRectangleLabelOnCt(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addRectangleCaliper(new Point2D.Double(0, 0), new Point2D.Double(10, 10));
    assertEquals(1, view.getGraphicList().size());
    Graphic g = view.getGraphicList().getFirst();
    assertInstanceOf(RectangleGraphic.class, g);
    RectangleGraphic rect = (RectangleGraphic) g;
    assertEquals(100.0, rect.getAreaValue(), 1e-9);
    assertEquals("25.0 mm²", view.formatRectangleMeasureLabel(rect));
    assertEquals("25.0 mm²", rect.getLabel()[0]);
  }

  @Test
  void rectangleCaliperHonestyPinsAgainstPolygonAndEllipse(@TempDir Path dir) throws Exception {
    File ct =
        StackPagingFixtures.writeCtInstance(
            dir.resolve("ct_aniso.dcm").toFile(), "2.25.1", 1, 0.5, 0.25, 0, 100);
    View2d view = new View2d();
    view.load(ct);
    view.addRectangleCaliper(CORNER_A, CORNER_B);
    RectangleGraphic rect = (RectangleGraphic) view.getGraphicList().getFirst();
    String rectLabel = view.formatRectangleMeasureLabel(rect);
    assertEquals(rectLabel, rect.getLabel()[0]);
    PolygonGraphic bboxPoly = bboxPolygon();
    assertEquals(view.formatPolygonMeasureLabel(bboxPoly), rectLabel);
    assertEquals("6.25 mm²", rectLabel);
    File ctRoiAir = MeasureLabelFixtures.writeCtRoiAir(dir.resolve("ct_roi_air.dcm").toFile());
    View2d huView = new View2d();
    huView.load(ctRoiAir);
    huView.addEllipseCaliper(CORNER_A, CORNER_B);
    EllipseGraphic ellipse = (EllipseGraphic) huView.getGraphicList().getFirst();
    String ellipseHu = huView.formatEllipseMeasureLabel((Ellipse2D) ellipse.getShape());
    assertTrue(ellipseHu.contains("HU"));
    assertNotEquals(rectLabel, ellipseHu);
    assertNotEquals(formatAreaMmLikeLabel(6.25 * (Math.PI / 4.0)), rectLabel);
    PolygonGraphic twoVertex = polygon(CORNER_A, CORNER_B);
    String polyTwo = view.formatPolygonMeasureLabel(twoVertex);
    assertFalse(polyTwo.toLowerCase().contains("mm"));
    assertTrue(rectLabel.contains("mm²"));
  }

  @Test
  void twoClickMouseDrawMatchesImageSpaceApi(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.RECTANGLE);
    view.simulateRectangleDrawTwoClick(24, 24, 34, 34);
    assertEquals(1, view.getGraphicList().size());
    RectangleGraphic rect = (RectangleGraphic) view.getGraphicList().getFirst();
    assertEquals("25.0 mm²", rect.getLabel()[0]);
    assertEquals(view.formatRectangleMeasureLabel(rect), rect.getLabel()[0]);
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

  private static String formatAreaMmLikeLabel(double mm2) {
    long cents = Math.round(mm2 * 100.0);
    double value = cents / 100.0;
    if (cents % 10 != 0) {
      return String.format(Locale.US, "%.2f mm²", value);
    }
    return String.format(Locale.US, "%.1f mm²", value);
  }

  private static PolygonGraphic bboxPolygon() {
    return polygon(
        new Point2D.Double(0, 0),
        new Point2D.Double(10, 0),
        new Point2D.Double(10, 5),
        new Point2D.Double(0, 5));
  }

  private static PolygonGraphic polygon(Point2D.Double... points) {
    PolygonGraphic poly = new PolygonGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }
}
