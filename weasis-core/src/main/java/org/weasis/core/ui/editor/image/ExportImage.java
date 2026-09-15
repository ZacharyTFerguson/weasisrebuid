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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Copy the view source raster for screenshot / transfer. */
public class ExportImage {

  public BufferedImage render(DefaultView2d<?> view) {
    if (missing(view)) {
      return empty();
    }
    return copy(view.getSourceImage());
  }

  boolean missing(DefaultView2d<?> view) {
    return view == null || view.getSourceImage() == null;
  }

  static BufferedImage empty() {
    return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
  }

  static BufferedImage copy(BufferedImage src) {
    BufferedImage out =
        new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g = out.createGraphics();
    g.drawImage(src, 0, 0, null);
    g.dispose();
    return out;
  }
}
