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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * WP-14 native zip. {@code mvn -P compressXZ -f weasis-distributions package} writes {@code
 * weasis-native.zip}. The zip is a portable tree, not a dump of the Maven cache. Production
 * versions must not be SNAPSHOT.
 */
public final class NativeDistribution {

  public static final String LAUNCHER_JAR = "weasis-launcher.jar";
  public static final String ZIP_NAME = "weasis-native.zip";

  private NativeDistribution() {}

  public static void main(String[] args) throws IOException {
    Path out = null;
    Path launcher = null;
    Path etc = null;
    Path bin = null;
    for (int i = 0; i < args.length; i++) {
      String a = args[i];
      if ("--out".equals(a) && i + 1 < args.length) {
        out = Path.of(args[++i]);
      } else if ("--launcher".equals(a) && i + 1 < args.length) {
        launcher = Path.of(args[++i]);
      } else if ("--etc".equals(a) && i + 1 < args.length) {
        etc = Path.of(args[++i]);
      } else if ("--bin".equals(a) && i + 1 < args.length) {
        bin = Path.of(args[++i]);
      }
    }
    if (out == null || launcher == null) {
      throw new IllegalArgumentException("Usage: --out zip --launcher jar [--etc dir] [--bin dir]");
    }
    write(out, launcher, etc, bin);
  }

  public static void write(Path zip, Path launcherJar, Path etcDir, Path binDir)
      throws IOException {
    write(zip, launcherJar, etcDir, binDir, System.getProperty("app.version"));
  }

  public static void write(Path zip, Path launcherJar, Path etcDir, Path binDir, String version)
      throws IOException {
    refuseSnapshot(version);
    requireZipAndLauncher(zip, launcherJar);
    Path parent = zip.getParent();
    Files.createDirectories(parent == null ? Path.of(".") : parent);
    try (OutputStream raw = Files.newOutputStream(zip);
        ZipOutputStream zos = new ZipOutputStream(raw)) {
      putFile(zos, LAUNCHER_JAR, launcherJar);
      putOptionalTree(zos, "etc/config", etcDir);
      putOptionalTree(zos, "bin", binDir);
    }
  }

  static void refuseSnapshot(String version) {
    if (version == null || version.isBlank()) {
      return;
    }
    if (snapshotToken(version)) {
      throw new IllegalArgumentException("production versions must not be SNAPSHOT: " + version);
    }
  }

  static boolean snapshotToken(String version) {
    return version.toUpperCase(Locale.ROOT).contains("SNAPSHOT");
  }

  static void requireZipAndLauncher(Path zip, Path launcherJar) {
    if (zip == null) {
      throw new IllegalArgumentException("zip");
    }
    if (launcherJar == null || !Files.isRegularFile(launcherJar)) {
      throw new IllegalArgumentException("launcher jar missing: " + launcherJar);
    }
  }

  static boolean looksLikeMavenCache(Path dir) {
    if (dir == null) {
      return false;
    }
    String p = dir.toAbsolutePath().toString().toLowerCase(Locale.ROOT);
    return p.contains("/.m2/repository") || p.endsWith("/repository");
  }

  static void putOptionalTree(ZipOutputStream zos, String prefix, Path dir) throws IOException {
    if (dir != null && Files.isDirectory(dir)) {
      putTree(zos, prefix, dir);
    }
  }

  static void putTree(ZipOutputStream zos, String prefix, Path root) throws IOException {
    try (Stream<Path> walk = Files.walk(root)) {
      for (Path p : walk.filter(Files::isRegularFile).toList()) {
        String rel = root.relativize(p).toString().replace('\\', '/');
        putFile(zos, prefix + "/" + rel, p);
      }
    }
  }

  static void putFile(ZipOutputStream zos, String entry, Path file) throws IOException {
    String name = entry.replace('\\', '/');
    if (name.startsWith("/") || name.contains("..") || name.startsWith(".m2/")) {
      throw new IOException("refusing zip entry " + name);
    }
    zos.putNextEntry(new ZipEntry(name));
    Files.copy(file, zos);
    zos.closeEntry();
  }
}
