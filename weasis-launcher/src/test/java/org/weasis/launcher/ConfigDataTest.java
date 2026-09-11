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
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigDataTest {

  @TempDir Path tempDir;

  @Test
  void parsesStartLevelsAndSubstitutesUserHome() throws Exception {
    Path json = tempDir.resolve("base.json");
    Files.writeString(
        json,
        """
        {
          "weasisPreferences": [
            {"code": "org.osgi.framework.startlevel.beginning", "value": "130", "type": "A", "category": "FELIX_CONFIG"},
            {"code": "felix.startlevel.bundle", "value": "300", "type": "A", "category": "FELIX_CONFIG"},
            {"code": "felix.cache.rootdir", "value": "${user.home}", "type": "A", "category": "FELIX_CONFIG"},
            {"code": "org.osgi.framework.storage", "value": "${felix.cache.rootdir}/.weasis/cache-dev", "type": "A", "category": "FELIX_CONFIG"},
            {"code": "weasis.version", "value": "${app.version}", "type": "A", "category": "LOCK"},
            {"code": "felix.auto.start.1", "value": "file:${maven.localRepository}/gogo.jar", "type": "A", "category": "FELIX_INSTALL"}
          ]
        }
        """);
    ConfigData data = ConfigData.load(json, Map.of("app.version", "4.7.3"));
    assertEquals(130, data.beginningStartLevel());
    assertEquals(300, data.bundleStartLevel());
    assertEquals("4.7.3", data.value("weasis.version"));
    assertTrue(data.value("org.osgi.framework.storage").endsWith("/.weasis/cache-dev"));
    assertFalse(data.value("org.osgi.framework.storage").contains("${"));
    assertEquals(1, data.autoBundles().size());
    assertTrue(data.autoBundles().get(0).start());
    assertEquals(1, data.autoBundles().get(0).startLevel());
    assertEquals("1", data.frameworkProperties().get("org.osgi.framework.startlevel.beginning"));
    assertEquals("300", data.frameworkProperties().get("felix.startlevel.bundle"));
  }

  @Test
  void typeApIsNotOverriddenBySystemProperty() throws Exception {
    Path json = tempDir.resolve("ap.json");
    Files.writeString(
        json,
        """
        {
          "weasisPreferences": [
            {"code": "weasis.name", "value": "Weasis", "type": "AP", "category": "LAUNCH"}
          ]
        }
        """);
    System.setProperty("weasis.name", "should-not-win");
    try {
      ConfigData data = ConfigData.load(json, Map.of());
      assertEquals("Weasis", data.value("weasis.name"));
    } finally {
      System.clearProperty("weasis.name");
    }
  }
}
