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

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.pref.ShellPrefPage;
import org.weasis.dicom.viewer3d.OpenGLInfo;

/**
 * Prefs &gt; 3D Viewer. Token is the documented PREFERENCES.md key {@code weasis.force.3d} (default
 * false).
 */
public class Viewer3dPrefView extends ShellPrefPage {

  public static final String TITLE = "3D Viewer (OpenGL " + OpenGLInfo.MIN_VERSION + "+)";
  public static final String PREF_FORCE_3D = "weasis.force.3d";
  public static final boolean DEFAULT_FORCE_3D = false;

  private final WProperties prefs;
  private final JCheckBox force3dBox;

  public Viewer3dPrefView() {
    this(UICore.getInstance().getSystemPreferences());
  }

  public Viewer3dPrefView(WProperties prefs) {
    super(TITLE, 520);
    this.prefs = prefs == null ? new WProperties() : prefs;
    force3dBox = new JCheckBox("Force 3D", loadedForce3d());
    force3dBox.setName("force3d");
    JPanel form = new JPanel();
    form.add(force3dBox);
    add(form);
  }

  public boolean force3d() {
    return force3dBox.isSelected();
  }

  public void setForce3d(boolean force) {
    force3dBox.setSelected(force);
  }

  public static boolean force3dProperty() {
    return Boolean.parseBoolean(System.getProperty(PREF_FORCE_3D, "false"));
  }

  @Override
  public void closeAdditionalWindow() {
    prefs.putBooleanProperty(PREF_FORCE_3D, force3d());
    System.setProperty(PREF_FORCE_3D, Boolean.toString(force3d()));
  }

  @Override
  public void resetToDefaultValues() {
    force3dBox.setSelected(DEFAULT_FORCE_3D);
  }

  boolean loadedForce3d() {
    return prefs.getBooleanProperty(PREF_FORCE_3D, systemForce3d());
  }

  static boolean systemForce3d() {
    return Boolean.parseBoolean(System.getProperty(PREF_FORCE_3D, "false"));
  }
}
