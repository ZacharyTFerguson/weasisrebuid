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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;

/**
 * View2d closed-polygon area labels: physical mm² from {@link PolygonGraphic#getAreaMm} when
 * instance spacing resolves, otherwise pixel shoelace area from {@link
 * PolygonGraphic#getAreaValue}.
 *
 * <p><b>Why UI after {@code getAreaMm} / {@code getAreaValue}:</b> {@link
 * MeasurementLabel#formatPolygon} only formats values already proved in {@link
 * org.weasis.core.ui.model.graphic.imp.area.PolygonGraphicAreaMmTest} and instance spacing in
 * {@link org.weasis.dicom.codec.utils.InstanceSpacingTest}. A label that recomputed shoelace in mm
 * space or applied a single isotropic mm factor would disagree with the landed area APIs.
 *
 * <p><b>Why bind to {@code Resolved} + graphic area methods only:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing.Resolved#spacing()} on the current {@link View2d}
 * dataset; the formatter has no {@code BufferedImage}, no {@link View2d}, and no dataset parameter
 * — it cannot read paint or re-resolve tags when window/level changes.
 *
 * <p><b>Why {@code px²} never coexists with {@code mm²}:</b> when resolve is empty, spacing is
 * unusable, or fewer than three vertices leave {@link PolygonGraphic#getAreaMm} empty, the label
 * uses pixel area only — no imager fallback on CT and no dual unit string.
 *
 * <p><b>Why the DX warning travels with the area number:</b> {@code (detector plane)} / {@code
 * (estimate)} suffixes follow {@link org.weasis.dicom.codec.utils.InstanceSpacing.Source} on the
 * same {@code Resolved} value as line, polyline, angle, and Cobb labels, not a tag-presence check
 * that clears when (0018,1164) exists.
 *
 * <p><b>Why paging changes mm² but pixel area on the graphic is unchanged:</b> the same vertex path
 * keeps the same shoelace pixel area while row/column spacing on the displayed instance changes the
 * physical mm² — the primary discriminating assert is {@link
 * #polygonLabelAfterPagingFollowsNewInstance}.
 *
 * <p><b>Why not copy Weasis:</b> upstream measure tools mix polygon drawing, calibration prefs, and
 * file-extracted spacing; this slice adds {@code formatPolygon} beside the existing line/polyline
 * formatters without porting {@code MeasureTool} or {@code MeasurementsAdapter}.
 */
class View2dPolygonMeasureLabelTest {

  @Test
  void polygonLabelOnSyntheticCtUsesInstanceSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct_iso_050.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    PolygonGraphic poly = rightTriangle();
    assertEquals("12.5 mm²", view.formatPolygonMeasureLabel(poly));
  }

  @Test
  void polygonLabelAfterPagingFollowsNewInstance(@TempDir Path dir) throws Exception {
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
    PolygonGraphic poly = rightTriangle();
    assertEquals("32.0 mm²", view.formatPolygonMeasureLabel(poly));

    view.setFrameIndex(1);
    assertEquals("12.5 mm²", view.formatPolygonMeasureLabel(poly));
  }

  @Test
  void polygonLabelOnDxCarriesDetectorWarning(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxImager020(dir.resolve("dx_imager_020.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    assertEquals("2.0 mm² (detector plane)", view.formatPolygonMeasureLabel(rightTriangle()));
  }

  @Test
  void polygonLabelOnDxEstimateCarriesCaveat(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxErmf12(dir.resolve("dx_ermf_12.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    String label = view.formatPolygonMeasureLabel(rightTriangle());
    assertTrue(label.contains("mm²"));
    assertTrue(label.toLowerCase().contains("estimate"));
  }

  @Test
  void polygonLabelWithoutSpacingIsPixelsOnly(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("ct_nospacing.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    String label = view.formatPolygonMeasureLabel(rightTriangle());
    assertEquals("50.0 px²", label);
    assertFalse(label.toLowerCase().contains("mm"));
  }

  @Test
  void polygonLabelFewerThanThreeVerticesUsesPixelsOnly(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    PolygonGraphic two = polygon(new Point2D.Double(0, 0), new Point2D.Double(10, 5));
    String label = view.formatPolygonMeasureLabel(two);
    assertEquals("50.0 px²", label);
    assertFalse(label.toLowerCase().contains("mm"));
  }

  @Test
  void manualCalibrationDoublesDoNotChangePolygonLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    PolygonGraphic poly = rightTriangle();
    String before = view.formatPolygonMeasureLabel(poly);
    view.setMonitorCalibrationMmPerPixel(9.9);
    view.setSessionManualCalibrationMmPerPixel(9.9);
    assertEquals(before, view.formatPolygonMeasureLabel(poly));
  }

  /** Right triangle (0,0)-(10,0)-(10,10): 50 px² shoelace area. */
  private static PolygonGraphic rightTriangle() {
    return polygon(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
  }

  private static PolygonGraphic polygon(Point2D.Double... points) {
    PolygonGraphic poly = new PolygonGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }
}
