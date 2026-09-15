/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Every main Java path in this clone must be a Weasis 4.7.3 path or a documented extra. Missing
 * upstream paths are recorded in the gap file; they do not fail this test.
 */
class SourceSurfaceOracleTest {

  @Test
  void noUnexpectedMainJavaPaths() throws IOException {
    Path root = findRepoRoot();
    Set<String> upstream =
        readList(root.resolve("docs/weasis-spec/fixtures/weasis-4.7.3-main-java.txt"));
    Set<String> extras =
        readList(root.resolve("docs/weasis-spec/fixtures/clone-only-main-java.txt"));
    Set<String> local = new HashSet<>();
    try (Stream<Path> walk = Files.walk(root)) {
      walk.filter(p -> p.toString().replace('\\', '/').contains("/src/main/java/"))
          .filter(p -> p.toString().endsWith(".java"))
          .filter(p -> !p.toString().contains("/target/"))
          .forEach(p -> local.add(root.relativize(p).toString().replace('\\', '/')));
    }
    Set<String> unexpected =
        local.stream()
            .filter(p -> !upstream.contains(p) && !extras.contains(p))
            .sorted()
            .collect(Collectors.toCollection(HashSet::new));
    if (!unexpected.isEmpty()) {
      fail(
          "Unexpected main Java paths (add to Weasis layout or clone-only list):\n"
              + String.join("\n", unexpected.stream().sorted().toList()));
    }
    assertTrue(local.stream().anyMatch(p -> p.endsWith("AngleToolGraphic.java")));
  }

  private static Path findRepoRoot() {
    Path dir = Path.of("").toAbsolutePath();
    for (int i = 0; i < 8 && dir != null; i++) {
      if (Files.isRegularFile(
          dir.resolve("docs/weasis-spec/fixtures/weasis-4.7.3-main-java.txt"))) {
        return dir;
      }
      dir = dir.getParent();
    }
    fail("Could not find repo root with weasis-spec fixtures");
    return Path.of(".");
  }

  private static Set<String> readList(Path file) throws IOException {
    if (!Files.isRegularFile(file)) {
      return Set.of();
    }
    try (Stream<String> lines = Files.lines(file)) {
      return lines
          .map(String::trim)
          .filter(s -> !s.isEmpty() && !s.startsWith("#"))
          .collect(Collectors.toSet());
    }
  }
}
