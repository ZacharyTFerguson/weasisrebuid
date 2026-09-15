/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.util.Properties;

/** Plans C-STORE publish using {@code weasis.acquire.dest.*} prefs (WP-12). */
public class PublishDicomTask {

  private final Properties preferences;
  private final boolean selectionOnly;
  private final int resolutionDownscale;

  public PublishDicomTask(Properties preferences, boolean selectionOnly, int resolutionDownscale) {
    this.preferences = preferences == null ? new Properties() : preferences;
    this.selectionOnly = selectionOnly;
    this.resolutionDownscale = Math.max(0, resolutionDownscale);
  }

  public AcquireDest.Publication plan(String callingAe) {
    String host = preferences.getProperty("weasis.acquire.dest.host", "");
    String dest =
        host.isBlank()
            ? AcquireDest.aet(preferences) + ":" + AcquireDest.port(preferences)
            : host + ":" + AcquireDest.port(preferences) + "/" + AcquireDest.aet(preferences);
    AcquireDest.PublishMode mode =
        AcquireDest.destinationLocked(preferences)
            ? AcquireDest.PublishMode.CSTORE
            : AcquireDest.PublishMode.LOCAL_EXPORT;
    return new AcquireDest.Publication(
        selectionOnly, resolutionDownscale, dest, callingAe, false, mode);
  }
}
