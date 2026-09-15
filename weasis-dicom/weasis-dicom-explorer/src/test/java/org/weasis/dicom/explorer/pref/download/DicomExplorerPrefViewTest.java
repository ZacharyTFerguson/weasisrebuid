/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pref.download;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.InsertableUtil;
import org.weasis.core.api.service.WProperties;
import org.weasis.dicom.explorer.SeriesDownloadManager;
import org.weasis.dicom.explorer.wado.DicomManager;

class DicomExplorerPrefViewTest {

  @Test
  void defaultsAreMx10Mx11() {
    WProperties prefs = new WProperties();
    DicomExplorerPrefView view = new DicomExplorerPrefView(prefs);
    assertEquals(SeriesDownloadManager.CONCURRENT_SERIES, view.concurrentSeries());
    assertEquals(SeriesDownloadManager.CONCURRENT_DOWNLOADS_IN_SERIES, view.concurrentImages());
    assertTrue(view.downloadImmediately());
    view.setConcurrentSeries(2);
    view.setConcurrentImages(1);
    view.setDownloadImmediately(false);
    view.closeAdditionalWindow();
    assertEquals(2, prefs.getIntProperty(SeriesDownloadManager.PREF_SERIES, 0));
    assertEquals(1, prefs.getIntProperty(SeriesDownloadManager.PREF_IMAGES, 0));
    assertFalse(prefs.getBooleanProperty(DicomManager.PREF_DOWNLOAD_IMMEDIATELY, true));
    view.resetToDefaultValues();
    assertEquals(3, view.concurrentSeries());
    assertEquals(4, view.concurrentImages());
  }

  @Test
  void factoryHonorsDicomizerDisablement() {
    DicomExplorerPrefFactory factory = new DicomExplorerPrefFactory();
    String key = DicomExplorerPrefFactory.class.getName();
    String previous = System.getProperty(key);
    try {
      System.clearProperty(key);
      assertTrue(InsertableUtil.isFactoryEnabled(DicomExplorerPrefFactory.class));
      assertNotNull(factory.createInstance(null));
      System.setProperty(key, "false");
      assertFalse(InsertableUtil.isFactoryEnabled(DicomExplorerPrefFactory.class));
      assertNull(factory.createInstance(null));
    } finally {
      if (previous == null) {
        System.clearProperty(key);
      } else {
        System.setProperty(key, previous);
      }
    }
  }
}
