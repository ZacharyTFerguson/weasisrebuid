/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.util.Toolbar;

public class KeyObjectToolBar implements Toolbar {

  public static final String NAME = "Key Object";

  private final KOManager manager;
  private final JToolBar bar = new JToolBar(NAME);
  private int position = 50;
  private boolean enabled = true;

  public KeyObjectToolBar() {
    this(new KOManager());
  }

  public KeyObjectToolBar(KOManager manager) {
    this.manager = manager == null ? new KOManager() : manager;
  }

  public KOManager getManager() {
    return manager;
  }

  public boolean toggle(String sopInstanceUid) {
    return manager.toggleKeyImage(sopInstanceUid);
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
