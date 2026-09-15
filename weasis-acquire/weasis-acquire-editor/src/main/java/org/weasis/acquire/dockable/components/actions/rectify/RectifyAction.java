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

import java.awt.image.BufferedImage;
import org.weasis.acquire.editor.PhotoEdits;
import org.weasis.acquire.explorer.AcquireImageValues;

/** 90° rectify steps used by the dicomizer photo editor. */
public class RectifyAction {

  public BufferedImage apply(BufferedImage src, AcquireImageValues values) {
    if (src == null) {
      return null;
    }
    int steps = values == null ? 0 : ((values.getRotation() / 90) % 4 + 4) % 4;
    BufferedImage out = src;
    for (int i = 0; i < steps; i++) {
      out = PhotoEdits.rotate90(out);
    }
    return out;
  }
}
