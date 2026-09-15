/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.util;

import javax.swing.JToolBar;
import org.weasis.core.api.gui.Insertable;

public class WtoolBar extends JToolBar implements Insertable {
  private final String barName;
  private int position;
  private boolean componentEnabled = true;

  public WtoolBar(String barName, int position) {
    super(barName);
    this.barName = barName == null ? "toolbar" : barName;
    this.position = position;
  }

  @Override
  public String getComponentName() {
    return barName;
  }

  @Override
  public Type getType() {
    return Type.TOOLBAR;
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
    return componentEnabled;
  }

  @Override
  public void setComponentEnabled(boolean enabled) {
    this.componentEnabled = enabled;
    setEnabled(enabled);
  }
}
