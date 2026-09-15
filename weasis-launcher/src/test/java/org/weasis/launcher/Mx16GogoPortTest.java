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

/** MX-16: Gogo desktop 17179 is a VM property, not a JSON pref. Dicomizer 17181. */
class Mx16GogoPortTest {

  @Test
  void goshPortIsNotAJsonPref() throws Exception {
    Path root = Mx03ShippingPrefsTest.moduleRoot();
    Path[] jsons =
        new Path[] {
          root.resolve("conf/base.json"),
          root.resolve("conf/dicomizer.json"),
          root.resolve("../weasis-distributions/etc/config/base.json"),
          root.resolve("../weasis-distributions/etc/config/dicomizer.json")
        };
    for (Path json : jsons) {
      assertTrue(Files.isRegularFile(json), json.toString());
      ConfigData data = ConfigData.load(json, Map.of("app.version", "4.7.3"));
      assertFalse(data.containsCode("gosh.port"), json.toString());
      String text = Files.readString(json);
      assertFalse(text.contains("gosh.port"), json.toString());
      assertFalse(text.contains("17179"), json.toString());
      assertFalse(text.contains("17181"), json.toString());
    }
  }

  @Test
  void defaultPortIs17179WhenUnset() {
    assertEquals("17179", WeasisLauncher.resolveGogoPort(null, null));
    assertEquals("17179", WeasisLauncher.resolveGogoPort("", "default"));
    assertEquals("17181", WeasisLauncher.resolveGogoPort("17181", "default"));
  }

  @Test
  void dicomizerProfileDefaultsTo17181() {
    assertEquals("17181", WeasisLauncher.resolveGogoPort(null, "dicomizer"));
    assertEquals("17181", WeasisLauncher.resolveGogoPort("", "dicomizer"));
    assertEquals("17179", WeasisLauncher.resolveGogoPort(null, "default"));
  }
}
