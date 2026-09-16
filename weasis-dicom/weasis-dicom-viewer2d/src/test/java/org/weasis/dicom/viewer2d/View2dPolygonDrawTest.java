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

import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * Interactive closed polygon on {@link View2d}: image-space vertices (≥3) create a {@link
 * PolygonGraphic} whose visible label is {@link View2d#formatPolygonMeasureLabel} — not shoelace or
 * mm² recomputed in the mouse path.
 *
 * <p><b>Why (0028,0030) via landed APIs:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing#resolve} on the loaded instance; physical area on
 * the label uses {@link PolygonGraphic#getAreaMm} only through {@link
 * MeasurementLabel#formatPolygon}. The draw handler must not apply row/column pitch in view
 * coordinates or format its own unit string.
 *
 * <p><b>Why closed polygon, not open polyline:</b> area labels bind to {@link PolygonGraphic} and
 * landed {@code getAreaValue} / {@code getAreaMm}; an open path belongs on {@link PolylineGraphic}
 * with length formatters. Click-to-add with double-click close matches the polyline interaction
 * family but requires at least three committed vertices before finalize.
 *
 * <p><b>Why fail-closed px²:</b> when spacing is missing, vertices still store in image space but
 * the label is pixel shoelace area from {@link PolygonGraphic#getAreaValue}, same as headless
 * polygon label tests — no silent mm² guess.
 *
 * <p><b>Why not copy Weasis:</b> upstream {@code MeasureTool} bundles polygon paint, calibration
 * prefs, and GSPS; this slice adds image-space vertices + {@link View2d#formatPolygonMeasureLabel}
 * beside line, polyline, angle, and Cobb calipers — no measure-tool port, free-hand scribble, or
 * spline.
 */
class View2dPolygonDrawTest {

  @Test
  void polygonCaliperUsesFormatPolygonLabelOnCt(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addPolygonCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    assertEquals(1, view.getGraphicList().size());
    Graphic g = view.getGraphicList().getFirst();
    assertInstanceOf(PolygonGraphic.class, g);
    PolygonGraphic poly = (PolygonGraphic) g;
    assertEquals(50.0, poly.getAreaValue(), 1e-9);
    assertEquals("12.5 mm²", view.formatPolygonMeasureLabel(poly));
    assertEquals("12.5 mm²", poly.getLabel()[0]);
  }

  @Test
  void polygonCaliperFailClosedPixelsWithoutSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("nosp.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addPolygonCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    PolygonGraphic poly = (PolygonGraphic) view.getGraphicList().getFirst();
    assertEquals("50.0 px²", view.formatPolygonMeasureLabel(poly));
    assertEquals("50.0 px²", poly.getLabel()[0]);
  }

  @Test
  void clickDrawPolygonMatchesImageSpaceApi(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.POLYGON);
    view.simulatePolygonDrawClick(24, 24, 1);
    view.simulatePolygonDrawClick(34, 24, 1);
    view.simulatePolygonDrawClick(34, 34, 2);
    assertEquals(1, view.getGraphicList().size());
    PolygonGraphic poly = (PolygonGraphic) view.getGraphicList().getFirst();
    assertEquals("12.5 mm²", poly.getLabel()[0]);
    assertEquals(3, poly.getPts().size());
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
}
