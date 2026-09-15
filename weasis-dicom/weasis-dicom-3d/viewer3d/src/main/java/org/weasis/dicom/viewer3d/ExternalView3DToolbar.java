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

import java.util.Hashtable;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.util.Toolbar;

/** 2D-side chrome that opens the current series in the DICOM 3D viewer. */
public class ExternalView3DToolbar implements Toolbar {

  public static final String NAME = "3D External";
  private final JToolBar bar = new JToolBar(NAME);
  private final View3DFactory factory = new View3DFactory();
  private final JButton open = new JButton("3D");
  private int position = 121;
  private boolean enabled = true;
  private View3DContainer lastOpened;

  public ExternalView3DToolbar() {
    bar.add(open);
    open.addActionListener(e -> open3d(new Hashtable<>()));
  }

  public View3DContainer open3d(Hashtable<String, Object> properties) {
    SeriesViewer<?> viewer = factory.createSeriesViewer(properties);
    if (viewer instanceof View3DContainer container) {
      lastOpened = container;
      EventManager.getInstance().setAction(ActionVol.RENDERING_TYPE);
      return container;
    }
    return null;
  }

  public View3DContainer getLastOpened() {
    return lastOpened;
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
