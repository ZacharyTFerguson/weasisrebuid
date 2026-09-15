/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
<<<<<<< HEAD
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.image.measure.ImageSpacing;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.dicom.codec.utils.InstanceSpacing.Resolved;
import org.weasis.dicom.codec.utils.InstanceSpacing.Source;
=======
>>>>>>> c54f4bc (fix(test): roundtrip path helper without cross-package access)

/**
 * Why (0028,0030) PixelSpacing / (0018,1164) ImagerPixelSpacing: PS3.3 row-first patient spacing vs
 * detector pitch; DX magnification divides detector pitch by M when evidence exists.
 *
 * <p>Why fail-closed: bad PixelSpacing → empty; no mag → detector mm + warn; ERMF vs SID/SOD
 * conflict → no silent object mm; mag present still warns (SOD ≠ lesion).
 *
 * <p>Why not copy Weasis: precedence and axis map are derived from the standard, not upstream
 * calibration helpers.
 *
 * <p>Why this fixture: round-trip CTs share isotropic 0.80 mm; anisotropic synthetics reject a
 * hard-coded 0.80 in production code.
 */
class InstanceSpacingTest {

  // --- Slice 1 (CT/MR PixelSpacing) — landed with #27 ---

  @Test
  void ctFixturesResolveRowAndColumnFromPixelSpacing() throws Exception {
    Path pack = roundtripDir();
    Assumptions.assumeTrue(
        Files.isDirectory(pack),
        () -> "round-trip pack missing at " + pack + " (clone testdata/weasis-roundtrip)");
    for (String name : new String[] {"ct_brain_ax_s01_256.dcm", "ct_brain_ax_s02_256.dcm"}) {
      Attributes dcm = readDataset(pack.resolve(name));
      Resolved resolved = InstanceSpacing.resolve(dcm).orElseThrow();
      assertEquals(Source.PIXEL_SPACING, resolved.source());
      assertEquals(0.80, resolved.spacing().rowMm(), 1e-9);
      assertEquals(0.80, resolved.spacing().colMm(), 1e-9);
      assertTrue(resolved.warning().isEmpty());
    }
  }

  @Test
  void syntheticAnisotropicRowIsFirstColumnIsSecond(@TempDir Path dir) throws Exception {
    File file = dir.resolve("aniso.dcm").toFile();
    SyntheticDicomFixtures.writeCtWithPixelSpacing(file, 0.50, 0.25);
    Attributes dcm = readDataset(file.toPath());
    ImageSpacing spacing = InstanceSpacing.resolve(dcm).orElseThrow().spacing();
    assertEquals(0.50, spacing.rowMm(), 1e-9);
    assertEquals(0.25, spacing.colMm(), 1e-9);
  }

  @Test
  void twoInstancesWithDifferentSpacingGiveDifferentMm(@TempDir Path dir) throws Exception {
    File a = dir.resolve("a.dcm").toFile();
    File b = dir.resolve("b.dcm").toFile();
    SyntheticDicomFixtures.writeCtWithPixelSpacing(a, 0.50, 0.50);
    SyntheticDicomFixtures.writeCtWithPixelSpacing(b, 1.00, 1.00);
    ImageSpacing spacingA =
        InstanceSpacing.resolve(readDataset(a.toPath())).orElseThrow().spacing();
    ImageSpacing spacingB =
        InstanceSpacing.resolve(readDataset(b.toPath())).orElseThrow().spacing();
    double mmA = horizontalLineMm(spacingA, 10);
    double mmB = horizontalLineMm(spacingB, 10);
    assertEquals(5.0, mmA, 1e-9);
    assertEquals(10.0, mmB, 1e-9);
  }

  @Test
  void singleValuePixelSpacingIsUnusable() {
    Attributes dcm = SyntheticDicomFixtures.monochrome2Dataset(UIDUtils.createUID("2.25"), 4, 4);
    dcm.setDouble(Tag.PixelSpacing, VR.DS, 0.5);
    assertTrue(InstanceSpacing.resolve(dcm).isEmpty());
  }

