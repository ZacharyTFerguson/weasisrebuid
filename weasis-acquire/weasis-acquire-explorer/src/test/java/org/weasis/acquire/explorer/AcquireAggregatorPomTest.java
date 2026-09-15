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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AcquireAggregatorPomTest {

  @Test
  void noAcquireParentAndChildrenParentWeasisParent() throws Exception {
    Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
    Path agg =
        dir.getFileName().toString().equals("weasis-acquire-explorer") ? dir.getParent() : dir;
    if (!Files.isRegularFile(agg.resolve("pom.xml"))) {
      agg = dir.resolve("weasis-acquire");
    }
    String aggregator = Files.readString(agg.resolve("pom.xml"), StandardCharsets.UTF_8);
    assertFalse(aggregator.contains("<artifactId>weasis-acquire-parent</artifactId>"));
    assertTrue(aggregator.contains("<artifactId>weasis-acquire</artifactId>"));
    assertTrue(aggregator.contains("<packaging>pom</packaging>"));
    String explorer = Files.readString(agg.resolve("weasis-acquire-explorer/pom.xml"));
    String editor = Files.readString(agg.resolve("weasis-acquire-editor/pom.xml"));
    assertTrue(explorer.contains("<artifactId>weasis-parent</artifactId>"));
    assertTrue(editor.contains("<artifactId>weasis-parent</artifactId>"));
    assertFalse(Files.exists(agg.resolve("weasis-acquire-parent")));
  }
}
