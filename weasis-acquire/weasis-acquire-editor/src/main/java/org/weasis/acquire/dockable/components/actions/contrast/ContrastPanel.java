/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions.contrast;

import org.weasis.acquire.dockable.components.actions.contrast.comp.BrightnessComponent;
import org.weasis.acquire.dockable.components.actions.contrast.comp.ContrastComponent;
import org.weasis.acquire.explorer.AcquireImageValues;

/** Brightness and contrast sliders that write pending {@link AcquireImageValues}. */
public class ContrastPanel {

  private final BrightnessComponent brightness = new BrightnessComponent();
  private final ContrastComponent contrast = new ContrastComponent();

  public BrightnessComponent brightness() {
    return brightness;
  }

  public ContrastComponent contrast() {
    return contrast;
  }

  public void applyTo(AcquireImageValues values) {
    brightness.applyTo(values);
    contrast.applyTo(values);
  }
}
