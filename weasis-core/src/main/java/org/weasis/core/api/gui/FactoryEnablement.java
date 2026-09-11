/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui;

import org.weasis.core.api.service.UICore;

/** Dicomizer disables explorers via four explicit factory FQCNs ({@code false}), not a wildcard. */
public final class FactoryEnablement {

  private FactoryEnablement() {}

  public static boolean isEnabled(Class<?> type) {
    if (type == null) {
      return true;
    }
    return isEnabled(type.getName());
  }

  public static boolean isEnabled(String className) {
    if (className == null || className.isBlank()) {
      return true;
    }
    String sys = System.getProperty(className);
    if (sys != null && !sys.isBlank()) {
      return parseTruthy(sys, true);
    }
    return UICore.getInstance().getSystemPreferences().getBooleanProperty(className, true);
  }

  static boolean parseTruthy(String raw, boolean defaultValue) {
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    if ("true".equalsIgnoreCase(raw) || "yes".equalsIgnoreCase(raw) || "1".equals(raw)) {
      return true;
    }
    if ("false".equalsIgnoreCase(raw) || "no".equalsIgnoreCase(raw) || "0".equals(raw)) {
      return false;
    }
    return defaultValue;
  }
}
