/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.util;

/** Native / GPU resource bookkeeping (filled in later WPs). */
public final class ResourceMonitor {

  public static final String NATIVE_MEMORY_PERCENT = "weasis.native.memory.percent";

  private ResourceMonitor() {}

  public static int nativeMemoryPercent(org.weasis.core.api.service.WProperties prefs) {
    if (prefs == null) {
      return 50;
    }
    return prefs.getIntProperty(NATIVE_MEMORY_PERCENT, 50);
  }
}
