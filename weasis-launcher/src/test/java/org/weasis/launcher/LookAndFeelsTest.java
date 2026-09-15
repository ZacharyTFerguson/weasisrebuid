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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LookAndFeelsTest {

  @Test
  void defaultThemeIsFlatWeasisTheme() {
    String previous = System.getProperty(LookAndFeels.THEME_PROPERTY);
    System.clearProperty(LookAndFeels.THEME_PROPERTY);
    try {
      assertEquals(FlatWeasisTheme.class.getName(), LookAndFeels.themeClassName());
      assertEquals("Weasis", new FlatWeasisTheme().getName());
    } finally {
      if (previous != null) {
        System.setProperty(LookAndFeels.THEME_PROPERTY, previous);
      }
    }
  }

  @Test
  void jsonShipsThemePref() throws Exception {
    Path base = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    ConfigData data = ConfigData.load(base, Map.of("app.version", "4.7.3"));
    assertEquals(FlatWeasisTheme.class.getName(), data.value("weasis.theme"));
    String extra = data.value("org.osgi.framework.system.packages.extra");
    assertTrue(extra.contains("com.formdev.flatlaf.extras"));
    assertTrue(extra.contains("com.formdev.flatlaf.ui"));
    assertTrue(extra.contains("com.formdev.flatlaf.util"));
  }
}
