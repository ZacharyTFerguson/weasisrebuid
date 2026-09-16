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
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.utils.RoiStatistics;

/**
 * Live ellipse ROI labels must expose the full {@link RoiStatistics.RoiStats} record already
 * computed by {@link RoiStatistics#ellipse} — not the mean-only {@link
 * RoiStatistics#formatMeanLabel} alias.
 *
 * <p><b>Why bind through {@link RoiStatistics#formatStatsLabel}:</b> min, max, and sample stdDev
 * are proved in {@link org.weasis.dicom.codec.utils.RoiStatisticsTest} on stored pixels via {@link
 * org.weasis.dicom.codec.utils.LutPipeline#modalityValue}. {@link MeasurementLabel#formatEllipse}
 * must delegate to that formatter so {@link View2d#formatEllipseMeasureLabel} cannot ship mean-only
 * text while the codec already holds the full record.
 *
 * <p><b>Why fail-closed:</b> {@code ModalityLUTSequence} on the instance → {@link
 * RoiStatistics#ellipse} empty → live label {@code ""}. Padding pixels are excluded from min/max/std
 * but counted in {@code excluded} when shown.
 *
 * <p><b>Why not copy Weasis:</b> upstream {@code MeasureTool} / WP-5 {@code PixelStatistics} sample
 * painted grey; this slice only formats the landed dataset stats string — no new sampler, no {@code
 * BufferedImage} on the label path.
 */
class View2dRegionStatsLabelTest {

