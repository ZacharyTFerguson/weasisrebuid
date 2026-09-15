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

/** ARCHITECTURE start 120 (jogamp + viewer3d) and install 121 (JOGL native). */
class Mx15StartLevelTest {

  @Test
  void shippingCatalogStarts3dAt120AndInstallsNativeAt121() throws Exception {
    Path launcherJson = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    Path distJson =
        Mx03ShippingPrefsTest.moduleRoot().resolve("../weasis-distributions/etc/config/base.json");
    Map<String, String> build =
        Map.of(
            "app.version",
            "4.7.3",
            "jogamp.version",
            "2.6.0",
            "native.library.spec",
            "linux-x86-64");
    ConfigData launcher = ConfigData.load(launcherJson, build);
    ConfigData dist = ConfigData.load(distJson, build);

    String start120 = dist.value("felix.auto.start.120");
    assertTrue(start120.contains("weasis-dicom-viewer3d-4.7.3.jar"));
    assertTrue(start120.contains("/org/weasis/dicom/3d/weasis-dicom-viewer3d/"));
    assertTrue(
        start120.contains("/org/weasis/thirdparty/org/jogamp/jogamp/2.6.0/jogamp-2.6.0.jar"));
    String install121 = dist.value("felix.auto.install.121");
    assertTrue(install121.contains("jogamp-linux-x86-64-2.6.0.jar"));
    assertFalse(install121.contains("weasis-dicom-viewer3d"));

    assertEquals(start120, launcher.value("felix.auto.start.120"));
    assertEquals(install121, launcher.value("felix.auto.install.121"));

    boolean found120 = false;
    boolean found121 = false;
    for (ConfigData.AutoBundle auto : dist.autoBundles()) {
      if (auto.startLevel() == 120) {
        found120 = true;
        assertTrue(auto.start());
        assertEquals(2, auto.files().size());
      }
      if (auto.startLevel() == 121) {
        found121 = true;
        assertFalse(auto.start());
        assertEquals(1, auto.files().size());
      }
    }
    assertTrue(found120);
    assertTrue(found121);
    assertTrue(Files.isRegularFile(launcherJson));
  }
}
