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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.dicom.codec.utils.RoiStatistics;

/**
 * Interactive ellipse ROI on {@link View2d}: two image-space bbox handles on {@link EllipseGraphic}
 * bind the visible label to {@link View2d#formatEllipseMeasureLabel} — not HU or spacing recomputed
 * in the mouse path.
 *
 * <p><b>Why landed {@link RoiStatistics#ellipse}:</b> the label string comes only from {@link
 * View2d#formatEllipseMeasureLabel} on the graphic's bbox {@link Ellipse2D}, which delegates to
 * {@link org.weasis.dicom.codec.utils.RoiStatistics#ellipse} on the loaded instance's stored pixels
 * and rescale tags. The draw handler must not sample the painted buffer or duplicate mean HU math.
 *
 * <p><b>Why bbox handles, not a separate formula path:</b> {@link EllipseGraphic#buildShape}
 * already turns two corner handles into an {@link Ellipse2D}; caliper finalize must reuse that
 * shape for the formatter so interactive draw matches headless {@link View2dMeasureLabelTest}
 * ellipse expectations.
 *
 * <p><b>Why fail-closed empty label:</b> when the instance or shape is unusable, {@link
 * View2d#formatEllipseMeasureLabel} returns {@code ""} via the existing oracle — the mouse path
 * must not invent a placeholder HU or px area string.
 *
 * <p><b>Why not copy Weasis:</b> upstream ellipse tools bundle measure prefs, GSPS, and painted ROI
 * adapters; this slice adds image-space bbox handles + {@link View2d#formatEllipseMeasureLabel}
 * beside line, polyline, angle, Cobb, and polygon calipers — no measure-tool port, rectangle ROI,
 * spline, or scribble.
 */
class View2dEllipseDrawTest {

  @Test
  void ellipseCaliperUsesFormatEllipseLabelOnRoiAir(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtRoiAir(dir.resolve("ct_roi_air.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Ellipse2D expectedRoi = MeasureLabelFixtures.ctRoiAirEllipse();
    String expected = view.formatEllipseMeasureLabel(expectedRoi);
    view.addEllipseCaliper(new Point2D.Double(6, 6), new Point2D.Double(10, 10));
    assertEquals(1, view.getGraphicList().size());
    Graphic g = view.getGraphicList().getFirst();
    assertInstanceOf(EllipseGraphic.class, g);
    EllipseGraphic ellipse = (EllipseGraphic) g;
    assertEquals(expected, view.formatEllipseMeasureLabel((Ellipse2D) ellipse.getShape()));
    assertEquals(expected, ellipse.getLabel()[0]);
    assertTrue(expected.contains("HU"));
    assertTrue(expected.contains("-1000"));
  }

  @Test
  void twoClickMouseDrawMatchesImageSpaceApi(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtRoiAir(dir.resolve("ct_roi_air.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.ELLIPSE);
    view.simulateEllipseDrawTwoClick(30, 30, 34, 34);
    assertEquals(1, view.getGraphicList().size());
    EllipseGraphic ellipse = (EllipseGraphic) view.getGraphicList().getFirst();
    Ellipse2D expectedRoi = MeasureLabelFixtures.ctRoiAirEllipse();
    assertEquals(view.formatEllipseMeasureLabel(expectedRoi), ellipse.getLabel()[0]);
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
}
