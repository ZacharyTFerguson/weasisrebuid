/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.pref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.editor.image.MouseActions;
import org.weasis.dicom.codec.PRSpecialElement;
import org.weasis.dicom.codec.display.WindowAndPresetsOp;

class ViewerPrefHaveTest {

  @Test
  void factoryCreates2dViewerPage() {
    ViewerPrefFactory factory = new ViewerPrefFactory();
    AbstractItemDialogPage page = factory.createInstance(null);
    assertInstanceOf(ViewerPrefView.class, page);
    assertEquals(Insertable.Type.PREFERENCES, factory.getType());
    assertTrue(factory.isComponentCreatedByThisFactory(page));
    assertEquals(ViewerPrefView.TITLE, page.getTitle());
  }

  @Test
  void persistsDocumentedWlAndOverlayTokens() {
    String inverse = System.getProperty(ViewerPrefView.PREF_LEVEL_INVERSE);
    String color = System.getProperty(ViewerPrefView.PREF_COLOR_WL_APPLY);
    String pr = System.getProperty(ViewerPrefView.PREF_APPLY_LATEST_PR);
    String left = System.getProperty(ViewerPrefView.PREF_MOUSE_LEFT);
    String middle = System.getProperty(ViewerPrefView.PREF_MOUSE_MIDDLE);
    String right = System.getProperty(ViewerPrefView.PREF_MOUSE_RIGHT);
    String wheel = System.getProperty(ViewerPrefView.PREF_MOUSE_WHEEL);
    try {
      WProperties prefs = new WProperties();
      ViewerPrefView page = new ViewerPrefView(prefs);
      assertTrue(page.levelInverse());
      assertTrue(page.colorWlApply());
      assertFalse(page.applyLatestPr());
      assertEquals(MouseActions.WINLEVEL, page.mouseLeft());
      page.setLevelInverse(false);
      page.setColorWlApply(false);
      page.setApplyLatestPr(true);
      page.setMouseLeft(MouseActions.PAN);
      page.setMouseMiddle(MouseActions.ZOOM);
      page.setMouseRight(MouseActions.SCROLL);
      page.setMouseWheel(MouseActions.SEQUENCE);
      page.closeAdditionalWindow();
      assertFalse(prefs.getBooleanProperty(ViewerPrefView.PREF_LEVEL_INVERSE, true));
      assertFalse(prefs.getBooleanProperty(ViewerPrefView.PREF_COLOR_WL_APPLY, true));
      assertTrue(prefs.getBooleanProperty(ViewerPrefView.PREF_APPLY_LATEST_PR, false));
      assertEquals(MouseActions.PAN, prefs.getProperty(ViewerPrefView.PREF_MOUSE_LEFT));
      assertEquals(MouseActions.ZOOM, prefs.getProperty(ViewerPrefView.PREF_MOUSE_MIDDLE));
      assertEquals(MouseActions.SCROLL, prefs.getProperty(ViewerPrefView.PREF_MOUSE_RIGHT));
      assertEquals(MouseActions.SEQUENCE, prefs.getProperty(ViewerPrefView.PREF_MOUSE_WHEEL));
      assertFalse(WindowAndPresetsOp.levelInverse());
      assertTrue(PRSpecialElement.applyLatestPr());
      assertFalse(ViewerPrefView.colorWlApplyProperty());

      ViewerPrefView loaded = new ViewerPrefView(prefs);
      assertFalse(loaded.levelInverse());
      assertTrue(loaded.applyLatestPr());
      assertEquals(MouseActions.PAN, loaded.mouseLeft());
      loaded.resetToDefaultValues();
      assertTrue(loaded.levelInverse());
      assertTrue(loaded.colorWlApply());
      assertFalse(loaded.applyLatestPr());
      assertEquals(MouseActions.WINLEVEL, loaded.mouseLeft());
      assertEquals(MouseActions.PAN, loaded.mouseMiddle());
    } finally {
      restore(ViewerPrefView.PREF_LEVEL_INVERSE, inverse);
      restore(ViewerPrefView.PREF_COLOR_WL_APPLY, color);
      restore(ViewerPrefView.PREF_APPLY_LATEST_PR, pr);
      restore(ViewerPrefView.PREF_MOUSE_LEFT, left);
      restore(ViewerPrefView.PREF_MOUSE_MIDDLE, middle);
      restore(ViewerPrefView.PREF_MOUSE_RIGHT, right);
      restore(ViewerPrefView.PREF_MOUSE_WHEEL, wheel);
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
