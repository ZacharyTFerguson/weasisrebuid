/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;

/**
 * Prefs &gt; Launchers. Tokens are the documented PREFERENCES.md UI-visibility flags for Import /
 * Q/R / Export / Send.
 */
public class LauncherPrefView extends ShellPrefPage {

  public static final String TITLE = "Launchers";
  public static final String PREF_IMPORT = "weasis.import.dicom";
  public static final String PREF_QR = "weasis.import.dicom.qr";
  public static final String PREF_EXPORT = "weasis.export.dicom";
  public static final String PREF_SEND = "weasis.send.dicom";

  private final WProperties prefs;
  private final JCheckBox importBox;
  private final JCheckBox qrBox;
  private final JCheckBox exportBox;
  private final JCheckBox sendBox;

  public LauncherPrefView() {
    this(UICore.getInstance().getSystemPreferences());
  }

  public LauncherPrefView(WProperties prefs) {
    super(TITLE, 800);
    this.prefs = prefs == null ? new WProperties() : prefs;
    importBox = flagBox("Import DICOM", PREF_IMPORT);
    qrBox = flagBox("Import DICOM Q/R", PREF_QR);
    exportBox = flagBox("Export DICOM", PREF_EXPORT);
    sendBox = flagBox("Send DICOM", PREF_SEND);
    JPanel form = new JPanel();
    form.add(importBox);
    form.add(qrBox);
    form.add(exportBox);
    form.add(sendBox);
    add(form);
  }

  public boolean importDicom() {
    return importBox.isSelected();
  }

  public void setImportDicom(boolean value) {
    importBox.setSelected(value);
  }

  public boolean importQr() {
    return qrBox.isSelected();
  }

  public void setImportQr(boolean value) {
    qrBox.setSelected(value);
  }

  public boolean exportDicom() {
    return exportBox.isSelected();
  }

  public void setExportDicom(boolean value) {
    exportBox.setSelected(value);
  }

  public boolean sendDicom() {
    return sendBox.isSelected();
  }

  public void setSendDicom(boolean value) {
    sendBox.setSelected(value);
  }

  @Override
  public void closeAdditionalWindow() {
    putFlag(PREF_IMPORT, importDicom());
    putFlag(PREF_QR, importQr());
    putFlag(PREF_EXPORT, exportDicom());
    putFlag(PREF_SEND, sendDicom());
  }

  @Override
  public void resetToDefaultValues() {
    importBox.setSelected(true);
    qrBox.setSelected(true);
    exportBox.setSelected(true);
    sendBox.setSelected(true);
  }

  JCheckBox flagBox(String title, String key) {
    return new JCheckBox(title, flagPref(key));
  }

  boolean flagPref(String key) {
    return prefs.getBooleanProperty(key, defaultBoolean(key));
  }

  void putFlag(String key, boolean value) {
    prefs.putBooleanProperty(key, value);
    System.setProperty(key, Boolean.toString(value));
  }

  static boolean defaultBoolean(String key) {
    return Boolean.parseBoolean(System.getProperty(key, "true"));
  }
}
