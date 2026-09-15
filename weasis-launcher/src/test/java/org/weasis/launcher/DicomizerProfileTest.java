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

/** WP-12 Dicomizer overlay Have. GUI Pass is not claimed. */
class DicomizerProfileTest {

  @Test
  void overlaySetsProfileBeginningAndStartLevels() throws Exception {
    Path base = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    Path overlay = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/dicomizer.json");
    ConfigData data = ConfigData.load(base, overlay, Map.of("app.version", "4.7.3"));

    assertEquals("dicomizer", data.value("weasis.profile"));
    assertEquals("Weasis Dicomizer", data.value("weasis.name"));
    assertEquals(110, data.beginningStartLevel());
    assertEquals("", data.value("felix.auto.start.70").trim());
    assertEquals("", data.value("felix.auto.start.75").trim());

    String start45 = data.value("felix.auto.start.45");
    assertTrue(start45.contains("weasis-base-explorer"));
    assertTrue(start45.contains("weasis-acquire-explorer"));
    assertTrue(data.value("felix.auto.start.85").contains("weasis-acquire-editor"));

    String start110 = data.value("felix.auto.start.110");
    assertTrue(start110.contains("weasis-dicom-send"));
    assertFalse(start110.contains("weasis-dicom-qr"));
    assertFalse(start110.contains("isowriter"));

    assertEquals("false", data.value("org.weasis.base.explorer.DefaultExplorerFactory"));
    assertEquals("false", data.value("org.weasis.dicom.explorer.MimeSystemAppFactory"));
    assertEquals("false", data.value("org.weasis.dicom.explorer.DicomExplorerFactory"));
    assertEquals(
        "false", data.value("org.weasis.dicom.explorer.pref.download.DicomExplorerPrefFactory"));

    assertEquals("1024", data.value("weasis.acquire.video.max.size"));
    assertEquals("DCM4CHEE", data.value("weasis.acquire.dest.aet"));
    assertEquals("11112", data.value("weasis.acquire.dest.port"));
    assertTrue(data.containsCode("weasis.acquire.meta.global.display"));
    assertTrue(data.containsCode("weasis.acquire.meta.global.edit"));
    assertTrue(data.containsCode("weasis.acquire.meta.global.required"));
    assertTrue(data.containsCode("weasis.acquire.wkl.host"));
  }

  @Test
  void distOverlayMatchesLauncher() throws Exception {
    Path launcher = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/dicomizer.json");
    Path dist =
        Mx03ShippingPrefsTest.moduleRoot()
            .resolve("../weasis-distributions/etc/config/dicomizer.json");
    assertTrue(Files.isRegularFile(dist), dist.toString());
    ConfigData a = ConfigData.load(launcher, Map.of("app.version", "4.7.3"));
    ConfigData b = ConfigData.load(dist, Map.of("app.version", "4.7.3"));
    assertEquals(a.value("weasis.profile"), b.value("weasis.profile"));
    assertEquals(a.value("felix.auto.start.110"), b.value("felix.auto.start.110"));
  }

  @Test
  void dicomizerJsonDoesNotContainGogoPort() throws Exception {
    Path overlay = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/dicomizer.json");
    String text = Files.readString(overlay);
    assertFalse(text.contains("gosh.port"));
    assertFalse(text.contains("17181"));
    assertFalse(text.contains("17179"));
  }

  @Test
  void resolveExtendedJsonNextToBase() throws Exception {
    Path base = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    System.setProperty(WeasisLauncher.EXTENDED_CONFIG_PROPERTY, "file:conf/dicomizer.json");
    try {
      Path resolved = WeasisLauncher.resolveExtendedJson(base);
      assertTrue(Files.isRegularFile(resolved));
      assertEquals("dicomizer.json", resolved.getFileName().toString());
    } finally {
      System.clearProperty(WeasisLauncher.EXTENDED_CONFIG_PROPERTY);
    }
  }
}
