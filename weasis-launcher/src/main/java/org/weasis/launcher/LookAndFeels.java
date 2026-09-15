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

import java.awt.GraphicsEnvironment;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Installs {@code weasis.theme} before Swing viewers start. Headless boots skip UIManager. */
public final class LookAndFeels {

  private static final Logger LOGGER = LoggerFactory.getLogger(LookAndFeels.class);

  public static final String THEME_PROPERTY = "weasis.theme";

  private LookAndFeels() {}

  public static String themeClassName() {
    String theme = System.getProperty(THEME_PROPERTY);
    if (theme == null || theme.isBlank()) {
      return FlatWeasisTheme.class.getName();
    }
    return theme.trim();
  }

  public static void install() {
    String theme = themeClassName();
    if (GraphicsEnvironment.isHeadless()) {
      LOGGER.info("Headless; leave UIManager default, theme {}", theme);
      return;
    }
    try {
      if (FlatWeasisTheme.class.getName().equals(theme)) {
        FlatWeasisTheme.setup();
      } else {
        UIManager.setLookAndFeel(theme);
      }
    } catch (Exception e) {
      LOGGER.warn("Cannot install look and feel {}", theme, e);
    }
  }
}
