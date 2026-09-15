/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.editor;

public class SplitLayout {
  private SplitPosition position = SplitPosition.NONE;
  private double divider = 0.5;

  public SplitPosition getPosition() {
    return position;
  }

  public void setPosition(SplitPosition position) {
    this.position = position == null ? SplitPosition.NONE : position;
  }

  public double getDivider() {
    return divider;
  }

  public void setDivider(double divider) {
    this.divider = divider;
  }
}
