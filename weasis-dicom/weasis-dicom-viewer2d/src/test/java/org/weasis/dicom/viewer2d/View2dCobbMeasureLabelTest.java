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
import org.weasis.core.ui.model.graphic.imp.angle.CobbToolGraphic;

/**
 * View2d Cobb measurement labels: degrees from pixel endplate directions plus per-endplate lengths,
 * same binding rules as {@link View2dAngleMeasureLabelTest} for angles.
 *
 * <p><b>Why UI after {@code getCobbAngleDegrees} / {@code getEndplateLengthMm}:</b> {@link
 * MeasurementLabel#formatCobb} only formats values already proved in {@link
 * org.weasis.core.ui.model.graphic.imp.angle.CobbToolGraphicMeasureTest} and instance spacing in
 * {@link org.weasis.dicom.codec.utils.InstanceSpacingTest}. A label that recomputed Cobb degrees from
 * mm-scaled endplate vectors or guessed endplate mm from pixel hypot would disagree with the landed
 * APIs.
 *
 * <p><b>Why bind to {@code Resolved} + graphic measure methods only:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing.Resolved#spacing()} on the current {@link View2d}
 * dataset; the formatter has no {@code BufferedImage}, no {@link View2d}, and no dataset parameter
 * — it cannot read paint or re-resolve tags when window/level changes.
 *
 * <p><b>Why degrees stay when spacing is missing but {@code mm} does not:</b> the Cobb angle is
 * dimensionless in pixel space; endplate lengths fall back to pixel segment length per endplate when
 * resolve is empty or {@link CobbToolGraphic#getEndplateLengthMm} is empty — no imager fallback on
 * CT and no dual unit string on the endplates.
 *
 * <p><b>Why the DX warning travels with the endplate numbers:</b> {@code (detector plane)} / {@code
 * (estimate)} suffixes follow {@link org.weasis.dicom.codec.utils.InstanceSpacing.Source} on the
 * same {@code Resolved} value as line, polyline, and angle labels, not a tag-presence check that
 * clears when (0018,1164) exists.
 *
 * <p><b>Why spacing changes endplate mm but not degrees:</b> anisotropic row/column mm scaling changes
 * physical endplate segment lengths but not the angle between the same pixel endplate directions —
 * the primary discriminating assert is {@link #cobbLabelDegreesUnchangedWhenInstanceSpacingChanges}.
 *
 * <p><b>Why not copy Weasis:</b> upstream measure tools mix Cobb drawing, calibration prefs, and
 * file-extracted spacing; this slice adds {@code formatCobb} beside the existing line/polyline/angle
 * formatters without porting {@code MeasureTool} or {@code MeasurementsAdapter}.
 */
class View2dCobbMeasureLabelTest {

  @Test
  void cobbLabelOnSyntheticCtUsesInstanceSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct_iso_050.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    CobbToolGraphic cobb = perpendicularEndplates();
    assertEquals("90.0°  5.0 mm / 5.0 mm", view.formatCobbMeasureLabel(cobb));
  }

  @Test
  void cobbLabelAfterPagingFollowsNewInstance(@TempDir Path dir) throws Exception {
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
    CobbToolGraphic cobb = perpendicularEndplates();
    assertEquals("90.0°  8.0 mm / 8.0 mm", view.formatCobbMeasureLabel(cobb));

    view.setFrameIndex(1);
    assertEquals("90.0°  5.0 mm / 5.0 mm", view.formatCobbMeasureLabel(cobb));
  }

  @Test
  void cobbLabelOnDxCarriesDetectorWarning(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxImager020(dir.resolve("dx_imager_020.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    assertEquals(
        "90.0°  2.0 mm / 2.0 mm (detector plane)",
        view.formatCobbMeasureLabel(perpendicularEndplates()));
  }

  @Test
  void cobbLabelOnDxEstimateCarriesCaveat(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxErmf12(dir.resolve("dx_ermf_12.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    String label = view.formatCobbMeasureLabel(perpendicularEndplates());
    assertTrue(label.contains("1.25 mm"));
    assertTrue(label.toLowerCase().contains("estimate"));
  }

  @Test
  void cobbLabelWithoutSpacingIsPixelsOnly(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("ct_nospacing.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    String label = view.formatCobbMeasureLabel(perpendicularEndplates());
    assertEquals("90.0°  10.0 px / 10.0 px", label);
    assertFalse(label.toLowerCase().contains("mm"));
  }

  @Test
  void cobbLabelDegreesUnchangedWhenInstanceSpacingChanges(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    CobbToolGraphic cobb = perpendicularEndplates();
    String withSpacing = view.formatCobbMeasureLabel(cobb);
    assertTrue(withSpacing.startsWith("90.0°"));

    File noSp = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("nosp.dcm").toFile());
    view.load(noSp);
    String withoutSpacing = view.formatCobbMeasureLabel(cobb);
    assertTrue(withoutSpacing.startsWith("90.0°"));
    assertFalse(withoutSpacing.toLowerCase().contains("mm"));
  }

  @Test
  void manualCalibrationDoublesDoNotChangeCobbLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    CobbToolGraphic cobb = perpendicularEndplates();
    String before = view.formatCobbMeasureLabel(cobb);
    view.setMonitorCalibrationMmPerPixel(9.9);
    view.setSessionManualCalibrationMmPerPixel(9.9);
    assertEquals(before, view.formatCobbMeasureLabel(cobb));
  }

  /** Upper endplate horizontal 10 px; lower endplate horizontal 10 px → 90° Cobb in pixel space. */
  private static CobbToolGraphic perpendicularEndplates() {
    CobbToolGraphic cobb = new CobbToolGraphic();
    cobb.setHandlePoint(0, new Point2D.Double(0, 0));
    cobb.setHandlePoint(1, new Point2D.Double(10, 0));
    cobb.setHandlePoint(2, new Point2D.Double(5, 10));
    cobb.setHandlePoint(3, new Point2D.Double(5, 0));
    return cobb;
  }
}
