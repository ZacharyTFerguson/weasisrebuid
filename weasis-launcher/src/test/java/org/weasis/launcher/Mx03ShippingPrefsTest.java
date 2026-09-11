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

/** MX-03: shipping prefs = dist JSON (INFO / stack 3 / felix.log.level 1). Launcher is IDE. */
class Mx03ShippingPrefsTest {

  @Test
  void distAndLauncherLoggingAreExclusiveDefaults() throws Exception {
    Path launcherJson = moduleRoot().resolve("conf/base.json");
    Path distJson = moduleRoot().resolve("../weasis-distributions/etc/config/base.json");
    assertTrue(Files.isRegularFile(launcherJson), launcherJson.toString());
    assertTrue(Files.isRegularFile(distJson), distJson.toString());

    ConfigData launcher = ConfigData.load(launcherJson, Map.of("app.version", "4.7.3"));
    ConfigData dist = ConfigData.load(distJson, Map.of("app.version", "4.7.3"));

    assertEquals("DEBUG", launcher.value("org.apache.sling.commons.log.level"));
    assertEquals("-1", launcher.value("org.apache.sling.commons.log.stack.limit"));
    assertEquals("2", launcher.value("felix.log.level"));

    assertEquals("INFO", dist.value("org.apache.sling.commons.log.level"));
    assertEquals("3", dist.value("org.apache.sling.commons.log.stack.limit"));
    assertEquals("1", dist.value("felix.log.level"));

    assertEquals(130, dist.beginningStartLevel());
    assertEquals(300, dist.bundleStartLevel());
    assertEquals(130, launcher.beginningStartLevel());
    assertEquals(300, launcher.bundleStartLevel());
    assertTrue(dist.value("org.osgi.framework.storage").endsWith("/.weasis/cache"));
    assertTrue(launcher.value("org.osgi.framework.storage").endsWith("/.weasis/cache-dev"));
    assertFalse(
        dist.value("org.apache.sling.commons.log.level")
            .equals(launcher.value("org.apache.sling.commons.log.level")));
  }

  static Path moduleRoot() {
    Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
    if (Files.isRegularFile(dir.resolve("conf/base.json"))) {
      return dir;
    }
    Path nested = dir.resolve("weasis-launcher");
    if (Files.isRegularFile(nested.resolve("conf/base.json"))) {
      return nested;
    }
    throw new IllegalStateException("cannot find weasis-launcher/conf/base.json from " + dir);
  }
}
