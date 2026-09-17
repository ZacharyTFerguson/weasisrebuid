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

/** {@code weasis.acquire.video.max.size} default 1024 MB; 0 disables. */
public final class VideoSizeLimit {

  public static final int DEFAULT_MB = 1024;

  private VideoSizeLimit() {}

  public static int maxMegabytes(Properties prefs) {
    if (prefs == null) {
      return DEFAULT_MB;
    }
    String raw = prefs.getProperty("weasis.acquire.video.max.size");
    if (raw == null || raw.isBlank()) {
      return DEFAULT_MB;
    }
    return Integer.parseInt(raw.trim());
  }

  public static boolean allowed(long sizeBytes, int maxMb) {
    if (maxMb == 0) {
      return true;
    }
    long cap = (long) maxMb * 1024L * 1024L;
    return sizeBytes <= cap;
  }
}
