/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions;

import java.awt.image.BufferedImage;
import org.weasis.acquire.dockable.components.actions.contrast.ContrastAction;
import org.weasis.acquire.dockable.components.actions.rectify.RectifyAction;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.graphics.CropRectangleGraphic;

/** Applies rotation, then crop, then brightness/contrast. */
public class AcquireAction {

  public BufferedImage apply(BufferedImage src, AcquireImageValues values) {
    if (src == null) {
      return null;
    }
    BufferedImage out = new RectifyAction().apply(src, values);
    if (values != null && values.getCrop() != null) {
      out = new CropRectangleGraphic(values.getCrop()).crop(out);
    }
    return new ContrastAction().apply(out, values);
  }
}
