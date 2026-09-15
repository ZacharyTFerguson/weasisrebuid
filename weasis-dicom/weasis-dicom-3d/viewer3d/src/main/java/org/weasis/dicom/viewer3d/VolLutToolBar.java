/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.util.Toolbar;
import org.weasis.dicom.viewer3d.vr.lut.VolumePreset;

public class VolLutToolBar implements Toolbar {

  public static final String NAME = "Volume LUT";
  private final JToolBar bar = new JToolBar(NAME);
  private VolumePreset selected = VolumePreset.ctSoftTissue();
  private int position = 122;
  private boolean enabled = true;

  public VolumePreset getSelected() {
    return selected;
  }

  public void setSelected(VolumePreset selected) {
    if (selected != null) {
      this.selected = selected;
    }
  }

  @Override
  public JComponent getComponent() {
    return bar;
  }

  @Override
  public String getComponentName() {
    return NAME;
  }

  @Override
  public Insertable.Type getType() {
    return Insertable.Type.TOOLBAR;
  }

  @Override
  public int getComponentPosition() {
    return position;
  }

  @Override
  public void setComponentPosition(int position) {
    this.position = position;
  }

  @Override
  public boolean isComponentEnabled() {
    return enabled;
  }

  @Override
  public void setComponentEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
