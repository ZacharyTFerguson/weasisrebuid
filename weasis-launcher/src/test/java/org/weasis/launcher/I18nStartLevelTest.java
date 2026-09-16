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
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class I18nStartLevelTest {

  static final List<String> HOST_I18N_JARS =
      List.of(
          "weasis-core-i18n-4.7.3.jar",
          "weasis-dicom-explorer-i18n-4.7.3.jar",
          "weasis-dicom-viewer2d-i18n-4.7.3.jar",
          "weasis-acquire-editor-i18n-4.7.3.jar",
          "weasis-acquire-explorer-i18n-4.7.3.jar",
          "weasis-base-explorer-i18n-4.7.3.jar",
          "weasis-base-ui-i18n-4.7.3.jar",
          "weasis-base-viewer2d-i18n-4.7.3.jar",
          "weasis-dicom-au-i18n-4.7.3.jar",
          "weasis-dicom-codec-i18n-4.7.3.jar",
          "weasis-dicom-isowriter-i18n-4.7.3.jar",
          "weasis-dicom-qr-i18n-4.7.3.jar",
          "weasis-dicom-rt-i18n-4.7.3.jar",
          "weasis-dicom-send-i18n-4.7.3.jar",
          "weasis-dicom-sr-i18n-4.7.3.jar",
          "weasis-dicom-wave-i18n-4.7.3.jar",
          "weasis-dicom-viewer3d-i18n-4.7.3.jar",
          "weasis-launcher-i18n-4.7.3.jar");

  @Test
  void shippingCatalogInstallsHostI18nFragmentsAt13() throws Exception {
    Path launcherJson = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    Path distJson =
        Mx03ShippingPrefsTest.moduleRoot().resolve("../weasis-distributions/etc/config/base.json");
    Map<String, String> build = Map.of("app.version", "4.7.3");
    ConfigData launcher = ConfigData.load(launcherJson, build);
    ConfigData dist = ConfigData.load(distJson, build);
    String install13 = dist.value("felix.auto.install.13");
    assertTrue(install13.contains("/org/weasis/core/weasis-core-i18n/"));
    assertTrue(install13.contains("/org/weasis/dicom/3d/weasis-dicom-viewer3d-i18n/"));
    assertTrue(install13.contains("/org/weasis/weasis-launcher-i18n/"));
    for (String jar : HOST_I18N_JARS) {
      assertTrue(install13.contains(jar), jar);
    }
    assertEquals(install13, launcher.value("felix.auto.install.13"));
    boolean found = false;
    for (ConfigData.AutoBundle auto : dist.autoBundles()) {
      if (auto.startLevel() == 13) {
        found = true;
        assertFalse(auto.start());
        assertEquals(HOST_I18N_JARS.size(), auto.files().size());
      }
    }
    assertTrue(found);
  }
}
