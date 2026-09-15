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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.util.Toolbar;
import org.weasis.dicom.viewer3d.vr.PresetRadioMenu;
import org.weasis.dicom.viewer3d.vr.View3d;
import org.weasis.dicom.viewer3d.vr.lut.VolumePreset;

/** Volume LUT chrome. Presets apply to {@link EventManager#getSelectedView()}. */
public class VolLutToolBar implements Toolbar {

  public static final String NAME = "Volume LUT";
  private final JToolBar bar = new JToolBar(NAME);
  private final PresetRadioMenu menu = new PresetRadioMenu();
  private int position = 122;
  private boolean enabled = true;

  public VolLutToolBar() {
    for (VolumePreset preset : menu.getPresets()) {
      bar.add(button(preset));
    }
  }

  public VolumePreset getSelected() {
    return menu.getSelected();
  }

  public void setSelected(VolumePreset selected) {
    menu.setSelected(selected);
    applySelected();
  }

  public void applySelected() {
    View3d view = EventManager.getInstance().getSelectedView();
    if (view != null) {
      view.setVolumePreset(getSelected());
    }
  }

  private JButton button(VolumePreset preset) {
    JButton button =
        new JButton(
            new AbstractAction(preset.getName()) {
              @Override
              public void actionPerformed(ActionEvent e) {
                setSelected(preset);
              }
            });
    button.setToolTipText(preset.getName());
    return button;
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
