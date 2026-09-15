/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.pref;

import java.util.Properties;
import org.weasis.core.api.service.WProperties;

public class AppPreferences extends WProperties {
  public AppPreferences() {
    super();
    put("weasis.name", "Weasis");
    put("weasis.version", "4.7.3");
    put("weasis.profile", "default");
    put("weasis.theme", "org.weasis.launcher.FlatWeasisTheme");
    put("weasis.dicom.root.uid", "2.25");
    put("weasis.download.immediately", "true");
    put("weasis.dicom.explorer.filter.mode", "TEXT");
    put("download.concurrent.series", "3");
    put("download.concurrent.series.images", "4");
    put("weasis.main.ui", "weasis-base-ui");
    put("weasis.level.inverse", "true");
    put("weasis.color.wl.apply", "true");
    put("weasis.apply.latest.pr", "false");
    put("weasis.force.3d", "false");
    put("weasis.toolbar.mouse.left", "winLevel");
    put("locale.lang.code", "en");
    put("felix.log.level", "1");
  }

  public AppPreferences(Properties defaults) {
    super(defaults);
  }

  public String weasisName() {
    return getProperty("weasis.name", "Weasis");
  }

  public String theme() {
    return getProperty("weasis.theme", "org.weasis.launcher.FlatWeasisTheme");
  }
}
