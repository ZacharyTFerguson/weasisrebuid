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

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.dicom.codec.utils.RoiStatistics;

/**
 * View2d measurement labels: strings only (no new Swing widget), formatted after slices 1–4 proved
 * spacing, DX magnification, ROI HU, and instance paging.
 *
 * <p><b>Why UI is last:</b> {@link MeasurementLabel} only pretty-prints numbers that already passed
 * unit tests on {@link InstanceSpacing#resolve}, {@link LineGraphic#getLengthMm}, and {@link
 * RoiStatistics#ellipse}. Building labels first would let a hard-coded {@code 0.80} mm or a
 * paint-buffer mean ship while the resolver still lied.
 *
 * <p><b>Why the label binds to {@code Resolved} / {@code RoiStats} only:</b> millimetres come from
 * {@link InstanceSpacing.Resolved#spacing()} plus {@link LineGraphic#getLengthMm}; HU and unit come
 * from {@link RoiStatistics.RoiStats} via {@link RoiStatistics#formatMeanLabel}. The formatter has
 * no {@code BufferedImage}, no {@link View2d}, and no dataset parameter on the ellipse path — it
 * cannot read window/level paint (0–255 grey) or re-resolve tags, so a W/L tweak cannot change
 * the ROI string.
 *
 * <p><b>Why {@code px} never coexists with {@code mm}:</b> when {@code InstanceSpacing.resolve}
 * is empty or spacing is unusable, the line label is pixel Euclidean length only; there is no
 * silent fallback to imager pitch on CT and no dual unit string.
 *
 * <p><b>Why the DX warning travels with the number:</b> detector-plane and estimate caveats live
 * on {@link InstanceSpacing.Resolved#warning()} and {@link InstanceSpacing.Source}; the line suffix
 * {@code (detector plane)} / {@code (estimate)} and the geometry banner both come from that
 * resolved value, not from a separate presence check that clears when (0018,1164) exists.
 *
 * <p><b>Why manual/monitor doubles stay inert:</b> {@link org.weasis.core.ui.editor.image.DefaultView2d}
 * keeps {@code monitorCalibrationMmPerPixel} and {@code sessionManualCalibrationMmPerPixel} unused
 * this run; labels use DICOM spacing only until a future slice defines precedence with its own red
 * test.
 *
 * <p><b>Why not copy Weasis:</b> upstream measure adapters mix calibration prefs, painted buffers,
 * and file-extracted spacing; this slice adds a thin formatter beside the existing oracle paint
 * path instead of porting {@code MeasureTool} / {@code MeasurementsAdapter}.
 */
class View2dMeasureLabelTest {

  @Test
  void lineLabelOnSyntheticCtUsesInstanceSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct_iso_050.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    LineGraphic line = horizontalLine(10);
    assertEquals("5.0 mm", view.formatLineMeasureLabel(line));
  }

  @Test
  void lineLabelAfterPagingFollowsNewInstance(@TempDir Path dir) throws Exception {
    Path pack = roundtripDir();
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
    view.loadStack(List.of(s01, iso));
    LineGraphic line = horizontalLine(10);
    assertEquals("8.0 mm", view.formatLineMeasureLabel(line));

    view.setFrameIndex(1);
    assertEquals("5.0 mm", view.formatLineMeasureLabel(line));
  }

  @Test
  void lineLabelOnDxCarriesDetectorWarning(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxImager020(dir.resolve("dx_imager_020.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    assertEquals("2.0 mm (detector plane)", view.formatLineMeasureLabel(horizontalLine(10)));

    Path pack = roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File chest = pack.resolve("dx_chest_pa_256.dcm").toFile();
    Assumptions.assumeTrue(chest.isFile());
    view.load(chest);
    assertEquals("1.5 mm (detector plane)", view.formatLineMeasureLabel(horizontalLine(10)));
  }

  @Test
  void lineLabelOnDxEstimateCarriesCaveat(@TempDir Path dir) throws Exception {
    File dx = MeasureLabelFixtures.writeDxErmf12(dir.resolve("dx_ermf_12.dcm").toFile());
    View2d view = new View2d();
    view.load(dx);
    String label = view.formatLineMeasureLabel(horizontalLine(10));
    assertTrue(label.contains("1.25 mm"));
    assertTrue(label.toLowerCase().contains("estimate"));
  }

  @Test
  void lineLabelWithoutSpacingIsPixelsOnly(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("ct_nospacing.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    String label = view.formatLineMeasureLabel(horizontalLine(10));
    assertEquals("10.0 px", label);
    assertFalse(label.toLowerCase().contains("mm"));
  }

  @Test
  void manualCalibrationDoublesDoNotChangeLabel(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    LineGraphic line = horizontalLine(10);
    String before = view.formatLineMeasureLabel(line);
    view.setMonitorCalibrationMmPerPixel(9.9);
    view.setSessionManualCalibrationMmPerPixel(9.9);
    assertEquals(before, view.formatLineMeasureLabel(line));
  }

  @Test
  void geometryWarningIsResolverTextNotPresenceCheck(@TempDir Path dir) throws Exception {
    File noSp = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("nosp.dcm").toFile());
    View2d view = new View2d();
    view.load(noSp);
    assertFalse(view.getGeometryWarning().isBlank());

    File dx = MeasureLabelFixtures.writeDxImager020(dir.resolve("dx.dcm").toFile());
    view.load(dx);
    assertTrue(view.getGeometryWarning().toLowerCase().contains("detector"));

    Path pack = roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File s01 = pack.resolve("ct_brain_ax_s01_256.dcm").toFile();
    Assumptions.assumeTrue(s01.isFile());
    view.load(s01);
    assertEquals("", view.getGeometryWarning());
  }

  @Test
  void ellipseLabelIsRoiStatisticsMeanAndUnit(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtRoiAir(dir.resolve("ct_roi_air.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Ellipse2D roi = new Ellipse2D.Double(2, 2, 12, 12);
    String expected =
        RoiStatistics.formatMeanLabel(RoiStatistics.ellipse(view.getDataset(), roi).orElseThrow());
    assertEquals(expected, view.formatEllipseMeasureLabel(roi));
    assertTrue(expected.contains("-1000"));
    assertTrue(expected.contains("HU"));

    view.setWindowLevel(2000, 500);
    view.render();
    assertEquals(expected, view.formatEllipseMeasureLabel(roi));
  }

  @Test
  void labelFormatterHasNoImageEntryPoint() {
    for (Method method : MeasurementLabel.class.getDeclaredMethods()) {
      if (!Modifier.isPublic(method.getModifiers())) {
        continue;
      }
      for (Class<?> param : method.getParameterTypes()) {
        assertFalse(
            param.getName().contains("BufferedImage"),
            () -> "MeasurementLabel must not take BufferedImage: " + method);
        assertFalse(
            param.equals(View2d.class),
            () -> "MeasurementLabel must not take View2d: " + method);
      }
    }
    assertFalse(
        Arrays.stream(MeasurementLabel.class.getDeclaredMethods())
            .anyMatch(
                m ->
                    Modifier.isPublic(m.getModifiers())
                        && m.getReturnType().getName().contains("BufferedImage")));
  }

  private static LineGraphic horizontalLine(double pixels) {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(pixels, 0));
    return line;
  }

  static Path roundtripDir() {
    Path module = Path.of(System.getProperty("basedir", System.getProperty("user.dir")));
    Path fromModule = module.resolve("../../testdata/weasis-roundtrip").normalize();
    if (Files.isDirectory(fromModule)) {
      return fromModule;
    }
    return module.resolve("../../../testdata/weasis-roundtrip").normalize();
  }
}
