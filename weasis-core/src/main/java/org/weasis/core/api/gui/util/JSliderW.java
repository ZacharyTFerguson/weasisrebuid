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

import javax.swing.JSlider;

public class JSliderW extends JSlider {
  private boolean displayValueInTitle = true;

  public JSliderW() {
    super();
  }

  public JSliderW(int min, int max, int value) {
    super(min, max, value);
  }

  public boolean isDisplayValueInTitle() {
    return displayValueInTitle;
  }

  public void setDisplayValueInTitle(boolean displayValueInTitle) {
    this.displayValueInTitle = displayValueInTitle;
  }

  public int getUnmodifiedValue() {
    return getValue();
  }
}
