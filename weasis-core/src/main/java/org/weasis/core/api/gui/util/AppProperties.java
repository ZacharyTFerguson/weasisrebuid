/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.gui.util;

import java.io.File;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;

public final class AppProperties {
  public static final String WEASIS_VERSION = System.getProperty("weasis.version", "4.7.3");
  public static final String WEASIS_NAME = System.getProperty("weasis.name", "Weasis");
  public static final String WEASIS_USER = System.getProperty("user.name", "user");
  public static final File WEASIS_PATH =
      new File(System.getProperty("user.home"), ".weasis");

  private AppProperties() {}

  public static WProperties getSystemPreferences() {
    return UICore.getInstance().getSystemPreferences();
  }

  public static String getWeasisProfile() {
    return System.getProperty("weasis.profile", "default");
  }

  public static String getTheme() {
    return System.getProperty("weasis.theme", "org.weasis.launcher.FlatWeasisTheme");
  }

  public static File getBundleDataFolder(String symbolicName) {
    File data = new File(WEASIS_PATH, "data");
    return symbolicName == null ? data : new File(data, symbolicName);
  }
}
