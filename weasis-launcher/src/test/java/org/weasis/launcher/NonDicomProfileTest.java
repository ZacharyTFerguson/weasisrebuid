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

class NonDicomProfileTest {

  @Test
  void overlayClearsCodecKeepsSendQrIso() throws Exception {
    Path base = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    Path overlay = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/non-dicom-explorer.json");
    ConfigData data = ConfigData.load(base, overlay, Map.of("app.version", "4.7.3"));
    assertEquals("base-explorer", data.value("weasis.profile"));
    assertEquals("Weasis Image Explorer", data.value("weasis.name"));
    assertEquals(100, data.beginningStartLevel());
    assertEquals("", data.value("felix.auto.start.30").trim());
    assertTrue(data.value("felix.auto.start.40").contains("weasis-base-explorer"));
    assertFalse(data.value("felix.auto.start.40").contains("weasis-dicom-explorer"));
    assertEquals("", data.value("felix.auto.start.70").trim());
    assertEquals("", data.value("felix.auto.start.75").trim());
    String start110 = data.value("felix.auto.start.110");
    assertTrue(start110.contains("weasis-dicom-send"));
    assertTrue(start110.contains("weasis-dicom-qr"));
    assertTrue(start110.contains("weasis-dicom-isowriter"));
    Path dist =
        Mx03ShippingPrefsTest.moduleRoot()
            .resolve("../weasis-distributions/etc/config/non-dicom-explorer.json");
    assertTrue(Files.isRegularFile(dist), dist.toString());
  }

  @Test
  void launchSessionAppliesConfigBeforeOverlayLookup() {
    String key = WeasisLauncher.EXTENDED_CONFIG_PROPERTY;
    System.clearProperty(key);
    try {
      LaunchSession session =
          LaunchSession.fromArgs(
              new String[] {
                "$weasis:config pro=\"felix.extended.config.properties file:conf/dicomizer.json\""
              });
      assertEquals(1, session.configs().size());
      session.applyConfigs();
      assertEquals("file:conf/dicomizer.json", System.getProperty(key));
    } finally {
      System.clearProperty(key);
    }
  }
}