  @Test
  void zeroNegativeOrNaNSpacingIsUnusable() {
    Attributes dcm = SyntheticDicomFixtures.monochrome2Dataset(UIDUtils.createUID("2.25"), 4, 4);
    dcm.setDouble(Tag.PixelSpacing, VR.DS, 0.5, 0.0);
    assertTrue(InstanceSpacing.resolve(dcm).isEmpty());

    dcm = SyntheticDicomFixtures.monochrome2Dataset(UIDUtils.createUID("2.25"), 4, 4);
    dcm.setDouble(Tag.PixelSpacing, VR.DS, -0.1, 0.5);
    assertTrue(InstanceSpacing.resolve(dcm).isEmpty());

    dcm = SyntheticDicomFixtures.monochrome2Dataset(UIDUtils.createUID("2.25"), 4, 4);
    dcm.setDouble(Tag.PixelSpacing, VR.DS, 0.5, Double.NaN);
    assertTrue(InstanceSpacing.resolve(dcm).isEmpty());
  }

  @Test
  void noSpacingTagsGivesEmpty() {
    Attributes dcm = SyntheticDicomFixtures.monochrome2Dataset(UIDUtils.createUID("2.25"), 4, 4);
    assertTrue(InstanceSpacing.resolve(dcm).isEmpty());
  }

  // --- Slice 2 (DX/CR imager + magnification) ---

  @Test
  void dxFixturesImagerOnlyAreDetectorMmWithWarning() throws Exception {
    Path pack = roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    for (String name :
        new String[] {"dx_chest_pa_256.dcm", "dx_hand_pa_256.dcm", "dx_knee_ap_256.dcm"}) {
      Path file = pack.resolve(name);
      Assumptions.assumeTrue(Files.isRegularFile(file), () -> "missing " + file);
      Attributes dcm = readDataset(file);
      var resolved = InstanceSpacing.resolve(dcm).orElseThrow();
      assertEquals(0.15, resolved.spacing().rowMm(), 1e-9);
      assertEquals(0.15, resolved.spacing().colMm(), 1e-9);
      assertEquals(Source.IMAGER_DETECTOR, resolved.source());
      assertFalse(resolved.warning().isBlank());
    }
  }

  @Test
  void dxEstimatedMagnificationDividesDetectorPitch() {
    Attributes dcm = dxImager015();
    dcm.setDouble(Tag.EstimatedRadiographicMagnificationFactor, VR.DS, 1.2);
    var resolved = InstanceSpacing.resolve(dcm).orElseThrow();
    assertEquals(0.125, resolved.spacing().rowMm(), 1e-9);
    assertEquals(0.125, resolved.spacing().colMm(), 1e-9);
    assertEquals(Source.IMAGER_OBJECT_ESTIMATE, resolved.source());
    assertFalse(resolved.warning().isBlank());
  }

  @Test
  void dxSidSodComputesMagnificationWhenFactorAbsent() {
    Attributes dcm = dxImager015();
    dcm.setDouble(Tag.DistanceSourceToDetector, VR.DS, 1000);
    dcm.setDouble(Tag.DistanceSourceToPatient, VR.DS, 800);
    var resolved = InstanceSpacing.resolve(dcm).orElseThrow();
    assertEquals(0.12, resolved.spacing().rowMm(), 1e-9);
    assertEquals(0.12, resolved.spacing().colMm(), 1e-9);
    assertEquals(Source.IMAGER_OBJECT_ESTIMATE, resolved.source());
    assertFalse(resolved.warning().isBlank());
  }

  @Test
  void dxSidWithoutSodStaysDetectorWithWarning() {
    Attributes dcm = dxImager015();
    dcm.setDouble(Tag.DistanceSourceToDetector, VR.DS, 1000);
    var resolved = InstanceSpacing.resolve(dcm).orElseThrow();
    assertEquals(0.15, resolved.spacing().rowMm(), 1e-9);
    assertEquals(Source.IMAGER_DETECTOR, resolved.source());
    assertFalse(resolved.warning().isBlank());
  }

  @Test
  void dxMagnificationBelowOneIsRejected() {
    Attributes dcm = dxImager015();
    dcm.setDouble(Tag.EstimatedRadiographicMagnificationFactor, VR.DS, 0.9);
    var resolved = InstanceSpacing.resolve(dcm).orElseThrow();
    assertEquals(0.15, resolved.spacing().rowMm(), 1e-9);
    assertEquals(Source.IMAGER_DETECTOR, resolved.source());
    assertFalse(resolved.warning().isBlank());
  }

