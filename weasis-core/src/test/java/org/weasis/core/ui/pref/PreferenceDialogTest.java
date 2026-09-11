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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

class PreferenceDialogTest {

  @Test
  void instantiatePagesSortsByPositionAndIncludesTutorialCatalog() {
    List<AbstractItemDialogPage> pages =
        PreferenceDialog.instantiatePages(
            List.of(
                new LauncherPrefFactory(),
                new GeneralPrefFactory(),
                new ProxyPrefFactory(),
                new ViewerPrefFactory(),
                new DicomPrefFactory(),
                new AppearancePrefFactory(),
                new DrawPrefFactory(),
                new ShortcutPrefFactory(),
                new ScreenPrefFactory(),
                new LoggingPrefFactory()));
    assertEquals("General", pages.getFirst().getTitle());
    assertTrue(pages.stream().anyMatch(p -> "Proxy Server".equals(p.getTitle())));
    assertTrue(pages.stream().anyMatch(p -> "Viewer".equals(p.getTitle())));
    AbstractItemDialogPage viewer =
        pages.stream().filter(p -> "Viewer".equals(p.getTitle())).findFirst().orElseThrow();
    assertTrue(viewer.getSubPages().stream().anyMatch(p -> "2D".equals(p.getTitle())));
    assertTrue(viewer.getSubPages().stream().anyMatch(p -> "MPR".equals(p.getTitle())));
  }
}
