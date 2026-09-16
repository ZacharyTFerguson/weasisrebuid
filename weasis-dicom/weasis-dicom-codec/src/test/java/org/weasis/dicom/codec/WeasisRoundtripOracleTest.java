/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Pixel-honest conformance for the five-file Dicom Light TS round-trip pack. Samples must match
 * {@code testdata/weasis-roundtrip/cross-oracle-report.json} (Weasis column).
 */
class WeasisRoundtripOracleTest {

  record Expected(
      String file, String sha256, int x0y0, int center, double window, double level, int rows) {}

  static Stream<Expected> pack() {
    return Stream.of(
        new Expected(
            "dx_chest_pa_256.dcm",
            "1d5b1dc76cb9a9502347dd0431d8f7e5df0c4b9152e985cb7fc1ce429b1601dd",
            102,
            193,
            2505,
            1748,
            256),
        new Expected(
            "dx_hand_pa_256.dcm",
            "3f2e9f35631b7451b839dd623c7b995c507c78965b75e598dda6e577f142f308",
            2,
            253,
            2634,
            1640,
            256),
        new Expected(
            "dx_knee_ap_256.dcm",
            "a05fc6e293a873534de3864688e2d6267b8ec5e4f719e62bcea5cdfb1c888e35",
            7,
            25,
            2634,
            1640,
            256),
        new Expected(
            "ct_brain_ax_s01_256.dcm",
            "c1e1828cad619e521c23ae8a16461543f26da818ba48e9dab7886424626dac7c",
            0,
            92,
            80,
            40,
            256),
        new Expected(
            "ct_brain_ax_s02_256.dcm",
            "b8457603c3765cd220d98421a7f480f3ab375a22f05bd41ac1dbedebfdb9d9fb",
            0,
            99,
            80,
            40,
            256));
  }

  @ParameterizedTest
  @MethodSource("pack")
  void oracleMatchesPinnedCrossOracleSamples(Expected expected) throws Exception {
    Path packDir = roundtripDir();
    Assumptions.assumeTrue(
        Files.isDirectory(packDir),
        () -> "round-trip pack missing at " + packDir + " (clone testdata/weasis-roundtrip)");
    Path file = packDir.resolve(expected.file());
    Assumptions.assumeTrue(Files.isRegularFile(file), () -> "missing " + file);

    assertEquals(expected.sha256(), sha256(file), "byte identity vs manifest");

    DicomUnderstandingOracle.Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertTrue(v.understood());
    assertEquals(DicomUnderstandingOracle.ACCEPTED, v.disposition());
    assertEquals(expected.rows(), v.rows());
    assertEquals(expected.rows(), v.columns());
    assertEquals(expected.window(), v.window(), 1e-6);
    assertEquals(expected.level(), v.level(), 1e-6);
    assertNotNull(v.samples());
    assertEquals(expected.x0y0(), v.samples().x0y0());
    assertEquals(expected.center(), v.samples().center());
    assertNull(v.reason());

    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    assertEquals(
        0,
        DicomUnderstandingOracle.run(
            new String[] {file.toString()},
            new PrintStream(stdout, true, StandardCharsets.UTF_8),
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8)));
  }

  static Path roundtripDir() {
    Path module = Path.of(System.getProperty("user.dir"));
    return module.resolve("../../../testdata/weasis-roundtrip").normalize();
  }

  static String sha256(Path file) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(Files.readAllBytes(file));
    return HexFormat.of().formatHex(md.digest());
  }
}
