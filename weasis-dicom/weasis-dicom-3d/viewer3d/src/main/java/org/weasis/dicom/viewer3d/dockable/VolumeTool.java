/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.dockable;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.dicom.viewer3d.EventManager;
import org.weasis.dicom.viewer3d.vr.RenderingType;
import org.weasis.dicom.viewer3d.vr.View3d;

public class VolumeTool extends JPanel implements Insertable {

  public static final String NAME = "Volume";
  private int position = 130;
  private boolean enabled = true;
  private RenderingType renderingType = RenderingType.COMPOSITE;

  public VolumeTool() {
    super(new BorderLayout());
    JPanel types = new JPanel(new FlowLayout(FlowLayout.LEFT));
    for (RenderingType type : RenderingType.values()) {
      JButton button = new JButton(type.name());
      button.addActionListener(e -> setRenderingType(type));
      types.add(button);
    }
    add(types, BorderLayout.CENTER);
  }

  public RenderingType getRenderingType() {
    return renderingType;
  }

  public void setRenderingType(RenderingType renderingType) {
    if (renderingType == null) {
      return;
    }
    View3d view = EventManager.getInstance().getSelectedView();
    if (view != null) {
      view.setRenderingType(renderingType);
      this.renderingType = view.getRenderingType();
    } else {
      this.renderingType = renderingType;
    }
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
