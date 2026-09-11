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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PackageWeasisScriptTest {

  @Test
  void packageWeasisShZipsWithoutMavenRepo(@TempDir Path temp) throws Exception {
    Path from = temp.resolve("stage");
    Files.createDirectories(from.resolve("bundle"));
    Files.createDirectories(from.resolve("i18n"));
    Files.createDirectories(from.resolve("conf"));
    Files.writeString(from.resolve("Weasis.jar"), "launcher");
    Files.writeString(from.resolve("bundle/weasis-core-4.7.3.jar"), "core");
    Files.writeString(from.resolve("i18n/org.weasis.core.nl_fr.jar"), "fr");
    Files.writeString(
        from.resolve("conf/base.json"),
        """
        {
          "weasisPreferences": [
            {"code": "org.osgi.framework.startlevel.beginning", "value": "130", "type": "A", "category": "FELIX_CONFIG"},
            {"code": "felix.startlevel.bundle", "value": "300", "type": "A", "category": "FELIX_CONFIG"},
            {"code": "felix.auto.start.12", "value": "file:${maven.localRepository}/org/weasis/core/weasis-core/4.7.3/weasis-core-4.7.3.jar", "type": "A", "category": "FELIX_INSTALL"},
            {"code": "maven.local.repo", "value": "${settings.localRepository}", "type": "A", "category": "LAUNCH"},
            {"code": "locale.lang.code", "value": "en", "type": "F", "category": "UI"},
            {"code": "weasis.portable.dicom.directory", "value": "dicom,DICOM,IMAGES,images", "type": "A", "category": "LAUNCH"}
          ]
        }
        """);

    Path script =
        Mx03ShippingPrefsTest.moduleRoot()
            .resolve("../weasis-distributions/script/package-weasis.sh");
    assertTrue(Files.isRegularFile(script), script.toString());
    Path zip = temp.resolve("weasis-native.zip");
    Path fakeJdk = temp.resolve("jdk");
    Files.createDirectories(fakeJdk.resolve("bin"));
    Path javaBin = fakeJdk.resolve("bin/java");
    Files.writeString(javaBin, "#!/bin/sh\nexit 0\n");
    Files.setPosixFilePermissions(
        javaBin,
        Set.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.OWNER_EXECUTE));

    ProcessBuilder pb =
        new ProcessBuilder(
            "bash",
            script.toString(),
            "--jdk",
            fakeJdk.toString(),
            "--from-target",
            from.toString(),
            "--output",
            zip.toString(),
            "--skip-jpackage");
    pb.redirectErrorStream(true);
    Process process = pb.start();
    String log = new String(process.getInputStream().readAllBytes());
    assertEquals(0, process.waitFor(), log);
    assertTrue(log.contains("NATIVE_ZIP_OK"), log);
    assertTrue(Files.isRegularFile(zip));

    try (ZipFile zf = new ZipFile(zip.toFile())) {
      assertTrue(zf.getEntry("weasis-native/Weasis.jar") != null);
      assertTrue(zf.getEntry("weasis-native/resources/bundle/weasis-core-4.7.3.jar") != null);
      assertTrue(zf.getEntry("weasis-native/resources/i18n/org.weasis.core.nl_fr.jar") != null);
      assertTrue(zf.getEntry("weasis-native/resources/conf/base.json") != null);
      assertTrue(zf.getEntry("weasis-native/Weasis.sh") != null);
      assertTrue(
          zf.getEntry("weasis-native/dicom/") != null
              || zf.getEntry("weasis-native/dicom") != null);
    }

    Path unpacked = temp.resolve("unpacked");
    try (ZipFile zf = new ZipFile(zip.toFile())) {
      zf.stream()
          .forEach(
              entry -> {
                try {
                  Path out = unpacked.resolve(entry.getName());
                  if (entry.isDirectory()) {
                    Files.createDirectories(out);
                    return;
                  }
                  Files.createDirectories(out.getParent());
                  try (var in = zf.getInputStream(entry)) {
                    Files.copy(in, out);
                  }
                } catch (java.io.IOException e) {
                  throw new java.io.UncheckedIOException(e);
                }
              });
    }
    Path nativeJson = unpacked.resolve("weasis-native/resources/conf/base.json");
    String json = Files.readString(nativeJson);
    assertTrue(json.contains("${weasis.resources.path}/bundle/weasis-core-4.7.3.jar"));
    assertFalse(json.contains("felix.auto.start.12") && json.contains("maven.localRepository"));
    assertTrue(json.contains("weasis.i18n.dir"));
    Path resources = unpacked.resolve("weasis-native/resources");
    System.setProperty(NativeConfigRewriter.RESOURCES_PATH, resources.toString());
    try {
      ConfigData data = ConfigData.load(nativeJson, Map.of("app.version", "4.7.3"));
      Path jar = data.autoBundles().getFirst().files().getFirst();
      assertTrue(Files.isRegularFile(jar), jar.toString());
      assertFalse(jar.toString().contains(".m2"));
    } finally {
      System.clearProperty(NativeConfigRewriter.RESOURCES_PATH);
    }
  }

  @Test
  void packageWeasisShRefusesM2FromTarget(@TempDir Path temp) throws Exception {
    Path script =
        Mx03ShippingPrefsTest.moduleRoot()
            .resolve("../weasis-distributions/script/package-weasis.sh");
    Path fakeJdk = temp.resolve("jdk");
    Files.createDirectories(fakeJdk.resolve("bin"));
    Path javaBin = fakeJdk.resolve("bin/java");
    Files.writeString(javaBin, "#!/bin/sh\n");
    Files.setPosixFilePermissions(
        javaBin,
        Set.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_EXECUTE,
            PosixFilePermission.OWNER_WRITE));
    Path m2 = temp.resolve(".m2/repository/stage");
    Files.createDirectories(m2);
    ProcessBuilder pb =
        new ProcessBuilder(
            "bash",
            script.toString(),
            "--jdk",
            fakeJdk.toString(),
            "--from-target",
            m2.toString(),
            "--output",
            temp.resolve("x.zip").toString(),
            "--skip-jpackage");
    pb.redirectErrorStream(true);
    Process process = pb.start();
    String log = new String(process.getInputStream().readAllBytes());
    assertEquals(2, process.waitFor(), log);
    assertTrue(log.contains("refusing") || log.contains(".m2"), log);
  }
}
