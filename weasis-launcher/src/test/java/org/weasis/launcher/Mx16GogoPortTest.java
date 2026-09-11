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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** MX-16: Gogo desktop 17179 is a VM property, not a base.json pref. Dicomizer 17181 is WP-12. */
class Mx16GogoPortTest {

  @Test
  void goshPortIsNotAJsonPref() throws Exception {
    Path launcherJson = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    Path distJson =
        Mx03ShippingPrefsTest.moduleRoot().resolve("../weasis-distributions/etc/config/base.json");
    ConfigData launcher = ConfigData.load(launcherJson, Map.of("app.version", "4.7.3"));
    ConfigData dist = ConfigData.load(distJson, Map.of("app.version", "4.7.3"));
    assertFalse(launcher.containsCode("gosh.port"));
    assertFalse(dist.containsCode("gosh.port"));
    String launcherText = Files.readString(launcherJson);
    String distText = Files.readString(distJson);
    assertFalse(launcherText.contains("gosh.port"));
    assertFalse(distText.contains("gosh.port"));
    assertFalse(launcherText.contains("17179"));
    assertFalse(distText.contains("17179"));
  }

  @Test
  void defaultPortIs17179WhenUnset() {
    assertEquals("17179", WeasisLauncher.resolveGogoPort(null));
    assertEquals("17179", WeasisLauncher.resolveGogoPort(""));
    assertEquals("17181", WeasisLauncher.resolveGogoPort("17181"));
  }
}
