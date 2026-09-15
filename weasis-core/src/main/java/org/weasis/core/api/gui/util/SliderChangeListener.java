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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class SliderChangeListener extends BasicActionState implements ChangeListener {
  private final JSliderW slider;

  protected SliderChangeListener(Feature<?> action, int min, int max, int value) {
    super(action);
    this.slider = new JSliderW(min, max, value);
    this.slider.addChangeListener(this);
  }

  public JSliderW getSlider() {
    return slider;
  }

  public int getSliderValue() {
    return slider.getValue();
  }

  public void setSliderValue(int value) {
    slider.setValue(value);
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    stateChanged(slider.getValue());
  }

  public abstract void stateChanged(int value);
}
