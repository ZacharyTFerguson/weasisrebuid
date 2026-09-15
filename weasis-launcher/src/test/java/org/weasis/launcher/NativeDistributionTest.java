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

import static org.junit.jupiter.api.Assertions.assertFalse;
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

class NativeDistributionTest {

  @Test
  void zipIsAPortableTreeNotAMavenCacheDump(@TempDir Path dir) throws IOException {
    Path launcher = dir.resolve("launcher.jar");
    Files.writeString(launcher, "jar-bytes", StandardCharsets.UTF_8);
    Path etc = dir.resolve("etc");
    Files.createDirectories(etc);
    Files.writeString(
        etc.resolve("base.json"), "{\"weasisPreferences\":[]}", StandardCharsets.UTF_8);
    Path bin = dir.resolve("bin");
    Files.createDirectories(bin);
    Files.writeString(bin.resolve("Dicomizer"), "#!/bin/sh\n", StandardCharsets.UTF_8);
    Path zip = dir.resolve(NativeDistribution.ZIP_NAME);
    NativeDistribution.write(zip, launcher, etc, bin);
    assertTrue(Files.isRegularFile(zip));
    Set<String> names = new HashSet<>();
    try (ZipInputStream in = new ZipInputStream(Files.newInputStream(zip))) {
      ZipEntry e;
      while ((e = in.getNextEntry()) != null) {
        names.add(e.getName());
      }
    }
    assertTrue(names.contains(NativeDistribution.LAUNCHER_JAR));
    assertTrue(names.contains("etc/config/base.json"));
    assertTrue(names.contains("bin/Dicomizer"));
    for (String name : names) {
      assertFalse(name.contains(".m2"), name);
      assertFalse(name.startsWith("repository/"), name);
    }
  }
}
