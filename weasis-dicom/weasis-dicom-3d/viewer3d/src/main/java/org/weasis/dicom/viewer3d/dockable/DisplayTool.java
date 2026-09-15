/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.dockable;

import java.awt.BorderLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.weasis.core.api.gui.Insertable;

public class DisplayTool extends JPanel implements Insertable {

  public static final String NAME = "Display";
  private final JCheckBox info = new JCheckBox("Info", true);
  private int position = 131;
  private boolean enabled = true;

  public DisplayTool() {
    super(new BorderLayout());
    add(info, BorderLayout.CENTER);
  }

  public boolean isInfoVisible() {
    return info.isSelected();
  }

  public void setInfoVisible(boolean visible) {
    info.setSelected(visible);
  }

  @Override
  public String getComponentName() {
    return NAME;
  }

  @Override
  public Type getType() {
    return Type.TOOL;
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
