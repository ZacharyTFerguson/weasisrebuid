/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

public class ImagePrint implements Printable {
  private final BufferedImage image;
  private final PrintOptions options;

  public ImagePrint(BufferedImage image, PrintOptions options) {
    this.image = image;
    this.options = options == null ? new PrintOptions() : options;
  }

  public PrintOptions getOptions() {
    return options;
  }

  public BufferedImage getImage() {
    return image;
  }

  @Override
  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0 || image == null) {
      return NO_SUCH_PAGE;
    }
    Graphics2D g2 = (Graphics2D) graphics;
    g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    g2.drawImage(image, 0, 0, null);
    return PAGE_EXISTS;
  }
}
