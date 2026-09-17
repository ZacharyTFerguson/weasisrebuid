/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.wado;

import org.weasis.core.api.service.WProperties;

/**
 * WADO-path wrapper around explorer {@link org.weasis.dicom.explorer.SeriesDownloadManager} (MX-10
 * / MX-11).
 */
public class SeriesDownloadManager {

  private final org.weasis.dicom.explorer.SeriesDownloadManager inner;

  public SeriesDownloadManager() {
    this(new org.weasis.dicom.explorer.SeriesDownloadManager());
  }

  public SeriesDownloadManager(int seriesCap, int imageCap) {
    this(new org.weasis.dicom.explorer.SeriesDownloadManager(seriesCap, imageCap));
  }

  public SeriesDownloadManager(org.weasis.dicom.explorer.SeriesDownloadManager inner) {
    this.inner = inner == null ? new org.weasis.dicom.explorer.SeriesDownloadManager() : inner;
  }

  public static SeriesDownloadManager fromPrefs(WProperties prefs) {
    return new SeriesDownloadManager(
        org.weasis.dicom.explorer.SeriesDownloadManager.fromPrefs(prefs));
  }

  public org.weasis.dicom.explorer.SeriesDownloadManager inner() {
    return inner;
  }

  public int seriesCap() {
    return inner.seriesCap();
  }

  public int imageCap() {
    return inner.imageCap();
  }

  public int peakConcurrentSeries() {
    return inner.peakConcurrentSeries();
  }

  public int peakConcurrentImages() {
    return inner.peakConcurrentImages();
  }

  public void runSeries(Runnable task) {
    inner.runSeries(task);
  }

  public void runImagesInSeries(int count, Runnable each) {
    inner.runImagesInSeries(count, each);
  }
}
