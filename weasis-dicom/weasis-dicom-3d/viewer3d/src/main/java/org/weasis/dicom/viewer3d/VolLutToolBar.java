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
import org.weasis.core.ui.util.WtoolBar;
import org.weasis.dicom.viewer3d.vr.PresetRadioMenu;
import org.weasis.dicom.viewer3d.vr.View3d;
import org.weasis.dicom.viewer3d.vr.lut.VolumePreset;

/** Volume LUT chrome. Presets apply to {@link EventManager#getSelectedView()}. */
public class VolLutToolBar extends WtoolBar {

  public static final String NAME = "Volume LUT";
  private final PresetRadioMenu menu = new PresetRadioMenu();

  public VolLutToolBar() {
    super(NAME, 122);
    for (VolumePreset preset : menu.getPresets()) {
      add(button(preset));
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
    button.setName(preset.getName());
    return button;
  }
}
