/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.pref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.pref.PreferenceDialog;
import org.weasis.dicom.viewer3d.OpenGLInfo;

class Viewer3dPrefHaveTest {

  @Test
  void factoryCreatesOpenGlTitled3dPage() {
    Viewer3dPrefFactory factory = new Viewer3dPrefFactory();
    AbstractItemDialogPage page = factory.createInstance(null);
    assertInstanceOf(Viewer3dPrefView.class, page);
    assertEquals(Viewer3dPrefView.class, page.getClass());
    assertEquals(Insertable.Type.PREFERENCES, factory.getType());
    assertTrue(factory.isComponentCreatedByThisFactory(page));
    assertEquals(Viewer3dPrefView.TITLE, page.getTitle());
    assertTrue(page.getTitle().contains(OpenGLInfo.MIN_VERSION));
  }

  @Test
  void persistsDocumentedForce3dDefaultFalse() {
    String previous = System.getProperty(Viewer3dPrefView.PREF_FORCE_3D);
    try {
      System.clearProperty(Viewer3dPrefView.PREF_FORCE_3D);
      WProperties prefs = new WProperties();
      Viewer3dPrefView page = new Viewer3dPrefView(prefs);
      assertFalse(page.force3d());
      assertFalse(Viewer3dPrefView.force3dProperty());
      page.setForce3d(true);
      page.closeAdditionalWindow();
      assertTrue(prefs.getBooleanProperty(Viewer3dPrefView.PREF_FORCE_3D, false));
      assertEquals("true", System.getProperty(Viewer3dPrefView.PREF_FORCE_3D));
      assertTrue(Viewer3dPrefView.force3dProperty());

      Viewer3dPrefView loaded = new Viewer3dPrefView(prefs);
      assertTrue(loaded.force3d());
      loaded.resetToDefaultValues();
      assertFalse(loaded.force3d());
    } finally {
      restore(Viewer3dPrefView.PREF_FORCE_3D, previous);
    }
  }

  @Test
  void preferenceOkStoresForce3dAndReloadShowsSameValue() {
    String previous = System.getProperty(Viewer3dPrefView.PREF_FORCE_3D);
    WProperties prefs = UICore.getInstance().getSystemPreferences();
    String previousPref = prefs.getProperty(Viewer3dPrefView.PREF_FORCE_3D);
    try {
      prefs.remove(Viewer3dPrefView.PREF_FORCE_3D);
      System.clearProperty(Viewer3dPrefView.PREF_FORCE_3D);
      Viewer3dPrefView page = new Viewer3dPrefView();
      assertFalse(page.force3d());
      page.setForce3d(true);
      PreferenceDialog dialog = new PreferenceDialog(null, List.of(page));
      dialog.applyAndClose();
      assertTrue(prefs.getBooleanProperty(Viewer3dPrefView.PREF_FORCE_3D, false));
      assertEquals("true", System.getProperty(Viewer3dPrefView.PREF_FORCE_3D));
      Viewer3dPrefView reloaded = new Viewer3dPrefView();
      assertTrue(reloaded.force3d());
    } finally {
      restorePref(prefs, previousPref);
      restore(Viewer3dPrefView.PREF_FORCE_3D, previous);
    }
  }

  private static void restorePref(WProperties prefs, String previous) {
    if (previous == null) {
      prefs.remove(Viewer3dPrefView.PREF_FORCE_3D);
    } else {
      prefs.setProperty(Viewer3dPrefView.PREF_FORCE_3D, previous);
    }
  }

  private static void restore(String key, String previous) {
    if (previous == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, previous);
    }
  }
}