  @Test
  void dxPixelSpacingPresentWinsAndReportsCalibrationType() {
    Attributes dcm = dxImager015();
    dcm.setString(Tag.PixelSpacing, VR.DS, "0.20\\0.20");
    dcm.setString(Tag.PixelSpacingCalibrationType, VR.CS, "GEOMETRY");
    dcm.setDouble(Tag.EstimatedRadiographicMagnificationFactor, VR.DS, 1.5);
    var resolved = InstanceSpacing.resolve(dcm).orElseThrow();
    assertEquals(0.20, resolved.spacing().rowMm(), 1e-9);
    assertEquals(Source.PIXEL_SPACING_CALIBRATED, resolved.source());
    assertTrue(resolved.note().contains("GEOMETRY"));
  }

  @Test
  void dxErmfAndSidSodDisagree_staysDetectorWithConflictWarning() {
    Attributes dcm = dxImager015();
    dcm.setDouble(Tag.EstimatedRadiographicMagnificationFactor, VR.DS, 1.2);
    dcm.setDouble(Tag.DistanceSourceToDetector, VR.DS, 1000);
    dcm.setDouble(Tag.DistanceSourceToPatient, VR.DS, 800);
    var resolved = InstanceSpacing.resolve(dcm).orElseThrow();
    assertEquals(0.15, resolved.spacing().rowMm(), 1e-9);
    assertEquals(Source.IMAGER_DETECTOR, resolved.source());
    assertTrue(resolved.warning().contains(InstanceSpacing.MAG_CONFLICT));
  }

  @Test
  void dxPixelSpacingNotDividedByMagnification() {
    Attributes dcm = dxImager015();
    dcm.setString(Tag.PixelSpacing, VR.DS, "0.20\\0.20");
    dcm.setDouble(Tag.EstimatedRadiographicMagnificationFactor, VR.DS, 1.5);
    var resolved = InstanceSpacing.resolve(dcm).orElseThrow();
    assertEquals(0.20, resolved.spacing().colMm(), 1e-9);
    assertEquals(Source.PIXEL_SPACING, resolved.source());
  }

  @Test
  void dxEstimatedObjectKeepsCaveat() {
    Attributes dcm = dxImager015();
    dcm.setDouble(Tag.EstimatedRadiographicMagnificationFactor, VR.DS, 1.2);
    var resolved = InstanceSpacing.resolve(dcm).orElseThrow();
    assertEquals(Source.IMAGER_OBJECT_ESTIMATE, resolved.source());
    assertFalse(resolved.warning().isBlank());
    assertTrue(
        resolved.warning().toLowerCase().contains("estimate")
            || resolved.warning().toLowerCase().contains("sod"));
  }

  private static double horizontalLineMm(ImageSpacing spacing, double pixels) {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(pixels, 0));
    return line.getLengthMm(spacing).orElseThrow();
  }

  static Attributes dxImager015() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "DX");
    dcm.setString(Tag.ImagerPixelSpacing, VR.DS, "0.15\\0.15");
    return dcm;
  }

  static Path roundtripDir() {
<<<<<<< HEAD
    Path module = Path.of(System.getProperty("basedir", System.getProperty("user.dir")));
    Path fromModule = module.resolve("../../testdata/weasis-roundtrip").normalize();
    if (Files.isDirectory(fromModule)) {
      return fromModule;
    }
    return module.resolve("../../../testdata/weasis-roundtrip").normalize();
  }

  static Attributes readDataset(Path path) throws Exception {
    try (DicomInputStream in = new DicomInputStream(path.toFile())) {
=======
    Path module = Path.of(System.getProperty("user.dir"));
    return module.resolve("../../../testdata/weasis-roundtrip").normalize();
  }

  static Attributes readDataset(Path file) throws Exception {
    try (DicomInputStream in = new DicomInputStream(file.toFile())) {
>>>>>>> c54f4bc (fix(test): roundtrip path helper without cross-package access)
      in.setIncludeBulkData(DicomInputStream.IncludeBulkData.NO);
      return in.readDataset(-1, -1);
    }
  }
}
