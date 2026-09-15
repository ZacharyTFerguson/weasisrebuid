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

import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

class I18nStartLevelTest {

  @Test
  void shippingCatalogInstallsCoreI18nFragmentAt13() throws Exception {
    Path launcherJson = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    Path distJson =
        Mx03ShippingPrefsTest.moduleRoot().resolve("../weasis-distributions/etc/config/base.json");
    Map<String, String> build = Map.of("app.version", "4.7.3");
    ConfigData launcher = ConfigData.load(launcherJson, build);
    ConfigData dist = ConfigData.load(distJson, build);
    String install13 = dist.value("felix.auto.install.13");
    assertTrue(install13.contains("weasis-core-i18n-4.7.3.jar"));
    assertTrue(install13.contains("/org/weasis/core/weasis-core-i18n/"));
    assertEquals(install13, launcher.value("felix.auto.install.13"));
    boolean found = false;
    for (ConfigData.AutoBundle auto : dist.autoBundles()) {
      if (auto.startLevel() == 13) {
        found = true;
        assertFalse(auto.start());
        assertEquals(1, auto.files().size());
      }
    }
    assertTrue(found);
  }
}
