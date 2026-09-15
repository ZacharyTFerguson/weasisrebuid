/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui.util;

public class BasicActionState implements ActionState {
  private final Feature<?> action;
  private boolean enabled = true;

  public BasicActionState(Feature<?> action) {
    this.action = action;
  }

  @Override
  public Feature<?> getActionW() {
    return action;
  }

  @Override
  public boolean isActionEnabled() {
    return enabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
