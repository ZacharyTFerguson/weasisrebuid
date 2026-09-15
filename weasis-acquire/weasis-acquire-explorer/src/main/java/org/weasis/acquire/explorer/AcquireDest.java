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

/** Destination lock when {@code weasis.acquire.dest.*} is set. Default AE DCM4CHEE:11112. */
public final class AcquireDest {

  public static final String DEFAULT_AET = "DCM4CHEE";
  public static final int DEFAULT_PORT = 11112;

  public enum PublishMode {
    CSTORE,
    LOCAL_EXPORT
  }

  private AcquireDest() {}

  public static boolean destinationLocked(Properties prefs) {
    if (prefs == null) {
      return false;
    }
    return notBlank(prefs.getProperty("weasis.acquire.dest.host"))
        || notBlank(prefs.getProperty("weasis.acquire.dest.aet"))
        || notBlank(prefs.getProperty("weasis.acquire.dest.port"));
  }

  public static String aet(Properties prefs) {
    String v = prefs == null ? null : prefs.getProperty("weasis.acquire.dest.aet");
    return v == null || v.isBlank() ? DEFAULT_AET : v;
  }

  public static int port(Properties prefs) {
    String v = prefs == null ? null : prefs.getProperty("weasis.acquire.dest.port");
    if (v == null || v.isBlank()) {
      return DEFAULT_PORT;
    }
    return Integer.parseInt(v.trim());
  }

  public record Publication(
      boolean selection,
      int resolutionDownscale,
      String destination,
      String callingAe,
      boolean published,
      PublishMode mode) {}

  private static boolean notBlank(String s) {
    return s != null && !s.isBlank();
  }
}
