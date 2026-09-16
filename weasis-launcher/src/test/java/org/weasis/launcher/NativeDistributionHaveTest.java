/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NativeDistributionHaveTest {

  @Test
  void compressXzProfileWritesPortableWeasisNativeZip(@TempDir Path dir) throws IOException {
    Path module = Mx03ShippingPrefsTest.moduleRoot();
    Path dist = module.resolve("../weasis-distributions").normalize();
    Path pom = dist.resolve("pom.xml");
    Path rootPom = module.resolve("../pom.xml").normalize();
    String profile = Files.readString(pom, StandardCharsets.UTF_8);
    String reactor = Files.readString(rootPom, StandardCharsets.UTF_8);
    assertTrue(profile.contains("<id>compressXZ</id>"), profile);
    assertTrue(profile.contains("org.weasis.launcher.NativeDistribution"), profile);
    assertTrue(profile.contains(NativeDistribution.ZIP_NAME), profile);
    assertTrue(profile.contains("app.version"), profile);
    assertFalse(reactor.contains("<module>weasis-distributions</module>"), reactor);

    Path launcher = dir.resolve("launcher.jar");
    Files.writeString(launcher, "launcher", StandardCharsets.UTF_8);
    Path zip = dir.resolve(NativeDistribution.ZIP_NAME);
    NativeDistribution.write(
        zip, launcher, dist.resolve("etc/config"), dist.resolve("bin"), "4.7.3");
    assertEquals(NativeDistribution.ZIP_NAME, zip.getFileName().toString());
    assertTrue(Files.isRegularFile(zip));
    Set<String> names = zipNames(zip);
    assertTrue(names.contains(NativeDistribution.LAUNCHER_JAR), names.toString());
    assertTrue(names.contains("etc/config/base.json"), names.toString());
    assertTrue(names.contains("etc/config/dicomizer.json"), names.toString());
    assertTrue(names.contains("bin/Dicomizer"), names.toString());
    for (String name : names) {
      assertFalse(name.contains(".m2"), name);
      assertFalse(name.startsWith("repository/"), name);
    }
  }

  @Test
  void productionVersionMustNotBeSnapshot(@TempDir Path dir) throws IOException {
    Path launcher = dir.resolve("launcher.jar");
    Files.writeString(launcher, "launcher", StandardCharsets.UTF_8);
    Path zip = dir.resolve(NativeDistribution.ZIP_NAME);
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> NativeDistribution.write(zip, launcher, null, null, "4.7.3-SNAPSHOT"));
    assertTrue(ex.getMessage().contains("SNAPSHOT"), ex.getMessage());
    assertFalse(Files.exists(zip));
    NativeDistribution.refuseSnapshot("4.7.3");
  }

  static Set<String> zipNames(Path zip) throws IOException {
    Set<String> names = new HashSet<>();
    try (ZipInputStream in = new ZipInputStream(Files.newInputStream(zip))) {
      ZipEntry e;
      while ((e = in.getNextEntry()) != null) {
        names.add(e.getName());
      }
    }
    return names;
  }
}
