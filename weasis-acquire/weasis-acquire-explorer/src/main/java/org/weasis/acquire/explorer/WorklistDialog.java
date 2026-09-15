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

/** Modality worklist connection from {@code weasis.acquire.wkl.*} prefs (WP-12). */
public class WorklistDialog {

  public record WorklistEndpoint(String host, String aet, int port, String stationAet) {

    public boolean configured() {
      return host != null && !host.isBlank() && aet != null && !aet.isBlank();
    }
  }

  public static WorklistEndpoint fromPreferences(Properties prefs) {
    if (prefs == null) {
      return new WorklistEndpoint("", "", 107, "");
    }
    String host = prefs.getProperty("weasis.acquire.wkl.host", "");
    String aet = prefs.getProperty("weasis.acquire.wkl.aet", "");
    String station = prefs.getProperty("weasis.acquire.wkl.station.aet", "");
    int port = 107;
    String portRaw = prefs.getProperty("weasis.acquire.wkl.port", "107");
    if (portRaw != null && !portRaw.isBlank()) {
      port = Integer.parseInt(portRaw.trim());
    }
    return new WorklistEndpoint(host, aet, port, station);
  }
}
