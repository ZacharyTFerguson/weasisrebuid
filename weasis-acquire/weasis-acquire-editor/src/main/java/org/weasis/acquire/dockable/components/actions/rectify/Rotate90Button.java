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

import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.operations.impl.RotationActionListener;

public class Rotate90Button {

  public void apply(AcquireImageValues values) {
    apply(values, null);
  }

  public void apply(AcquireImageValues values, RotationActionListener listener) {
    if (values == null) {
      return;
    }
    values.setRotation(values.getRotation() + 90);
    if (listener != null) {
      listener.rotationChanged(values.getRotation());
    }
  }
}
