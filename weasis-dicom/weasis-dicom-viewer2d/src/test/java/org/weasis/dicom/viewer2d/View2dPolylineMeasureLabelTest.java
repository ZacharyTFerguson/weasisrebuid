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
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * View2d polyline measurement labels: same binding rules as {@link View2dMeasureLabelTest} for
 * lines, but millimetres come from {@link PolylineGraphic#getLengthMm} (sum of per-segment hypot
 * lengths).
 *
 * <p><b>Why UI after {@code getLengthMm}:</b> {@link MeasurementLabel#formatPolyline} only formats
 * values already proved in {@link org.weasis.core.ui.model.graphic.imp.line.PolylineGraphicMmTest}
 * and instance spacing in {@link org.weasis.dicom.codec.utils.InstanceSpacingTest}. A label that
 * recomputed mm from pixel distance or used end-point hypot would disagree with the segment-sum
 * API.
 *
 * <p><b>Why bind to {@code Resolved} + {@link PolylineGraphic#getLengthMm} only:</b> spacing comes
 * from {@link org.weasis.dicom.codec.utils.InstanceSpacing.Resolved#spacing()} on the current
 * {@link View2d} dataset; the formatter has no {@code BufferedImage}, no {@link View2d}, and no
 * dataset parameter — it cannot read paint or re-resolve tags when window/level changes.
 *
 * <p><b>Why {@code px} never coexists with {@code mm}:</b> when resolve is empty or spacing is
 * unusable, the label uses summed pixel segment length only; no imager fallback on CT and no dual
 * unit string.
 *
 * <p><b>Why the DX warning travels with the number:</b> {@code (detector plane)} / {@code
 * (estimate)} suffixes follow {@link org.weasis.dicom.codec.utils.InstanceSpacing.Source} on the
 * same {@code Resolved} value as line labels, not a tag-presence check that clears when (0018,1164)
 * exists.
 *
 * <p><b>Why segment sum, not chord length:</b> an L-shaped path from (0,0) to (10,10) via (10,0) is
 * 20 px and 10.0 mm on isotropic 0.50 spacing — not the 14.14 px diagonal chord. The primary
 * discriminating assert is {@link #polylineLabelSumsSegmentsNotEndToEndHypot}.
 *
 * <p><b>Why not copy Weasis:</b> upstream measure tools mix polyline drawing, calibration prefs,
 * and file-extracted spacing; this slice adds {@code formatPolyline} beside the existing line
 * formatter without porting {@code MeasureTool} or {@code MeasurementsAdapter}.
 */
class View2dPolylineMeasureLabelTest {

  @Test
  void polylineLabelOnSyntheticCtUsesInstanceSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct_iso_050.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    PolylineGraphic poly = horizontalPolyline(10);
    assertEquals("5.0 mm", view.formatPolylineMeasureLabel(poly));
  }

  @Test
  void polylineLabelAfterPagingFollowsNewInstance(@TempDir Path dir) throws Exception {
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
    PolylineGraphic poly = horizontalPolyline(10);
    assertEquals("8.0 mm", view.formatPolylineMeasureLabel(poly));

    view.setFrameIndex(1);
    assertEquals("5.0 mm", view.formatPolylineMeasureLabel(poly));
  }

  @Test
  void polylineLabelOnDxCarriesDetectorWarning(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxImager020(dir.resolve("dx_imager_020.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    assertEquals(
        "2.0 mm (detector plane)", view.formatPolylineMeasureLabel(horizontalPolyline(10)));
  }

  @Test
  void polylineLabelOnDxEstimateCarriesCaveat(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxErmf12(dir.resolve("dx_ermf_12.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    String label = view.formatPolylineMeasureLabel(horizontalPolyline(10));
    assertTrue(label.contains("1.25 mm"));
    assertTrue(label.toLowerCase().contains("estimate"));
  }

  @Test
  void polylineLabelWithoutSpacingIsPixelsOnly(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("ct_nospacing.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    PolylineGraphic poly = lShapePolyline();
    String label = view.formatPolylineMeasureLabel(poly);
    assertEquals("20.0 px", label);
    assertFalse(label.toLowerCase().contains("mm"));
  }

  @Test
  void polylineLabelSumsSegmentsNotEndToEndHypot(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    assertEquals("10.0 mm", view.formatPolylineMeasureLabel(lShapePolyline()));
  }

  @Test
  void manualCalibrationDoublesDoNotChangePolylineLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    PolylineGraphic poly = horizontalPolyline(10);
    String before = view.formatPolylineMeasureLabel(poly);
    view.setMonitorCalibrationMmPerPixel(9.9);
    view.setSessionManualCalibrationMmPerPixel(9.9);
    assertEquals(before, view.formatPolylineMeasureLabel(poly));
  }

  private static PolylineGraphic horizontalPolyline(double pixels) {
    return polyline(new Point2D.Double(0, 0), new Point2D.Double(pixels, 0));
  }

  /** (0,0) → (10,0) → (10,10): 20 px path, 10.0 mm at 0.50 spacing — not ~7.1 mm chord. */
  private static PolylineGraphic lShapePolyline() {
    return polyline(
        new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
  }

  private static PolylineGraphic polyline(Point2D.Double... points) {
    PolylineGraphic poly = new PolylineGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }
}
