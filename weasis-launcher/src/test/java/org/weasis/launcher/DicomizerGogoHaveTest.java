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

/**
 * WP-12: Dicomizer Gogo is 17181. Desktop Gogo stays 17179. {@code gosh.port} is never a JSON pref.
 */
class DicomizerGogoHaveTest {

  @Test
  void documentedConfigProSetsGogo17181NotJson() throws Exception {
    Utils.LaunchRequest req =
        Utils.parseLaunch(
            new String[] {
              "$weasis:config pro=\"felix.extended.config.properties file:conf/dicomizer.json\" pro=\"gosh.port 17181\""
            });
    assertEquals("17181", req.properties().get("gosh.port"));
    assertFalse("17179".equals(req.properties().get("gosh.port")));
    Path overlay = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/dicomizer.json");
    String text = Files.readString(overlay);
    assertFalse(text.contains("gosh.port"));
    assertFalse(text.contains("17181"));
    assertFalse(text.contains("17179"));
  }

  @Test
  void overlayProfileResolves17181AndDesktopStays17179() throws Exception {
    Path base = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    Path overlay = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/dicomizer.json");
    ConfigData data = ConfigData.load(base, overlay, Map.of("app.version", "4.7.3"));
    assertEquals("dicomizer", data.value("weasis.profile"));
    assertEquals("17181", WeasisLauncher.resolveGogoPort(null, data.value("weasis.profile")));
    assertEquals("17179", WeasisLauncher.resolveGogoPort(null, "default"));
    assertEquals("17179", WeasisLauncher.resolveGogoPort(null, null));
    assertEquals("17181", WeasisLauncher.resolveGogoPort("17181", "default"));
  }

  @Test
  void infoAllPrintsDicomizerPort17181NotDesktop17179() {
    String previousPort = System.getProperty(WeasisLauncher.GOGO_PORT_PROPERTY);
    String previousProfile = System.getProperty("weasis.profile");
    try {
      System.setProperty(WeasisLauncher.GOGO_PORT_PROPERTY, WeasisLauncher.DICOMIZER_GOGO_PORT);
      System.setProperty("weasis.profile", WeasisLauncher.DICOMIZER_PROFILE);
      String all = new LauncherGogo(null).info("-a");
      assertTrue(all.contains("weasis.profile"));
      assertTrue(all.contains("dicomizer"));
      assertTrue(all.contains("17181"));
      assertFalse(all.contains("17179"));
    } finally {
      restore(WeasisLauncher.GOGO_PORT_PROPERTY, previousPort);
      restore("weasis.profile", previousProfile);
    }
  }

  private static void restore(String key, String previous) {
    if (previous == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, previous);
    }
  }
}
