/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AcquireMetaDestGroupExifTest {

  @Test
  void metaSetsAndRequired() {
    Properties p = new Properties();
    p.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.REQUIRED),
        "PatientName,PatientID");
    p.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.DISPLAY),
        "PatientName,PatientID,AccessionNumber");
    assertTrue(AcquireMeta.required(p, AcquireMeta.Scope.GLOBAL, "PatientID"));
    assertFalse(AcquireMeta.required(p, AcquireMeta.Scope.GLOBAL, "Modality"));
    assertEquals(
        3, AcquireMeta.tokens(p, AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.DISPLAY).size());
  }

  @Test
  void destLockedWhenAcquireDestSet() {
    Properties empty = new Properties();
    assertFalse(AcquireDest.destinationLocked(empty));
    assertEquals("DCM4CHEE", AcquireDest.aet(empty));
    assertEquals(11112, AcquireDest.port(empty));
    Properties locked = new Properties();
    locked.setProperty("weasis.acquire.dest.host", "localhost");
    locked.setProperty("weasis.acquire.dest.aet", "DCM4CHEE");
    locked.setProperty("weasis.acquire.dest.port", "11112");
    assertTrue(AcquireDest.destinationLocked(locked));
  }

  @Test
  void groupingNoneDateName(@TempDir Path dir) throws Exception {
    Path a = dir.resolve("seriesA1.png");
    Path b = dir.resolve("seriesA2.png");
    Path c = dir.resolve("other.png");
    Files.writeString(a, "a");
    Files.writeString(b, "b");
    Files.writeString(c, "c");
    List<List<Path>> none = ImportGrouping.group(List.of(a, b, c), ImportGrouping.NONE, null);
    assertEquals(3, none.size());
    List<List<Path>> byName = ImportGrouping.group(List.of(a, b, c), ImportGrouping.NAME, null);
    assertTrue(byName.stream().anyMatch(g -> g.size() == 2));
    List<List<Path>> byDate =
        ImportGrouping.group(List.of(a, b, c), ImportGrouping.DATE, Duration.ofDays(1));
    assertEquals(1, byDate.size());
  }

  @Test
  void exifInvalidDateFallsBackToFileMtime() {
    Instant now = Instant.parse("2026-01-15T00:00:00Z");
    Instant file = Instant.parse("2026-01-01T00:00:00Z");
    Instant future = now.plus(Duration.ofDays(10));
    Instant ancient = now.minus(Duration.ofDays(365L * 40L));
    var futureMap =
        ExifMapper.map(new ExifMapper.ExifTags(1, "c", "Acme", "Cam", future, null), file, now);
    assertEquals(file, futureMap.contentDateTime());
    var ancientMap =
        ExifMapper.map(new ExifMapper.ExifTags(1, "c", "Acme", "Cam", ancient, null), file, now);
    assertEquals(file, ancientMap.contentDateTime());
    Instant ok = Instant.parse("2025-06-01T12:00:00Z");
    var okMap =
        ExifMapper.map(new ExifMapper.ExifTags(6, "note", "Acme", "Cam", ok, null), file, now);
    assertEquals(ok, okMap.contentDateTime());
    assertEquals("note", okMap.imageComments());
    assertEquals("Cam", okMap.manufacturerModelName());
  }
}
