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
import org.weasis.dicom.viewer3d.vr.RenderingType;
import org.weasis.dicom.viewer3d.vr.View3d;

/** 3D chrome: COMPOSITE/MIP/MINIP/ISO on {@link EventManager#getSelectedView()}. */
public class View3DToolbar implements Toolbar {

  public static final String NAME = "3D";
  private final JToolBar bar = new JToolBar(NAME);
  private int position = 120;
  private boolean enabled = true;
  private RenderingType selected = RenderingType.COMPOSITE;

  public View3DToolbar() {
    for (RenderingType type : RenderingType.values()) {
      bar.add(button(type));
    }
  }

  public RenderingType getSelected() {
    return selected;
  }

  public void select(RenderingType type) {
    if (type == null) {
      return;
    }
    selected = type;
    applySelected();
  }

  public void applySelected() {
    View3d view = EventManager.getInstance().getSelectedView();
    if (view != null) {
      view.setRenderingType(selected);
    }
  }

  private JButton button(RenderingType type) {
    JButton button =
        new JButton(
            new AbstractAction(type.name()) {
              @Override
              public void actionPerformed(ActionEvent e) {
                select(type);
              }
            });
    button.setToolTipText(type.name());
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
