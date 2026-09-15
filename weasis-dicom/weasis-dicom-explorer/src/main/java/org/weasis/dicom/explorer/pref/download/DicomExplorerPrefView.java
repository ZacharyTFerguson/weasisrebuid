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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.pref.ShellPrefPage;
import org.weasis.dicom.explorer.SeriesDownloadManager;
import org.weasis.dicom.explorer.wado.DicomManager;

/** Concurrent series/images (MX-10 / MX-11) and download-immediately. */
public class DicomExplorerPrefView extends ShellPrefPage {

  public static final String TITLE = "DICOM Explorer";

  private final WProperties prefs;
  private final JSpinner seriesSpinner;
  private final JSpinner imagesSpinner;
  private final JCheckBox immediatelyBox;

  public DicomExplorerPrefView() {
    this(DicomManager.prefs());
  }

  public DicomExplorerPrefView(WProperties prefs) {
    super(TITLE, 310);
    this.prefs = prefs == null ? new WProperties() : prefs;
    JPanel form = new JPanel();
    form.add(new JLabel("Concurrent series"));
    seriesSpinner =
        new JSpinner(
            new SpinnerNumberModel(
                this.prefs.getIntProperty(
                    SeriesDownloadManager.PREF_SERIES, SeriesDownloadManager.CONCURRENT_SERIES),
                1,
                64,
                1));
    form.add(seriesSpinner);
    form.add(new JLabel("Concurrent images in series"));
    imagesSpinner =
        new JSpinner(
            new SpinnerNumberModel(
                this.prefs.getIntProperty(
                    SeriesDownloadManager.PREF_IMAGES,
                    SeriesDownloadManager.CONCURRENT_DOWNLOADS_IN_SERIES),
                1,
                64,
                1));
    form.add(imagesSpinner);
    immediatelyBox =
        new JCheckBox(
            "Download immediately",
            this.prefs.getBooleanProperty(DicomManager.PREF_DOWNLOAD_IMMEDIATELY, true));
    form.add(immediatelyBox);
    add(form);
  }

  public int concurrentSeries() {
    return ((Number) seriesSpinner.getValue()).intValue();
  }

  public int concurrentImages() {
    return ((Number) imagesSpinner.getValue()).intValue();
  }

  public boolean downloadImmediately() {
    return immediatelyBox.isSelected();
  }

  public void setConcurrentSeries(int value) {
    seriesSpinner.setValue(value);
  }

  public void setConcurrentImages(int value) {
    imagesSpinner.setValue(value);
  }

  public void setDownloadImmediately(boolean value) {
    immediatelyBox.setSelected(value);
  }

  @Override
  public void closeAdditionalWindow() {
    prefs.putIntProperty(SeriesDownloadManager.PREF_SERIES, concurrentSeries());
    prefs.putIntProperty(SeriesDownloadManager.PREF_IMAGES, concurrentImages());
    prefs.putBooleanProperty(DicomManager.PREF_DOWNLOAD_IMMEDIATELY, downloadImmediately());
  }

  @Override
  public void resetToDefaultValues() {
    seriesSpinner.setValue(SeriesDownloadManager.CONCURRENT_SERIES);
    imagesSpinner.setValue(SeriesDownloadManager.CONCURRENT_DOWNLOADS_IN_SERIES);
    immediatelyBox.setSelected(true);
  }
}
