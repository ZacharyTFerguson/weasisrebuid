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

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;

/**
 * View2d two-corner rectangle area labels: physical mm² from {@link RectangleGraphic#getAreaMm}
 * when instance spacing resolves, otherwise pixel bbox area from {@link
 * RectangleGraphic#getAreaValue}.
 *
 * <p><b>Why UI after {@code getAreaMm} / {@code getAreaValue}:</b> {@link
 * MeasurementLabel#formatRectangle} only formats values already proved in {@link
 * org.weasis.core.ui.model.graphic.imp.area.RectangleGraphicAreaMmTest} and instance spacing in
 * {@link org.weasis.dicom.codec.utils.InstanceSpacingTest}. A label that reused isotropic mm²,
 * ellipse HU, or a two-vertex polygon path would disagree with the landed rectangle APIs.
 *
 * <p><b>Why bind to {@code Resolved} + graphic area methods only:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing.Resolved#spacing()} on the current {@link View2d}
 * dataset; the formatter has no {@code BufferedImage}, no {@link View2d}, and no dataset parameter
 * — it cannot read paint or re-resolve tags when window/level changes.
 *
 * <p><b>Why {@code px²} never coexists with {@code mm²}:</b> when resolve is empty, spacing is
 * unusable, or a corner handle is missing and {@link RectangleGraphic#getAreaMm} is empty, the
 * label uses pixel bbox area only — no imager fallback on CT and no dual unit string.
 *
 * <p><b>Why the DX warning travels with the area number:</b> {@code (detector plane)} / {@code
 * (estimate)} suffixes follow {@link org.weasis.dicom.codec.utils.InstanceSpacing.Source} on the
 * same {@code Resolved} value as polygon and line labels, not a tag-presence check that clears when
 * (0018,1164) exists.
 *
 * <p><b>Why paging changes mm² but pixel area on the graphic is unchanged:</b> the same two corners
 * keep the same bbox pixel area while row/column spacing on the displayed instance changes the
 * physical mm² — the primary discriminating assert is {@link
 * #rectangleLabelAfterPagingFollowsNewInstance}.
 *
 * <p><b>Why not copy Weasis:</b> upstream measure tools mix rectangle drawing, calibration prefs,
 * and file-extracted spacing; this slice adds {@code formatRectangle} beside {@code formatPolygon}
 * without porting {@code MeasureTool} or {@code MeasurementsAdapter}.
 */
class View2dRectangleMeasureLabelTest {

  private static final Point2D.Double CORNER_A = new Point2D.Double(0, 0);
  private static final Point2D.Double CORNER_B = new Point2D.Double(10, 5);

  @Test
  void rectangleLabelMatchesPolygonBboxWithAnisotropicSpacing(@TempDir Path dir) throws Exception {
    File ct =
        StackPagingFixtures.writeCtInstance(
            dir.resolve("ct_aniso.dcm").toFile(), "2.25.1", 1, 0.5, 0.25, 0, 100);
    View2d view = new View2d();
    view.load(ct);
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    PolygonGraphic bboxPoly = bboxPolygon();
    String polyLabel = view.formatPolygonMeasureLabel(bboxPoly);
    String rectLabel = view.formatRectangleMeasureLabel(rect);
    assertEquals(polyLabel, rectLabel);
    assertEquals("6.25 mm²", rectLabel);
  }

