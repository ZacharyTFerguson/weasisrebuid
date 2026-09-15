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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** ARCHITECTURE start 7: MigLayout core + swing + JAXB-OSGi. */
class MigLayoutStartLevelTest {

  @Test
  void startLevel7ListsMigLayoutAndJaxbJars() throws Exception {
    Path base = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    ConfigData data =
        ConfigData.load(
            base,
            Map.of(
                "app.version",
                "4.7.3",
                "miglayout.version",
                "11.4.3",
                "jaxb.osgi.version",
                "4.0.3"));
    String start7 = data.value("felix.auto.start.7");
    assertTrue(start7.contains("miglayout-core-11.4.3.jar"));
    assertTrue(start7.contains("miglayout-swing-11.4.3.jar"));
    assertTrue(start7.contains("jaxb-osgi-4.0.3.jar"));
    boolean found = false;
    for (ConfigData.AutoBundle auto : data.autoBundles()) {
      if (auto.startLevel() != 7) {
        continue;
      }
      found = true;
      assertTrue(auto.start());
      assertTrue(auto.files().size() >= 3);
      for (Path jar : auto.files()) {
        assertTrue(Files.isRegularFile(jar), jar.toString());
      }
    }
    assertTrue(found, "felix.auto.start.7");
  }
}
