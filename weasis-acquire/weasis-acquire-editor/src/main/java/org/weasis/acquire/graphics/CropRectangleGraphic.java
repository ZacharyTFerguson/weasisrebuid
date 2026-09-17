/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.graphics;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.weasis.acquire.editor.PhotoEdits;

public class CropRectangleGraphic {

  private Rectangle rectangle = new Rectangle();

  public CropRectangleGraphic() {}

  public CropRectangleGraphic(Rectangle rectangle) {
    setRectangle(rectangle);
  }

  public Rectangle getRectangle() {
    return new Rectangle(rectangle);
  }

  public void setRectangle(Rectangle rectangle) {
    this.rectangle = rectangle == null ? new Rectangle() : new Rectangle(rectangle);
  }

  public BufferedImage crop(BufferedImage src) {
    if (src == null || rectangle.width <= 0 || rectangle.height <= 0) {
      return src;
    }
    return PhotoEdits.crop(src, rectangle);
  }
}
