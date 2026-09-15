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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.editor.image.DefaultView2d;

class TitlePrefHaveTest {

  @Test
  void factoriesCreateLoggingLauncherAndMonitorPages() {
    LoggingPrefFactory logging = new LoggingPrefFactory();
    LauncherPrefFactory launchers = new LauncherPrefFactory();
    ScreenPrefFactory monitors = new ScreenPrefFactory();
    AbstractItemDialogPage logPage = logging.createInstance(null);
    AbstractItemDialogPage launcherPage = launchers.createInstance(null);
    AbstractItemDialogPage monitorPage = monitors.createInstance(null);
    assertInstanceOf(LoggingPrefView.class, logPage);
    assertInstanceOf(LauncherPrefView.class, launcherPage);
    assertInstanceOf(ScreenPrefView.class, monitorPage);
    assertEquals(Insertable.Type.PREFERENCES, logging.getType());
    assertTrue(logging.isComponentCreatedByThisFactory(logPage));
    assertTrue(launchers.isComponentCreatedByThisFactory(launcherPage));
    assertTrue(monitors.isComponentCreatedByThisFactory(monitorPage));
    assertEquals(LoggingPrefView.TITLE, logPage.getTitle());
    assertEquals(LauncherPrefView.TITLE, launcherPage.getTitle());
    assertEquals(ScreenPrefView.TITLE, monitorPage.getTitle());
  }

  @Test
  void loggingPersistsDocumentedFelixAndSlingTokens() {
    String felix = System.getProperty(LoggingPrefView.PREF_FELIX);
    String sling = System.getProperty(LoggingPrefView.PREF_SLING);
    String stack = System.getProperty(LoggingPrefView.PREF_STACK);
    try {
      WProperties prefs = new WProperties();
      LoggingPrefView page = new LoggingPrefView(prefs);
      assertEquals(LoggingPrefView.DEFAULT_FELIX, page.felixLevel());
      assertEquals(LoggingPrefView.DEFAULT_SLING, page.slingLevel());
      assertEquals(LoggingPrefView.DEFAULT_STACK, page.stackLimit());
      page.setFelixLevel(2);
      page.setSlingLevel("DEBUG");
      page.setStackLimit(-1);
      page.closeAdditionalWindow();
      assertEquals(2, prefs.getIntProperty(LoggingPrefView.PREF_FELIX, 0));
      assertEquals("DEBUG", prefs.getProperty(LoggingPrefView.PREF_SLING));
      assertEquals(-1, prefs.getIntProperty(LoggingPrefView.PREF_STACK, 0));
      assertEquals("2", System.getProperty(LoggingPrefView.PREF_FELIX));
      assertEquals("DEBUG", System.getProperty(LoggingPrefView.PREF_SLING));
      assertEquals("-1", System.getProperty(LoggingPrefView.PREF_STACK));

      LoggingPrefView loaded = new LoggingPrefView(prefs);
      assertEquals(2, loaded.felixLevel());
      assertEquals("DEBUG", loaded.slingLevel());
      loaded.resetToDefaultValues();
      assertEquals(1, loaded.felixLevel());
      assertEquals("INFO", loaded.slingLevel());
      assertEquals(3, loaded.stackLimit());
    } finally {
      restore(LoggingPrefView.PREF_FELIX, felix);
      restore(LoggingPrefView.PREF_SLING, sling);
      restore(LoggingPrefView.PREF_STACK, stack);
    }
  }

  @Test
  void launcherPersistsDocumentedImportExportSendTokens() {
    String imp = System.getProperty(LauncherPrefView.PREF_IMPORT);
    String qr = System.getProperty(LauncherPrefView.PREF_QR);
    String exp = System.getProperty(LauncherPrefView.PREF_EXPORT);
    String send = System.getProperty(LauncherPrefView.PREF_SEND);
    try {
      WProperties prefs = new WProperties();
      LauncherPrefView page = new LauncherPrefView(prefs);
      assertTrue(page.importDicom());
      assertTrue(page.importQr());
      assertTrue(page.exportDicom());
      assertTrue(page.sendDicom());
      page.setImportDicom(false);
      page.setImportQr(false);
      page.setExportDicom(false);
      page.setSendDicom(false);
      page.closeAdditionalWindow();
      assertFalse(prefs.getBooleanProperty(LauncherPrefView.PREF_IMPORT, true));
      assertFalse(prefs.getBooleanProperty(LauncherPrefView.PREF_QR, true));
      assertFalse(prefs.getBooleanProperty(LauncherPrefView.PREF_EXPORT, true));
      assertFalse(prefs.getBooleanProperty(LauncherPrefView.PREF_SEND, true));
      assertEquals("false", System.getProperty(LauncherPrefView.PREF_EXPORT));

      LauncherPrefView loaded = new LauncherPrefView(prefs);
      assertFalse(loaded.exportDicom());
      loaded.resetToDefaultValues();
      assertTrue(loaded.importDicom());
      assertTrue(loaded.exportDicom());
      assertTrue(loaded.sendDicom());
    } finally {
      restore(LauncherPrefView.PREF_IMPORT, imp);
      restore(LauncherPrefView.PREF_QR, qr);
      restore(LauncherPrefView.PREF_EXPORT, exp);
      restore(LauncherPrefView.PREF_SEND, send);
    }
  }

  @Test
  void monitorPitchIsNotSessionManualCalibration() {
    Monitor monitor = new Monitor(null);
    ScreenPrefView page = new ScreenPrefView(monitor);
    assertEquals(ScreenPrefView.DEFAULT_PITCH_MM, page.pitchXmm(), 1e-9);
    page.setPitchXmm(0.31);
    page.closeAdditionalWindow();
    assertEquals(0.31, monitor.getPitchXmm(), 1e-9);
    DefaultView2d<?> view = new DefaultView2d<>();
    page.applyTo(view);
    view.setSessionManualCalibrationMmPerPixel(2.0);
    assertEquals(0.31, view.getMonitorCalibrationMmPerPixel(), 1e-9);
    assertEquals(2.0, view.getSessionManualCalibrationMmPerPixel(), 1e-9);
    assertNotEquals(
        view.getMonitorCalibrationMmPerPixel(), view.getSessionManualCalibrationMmPerPixel());
    page.resetToDefaultValues();
    assertEquals(ScreenPrefView.DEFAULT_PITCH_MM, page.pitchXmm(), 1e-9);
  }

  private static void restore(String key, String previous) {
    if (previous == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, previous);
    }
  }
}
