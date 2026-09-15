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

import java.awt.image.BufferedImage;
import org.weasis.acquire.editor.PhotoEdits;
import org.weasis.acquire.explorer.AcquireImageValues;

public class ContrastAction {

  public BufferedImage apply(BufferedImage src, AcquireImageValues values) {
    if (src == null) {
      return null;
    }
    if (values == null) {
      return src;
    }
    return PhotoEdits.contrast(src, values.getContrast(), values.getBrightness());
  }
}
