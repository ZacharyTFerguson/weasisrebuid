/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions.rectify;

import org.weasis.acquire.dockable.components.util.AbstractSliderComponent;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.operations.impl.RectifyOrientationChangeListener;

public class OrientationSliderComponent extends AbstractSliderComponent {

  public OrientationSliderComponent() {
    super(0, 270, 0);
  }

  public int degrees() {
    return getValue() / 90 * 90;
  }

  public void applyTo(AcquireImageValues values) {
    applyTo(values, null);
  }

  public void applyTo(AcquireImageValues values, RectifyOrientationChangeListener listener) {
    if (values != null) {
      values.setRotation(degrees());
    }
    if (listener != null) {
      listener.orientationChanged(degrees());
    }
  }
}
