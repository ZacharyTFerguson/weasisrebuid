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

import org.weasis.core.api.service.WProperties;

/** Applies persisted visibility and position to an {@link Insertable}. */
public final class InsertableUtil {

  private InsertableUtil() {}

  public static boolean isFactoryEnabled(Class<?> type) {
    if (type == null) {
      return true;
    }
    return isFactoryEnabled(type.getName());
  }

  public static boolean isFactoryEnabled(String className) {
    if (className == null || className.isBlank()) {
      return true;
    }
    String v = System.getProperty(className);
    if (v == null || v.isBlank()) {
      return true;
    }
    return !"false".equalsIgnoreCase(v.trim());
  }

  public static String visibilityKey(String className) {
    return className + ".visible";
  }

  public static String positionKey(String className) {
    return className + ".position";
  }

  public static void applyPreferences(
      Insertable insertable, WProperties prefs, String className, boolean defaultVisible) {
    if (insertable == null || prefs == null || className == null) {
      return;
    }
    insertable.setComponentEnabled(
        prefs.getBooleanProperty(visibilityKey(className), defaultVisible));
    insertable.setComponentPosition(
        prefs.getIntProperty(positionKey(className), insertable.getComponentPosition()));
  }

  public static void savePreferences(Insertable insertable, WProperties prefs, String className) {
    if (insertable == null || prefs == null || className == null) {
      return;
    }
    prefs.putBooleanProperty(visibilityKey(className), insertable.isComponentEnabled());
    prefs.putIntProperty(positionKey(className), insertable.getComponentPosition());
  }
}