  @Test
  void liveEllipseLabelEqualsFormatStatsLabel(@TempDir java.nio.file.Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtRoiAir(dir.resolve("ct_roi_air.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Ellipse2D roi = MeasureLabelFixtures.ctRoiAirEllipse();
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(view.getDataset(), roi).orElseThrow();
    String expected = RoiStatistics.formatStatsLabel(stats);
    assertEquals(expected, view.formatEllipseMeasureLabel(roi));
    assertEquals(expected, MeasurementLabel.formatEllipse(stats));

    view.setWindowLevel(2000, 500);
    view.render();
    assertEquals(expected, view.formatEllipseMeasureLabel(roi));
  }

  @Test
  void liveEllipseLabelIsNotMeanOnlyAlias(@TempDir java.nio.file.Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtRoiAir(dir.resolve("ct_roi_air.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Ellipse2D roi = MeasureLabelFixtures.ctRoiAirEllipse();
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(view.getDataset(), roi).orElseThrow();
    String live = view.formatEllipseMeasureLabel(roi);
    String meanOnly = RoiStatistics.formatMeanLabel(stats);
    assertNotEquals(meanOnly, live);
  }

  @Test
  void liveLabelContainsMinMaxStdDevFromRecord(@TempDir java.nio.file.Path dir) throws Exception {
    File ct = writeFourByFourVaryingDisk(dir.resolve("vary.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Ellipse2D roi = new Ellipse2D.Double(0.5, 0.5, 3, 3);
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(view.getDataset(), roi).orElseThrow();
    String live = view.formatEllipseMeasureLabel(roi);
    assertTrue(live.contains(formatOneDecimal(stats.min())));
    assertTrue(live.contains(formatOneDecimal(stats.max())));
    assertTrue(live.contains(formatOneDecimal(stats.stdDev())));
    assertTrue(live.contains(formatOneDecimal(stats.mean())));
    assertTrue(live.contains(stats.unit()));
    assertTrue(live.contains("n=" + stats.n()));
    assertNotEquals(stats.min(), stats.max(), 1e-6);
    assertTrue(stats.stdDev() > 0);
  }

  @Test
  void uniformDiskHasZeroStdDevInLabel(@TempDir java.nio.file.Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtRoiAir(dir.resolve("ct_roi_air.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Ellipse2D roi = MeasureLabelFixtures.ctRoiAirEllipse();
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(view.getDataset(), roi).orElseThrow();
    assertEquals(stats.min(), stats.max(), 1e-6);
    assertEquals(0.0, stats.stdDev(), 1e-6);
    String live = view.formatEllipseMeasureLabel(roi);
    assertTrue(live.contains(formatOneDecimal(0.0)));
  }

  @Test
  void stdDevInLabelMatchesSampleStd(@TempDir java.nio.file.Path dir) throws Exception {
    File ct = writeFourByFourVaryingDisk(dir.resolve("vary.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Ellipse2D roi = new Ellipse2D.Double(0.5, 0.5, 3, 3);
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(view.getDataset(), roi).orElseThrow();
    double expectedStd = stats.stdDev();
    assertEquals(18.257418550496073, expectedStd, 1e-6);
    assertTrue(view.formatEllipseMeasureLabel(roi).contains(formatOneDecimal(expectedStd)));
  }

  @Test
  void paddingShowsExcludedWhenPositive(@TempDir java.nio.file.Path dir) throws Exception {
    File ct = writeFourByFourWithPadding(dir.resolve("pad.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Ellipse2D roi = new Ellipse2D.Double(0, 0, 2, 2);
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(view.getDataset(), roi).orElseThrow();
    assertEquals(1, stats.excluded());
    String live = view.formatEllipseMeasureLabel(roi);
    assertTrue(live.contains("excluded=1"));
    assertTrue(live.contains(formatOneDecimal(100.0)));
  }

  @Test
  void modalityLutSequenceGivesEmptyLiveLabel(@TempDir java.nio.file.Path dir) throws Exception {
    File ct = writeFourByFourWithModalityLut(dir.resolve("modlut.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    Ellipse2D roi = new Ellipse2D.Double(0, 0, 4, 4);
    assertEquals("", view.formatEllipseMeasureLabel(roi));
  }

  @Test
  void roiStatisticsFormatterHasNoImageEntryPoint() {
    for (Method method : RoiStatistics.class.getDeclaredMethods()) {
      if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
        continue;
      }
      for (Class<?> param : method.getParameterTypes()) {
        assertFalse(
            param.getName().contains("BufferedImage"),
            () -> "RoiStatistics must not take BufferedImage: " + method);
      }
    }
  }

  private static String formatOneDecimal(double value) {
    return String.format(Locale.US, "%.1f", value);
  }

  private static File writeFourByFourVaryingDisk(File dest) throws Exception {
    Attributes dcm = fourByFourBase();
    int[] px = dcm.getInts(Tag.PixelData);
    px[5] = 10;
    px[6] = 30;
    px[9] = 50;
    px[10] = 70;
    dcm.setInt(Tag.PixelData, VR.OW, px);
    writePart10(dest, dcm);
    return dest;
  }

  private static File writeFourByFourWithPadding(File dest) throws Exception {
    Attributes dcm = fourByFourBase();
    dcm.setInt(Tag.PixelPaddingValue, VR.US, 555);
    int[] px = dcm.getInts(Tag.PixelData);
    px[0] = 555;
    px[1] = 100;
    px[4] = 100;
    px[5] = 100;
    dcm.setInt(Tag.PixelData, VR.OW, px);
    writePart10(dest, dcm);
    return dest;
  }

  private static File writeFourByFourWithModalityLut(File dest) throws Exception {
    Attributes dcm = fourByFourBase();
    Sequence seq = dcm.newSequence(Tag.ModalityLUTSequence, 1);
    seq.add(new Attributes());
    writePart10(dest, dcm);
    return dest;
  }

  private static Attributes fourByFourBase() {
    String sop = UIDUtils.createUID("2.25");
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.Rows, VR.US, 4);
    dcm.setInt(Tag.Columns, VR.US, 4);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.PixelData, VR.OW, new int[16]);
    return dcm;
  }

  private static void writePart10(File dest, Attributes dcm) throws Exception {
    Attributes fmi = new Attributes();
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, dcm.getString(Tag.SOPClassUID));
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, dcm.getString(Tag.SOPInstanceUID));
    try (DicomOutputStream out =
        new DicomOutputStream(new FileOutputStream(dest), UID.ExplicitVRLittleEndian)) {
      out.writeDataset(fmi, dcm);
    }
  }
}
