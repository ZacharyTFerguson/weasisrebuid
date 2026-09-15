/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

/** Load / decode progress for a 2D view (0–100). */
public class ViewProgress {

  private int percent;

  public void setPercent(int percent) {
    this.percent = clamp(percent);
  }

  public int getPercent() {
    return percent;
  }

  public boolean isComplete() {
    return percent >= 100;
  }

  static int clamp(int percent) {
    if (percent < 0) {
      return 0;
    }
    if (percent > 100) {
      return 100;
    }
    return percent;
  }
}
