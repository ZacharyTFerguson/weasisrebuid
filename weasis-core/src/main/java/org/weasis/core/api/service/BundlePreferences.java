/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.service;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.osgi.framework.BundleContext;

/** OSGi preferences node helpers. */
public final class BundlePreferences {

  private BundlePreferences() {}

  public static Preferences getDefaultPreferences(BundleContext context) {
    if (context == null) {
      return Preferences.userRoot().node("weasis");
    }
    return Preferences.userRoot().node("weasis/" + context.getBundle().getSymbolicName());
  }

  public static void putStringPreferences(Preferences prefs, String key, String value) {
    if (prefs == null || key == null) {
      return;
    }
    if (value == null) {
      prefs.remove(key);
    } else {
      prefs.put(key, value);
    }
    try {
      prefs.flush();
    } catch (BackingStoreException e) {
      throw new IllegalStateException(e);
    }
  }
}
