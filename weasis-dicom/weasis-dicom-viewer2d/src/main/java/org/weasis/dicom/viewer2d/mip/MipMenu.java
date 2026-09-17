/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mip;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class MipMenu extends JPanel {

  private final JComboBox<MipView.Type> types = new JComboBox<>(MipView.Type.values());

  public MipMenu() {
    add(types);
  }

  public MipView.Type getSelectedType() {
    MipView.Type t = (MipView.Type) types.getSelectedItem();
    return t == null ? MipView.Type.NONE : t;
  }

  public void setSelectedType(MipView.Type type) {
    types.setSelectedItem(type == null ? MipView.Type.NONE : type);
  }
}
