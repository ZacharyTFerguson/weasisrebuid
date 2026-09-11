/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

import org.weasis.core.api.service.UICore;

/** Registers catalog pages when DS has not yet published factories (tests / early boot). */
public final class DefaultPrefBootstrap {

  private DefaultPrefBootstrap() {}

  public static void ensureRegistered(UICore core) {
    UICore target = core == null ? UICore.getInstance() : core;
    if (!target.getPreferencesPageFactories().isEmpty()) {
      return;
    }
    target.registerPreferencesPageFactory(new GeneralPrefFactory());
    target.registerPreferencesPageFactory(new AppearancePrefFactory());
    target.registerPreferencesPageFactory(new DicomPrefFactory());
    target.registerPreferencesPageFactory(new ViewerPrefFactory());
    target.registerPreferencesPageFactory(new DrawPrefFactory());
    target.registerPreferencesPageFactory(new ShortcutPrefFactory());
    target.registerPreferencesPageFactory(new ScreenPrefFactory());
    target.registerPreferencesPageFactory(new ProxyPrefFactory());
    target.registerPreferencesPageFactory(new LoggingPrefFactory());
    target.registerPreferencesPageFactory(new LauncherPrefFactory());
  }
}
