/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.i18n;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class HostMessagesHookTest {

  @Test
  void eachHostBundleHasMessagesAndProperties() throws Exception {
    Path repo = repoRoot();
    List<String> hosts =
        List.of(
            "weasis-core",
            "weasis-launcher",
            "weasis-base/weasis-base-ui",
            "weasis-base/weasis-base-explorer",
            "weasis-base/weasis-base-viewer2d",
            "weasis-dicom/weasis-dicom-codec",
            "weasis-dicom/weasis-dicom-explorer",
            "weasis-dicom/weasis-dicom-viewer2d",
            "weasis-imageio",
            "weasis-acquire/weasis-acquire-explorer",
            "weasis-acquire/weasis-acquire-editor");
    for (String host : hosts) {
      Path javaTree = repo.resolve(host).resolve("src/main/java");
      Path propsTree = repo.resolve(host).resolve("src/main/resources");
      assertTrue(Files.isDirectory(javaTree), host);
      boolean foundJava;
      boolean foundProps;
      try (Stream<Path> walk = Files.walk(javaTree)) {
        foundJava = walk.anyMatch(p -> p.getFileName().toString().equals("Messages.java"));
      }
      try (Stream<Path> walk = Files.walk(propsTree)) {
        foundProps = walk.anyMatch(p -> p.getFileName().toString().equals("messages.properties"));
      }
      assertTrue(foundJava, "Messages.java missing in " + host);
      assertTrue(foundProps, "messages.properties missing in " + host);
    }
  }

  static Path repoRoot() {
    Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
    if (Files.isRegularFile(dir.resolve("weasis-core/pom.xml"))) {
      return dir;
    }
    if (Files.isRegularFile(dir.resolve("../pom.xml")) && Files.isDirectory(dir.resolve("src"))) {
      return dir.getParent();
    }
    throw new IllegalStateException("cannot find repo root from " + dir);
  }
}
