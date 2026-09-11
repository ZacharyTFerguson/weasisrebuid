/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.docking;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;

/** Default dockable tool pane. */
public abstract class PluginTool extends JPanel implements DockableTool {

  private final String name;
  private int position;
  private boolean enabled = true;
  private boolean visibleDock = true;

  protected PluginTool(String name, int position) {
    super(new BorderLayout());
    this.name = name == null ? "" : name;
    this.position = position;
  }

  @Override
  public String getComponentName() {
    return name;
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

  @Override
  public Component getToolComponent() {
    return this;
  }

  @Override
  public void showDockable() {
    visibleDock = true;
    setVisible(true);
  }

  @Override
  public void closeDockable() {
    visibleDock = false;
    setVisible(false);
  }

  public boolean isDockVisible() {
    return visibleDock;
  }
}