  @Test
  void rectangleLabelNotEllipseHuAlias(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtRoiAir(dir.resolve("ct_roi_air.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    String rectLabel = view.formatRectangleMeasureLabel(rect);
    Ellipse2D bboxEllipse = new Ellipse2D.Double(0, 0, 10, 5);
    String ellipseLabel = view.formatEllipseMeasureLabel(bboxEllipse);
    assertTrue(ellipseLabel.contains("HU"));
    assertNotEquals(rectLabel, ellipseLabel);
  }

  @Test
  void rectangleLabelNotPiQuarterEllipseAreaAlias(@TempDir Path dir) throws Exception {
    File ct =
        StackPagingFixtures.writeCtInstance(
            dir.resolve("ct_aniso.dcm").toFile(), "2.25.2", 1, 0.5, 0.25, 0, 100);
    View2d view = new View2d();
    view.load(ct);
    String rectLabel = view.formatRectangleMeasureLabel(rectangle(CORNER_A, CORNER_B));
    String piQuarterAlias = formatAreaMmLikeLabel(6.25 * (Math.PI / 4.0));
    assertNotEquals(piQuarterAlias, rectLabel);
    assertEquals("6.25 mm²", rectLabel);
  }

  @Test
  void twoVertexPolygonNoMmButRectangleHasMm(@TempDir Path dir) throws Exception {
    File ct =
        StackPagingFixtures.writeCtInstance(
            dir.resolve("ct_aniso.dcm").toFile(), "2.25.3", 1, 0.5, 0.25, 0, 100);
    View2d view = new View2d();
    view.load(ct);
    PolygonGraphic two = polygon(CORNER_A, CORNER_B);
    String polyLabel = view.formatPolygonMeasureLabel(two);
    assertEquals("50.0 px²", polyLabel);
    assertFalse(polyLabel.toLowerCase().contains("mm"));
    String rectLabel = view.formatRectangleMeasureLabel(rectangle(CORNER_A, CORNER_B));
    assertTrue(rectLabel.contains("mm²"));
    assertEquals("6.25 mm²", rectLabel);
  }

  @Test
  void rectangleLabelOnSyntheticCtUsesInstanceSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct_iso_050.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    assertEquals("12.5 mm²", view.formatRectangleMeasureLabel(rectangle(CORNER_A, CORNER_B)));
  }

  @Test
  void rectangleLabelAfterPagingFollowsNewInstance(@TempDir Path dir) throws Exception {
    Path pack = View2dMeasureLabelTest.roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File s01 = pack.resolve("ct_brain_ax_s01_256.dcm").toFile();
    Assumptions.assumeTrue(s01.isFile());
    String seriesUid;
    try (DicomInputStream in = new DicomInputStream(s01)) {
      seriesUid = in.readDataset(-1, -1).getString(Tag.SeriesInstanceUID);
    }
    File iso =
        StackPagingFixtures.writeCtInstance(
            dir.resolve("iso050.dcm").toFile(), seriesUid, 2, 0.50, 0.50, 5.0, 100);

    View2d view = new View2d();
    view.loadStack(java.util.List.of(s01, iso));
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    assertEquals("32.0 mm²", view.formatRectangleMeasureLabel(rect));

    view.setFrameIndex(1);
    assertEquals("12.5 mm²", view.formatRectangleMeasureLabel(rect));
  }

  @Test
  void rectangleLabelOnDxCarriesDetectorWarning(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxImager020(dir.resolve("dx_imager_020.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    assertEquals(
        "2.0 mm² (detector plane)",
        view.formatRectangleMeasureLabel(rectangle(CORNER_A, CORNER_B)));
  }

  @Test
  void rectangleLabelOnDxEstimateCarriesCaveat(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxErmf12(dir.resolve("dx_ermf_12.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    String label = view.formatRectangleMeasureLabel(rectangle(CORNER_A, CORNER_B));
    assertTrue(label.contains("mm²"));
    assertTrue(label.toLowerCase().contains("estimate"));
  }

  @Test
  void rectangleLabelWithoutSpacingIsPixelsOnly(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("ct_nospacing.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    String label = view.formatRectangleMeasureLabel(rectangle(CORNER_A, CORNER_B));
    assertEquals("50.0 px²", label);
    assertFalse(label.toLowerCase().contains("mm"));
  }

  @Test
  void manualCalibrationDoublesDoNotChangeRectangleLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    String before = view.formatRectangleMeasureLabel(rect);
    view.setMonitorCalibrationMmPerPixel(9.9);
    view.setSessionManualCalibrationMmPerPixel(9.9);
    assertEquals(before, view.formatRectangleMeasureLabel(rect));
  }

  /** Same rounding as {@link MeasurementLabel} private area formatter — honesty pin only. */
  private static String formatAreaMmLikeLabel(double mm2) {
    long cents = Math.round(mm2 * 100.0);
    double value = cents / 100.0;
    if (cents % 10 != 0) {
      return String.format(Locale.US, "%.2f mm²", value);
    }
    return String.format(Locale.US, "%.1f mm²", value);
  }

  private static RectangleGraphic rectangle(Point2D.Double a, Point2D.Double b) {
    RectangleGraphic rect = new RectangleGraphic();
    rect.setHandlePoint(0, a);
    rect.setHandlePoint(1, b);
    return rect;
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
