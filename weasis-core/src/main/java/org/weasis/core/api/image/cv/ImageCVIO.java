/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.image.cv;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageCVIO {
  public BufferedImage read(File file) {
    if (file == null) {
      return null;
    }
    try {
      return ImageIO.read(file);
    } catch (Exception e) {
      return null;
    }
  }

  public boolean write(BufferedImage image, String format, File file) {
    if (image == null || file == null) {
      return false;
    }
    try {
      return ImageIO.write(image, format == null ? "png" : format, file);
    } catch (Exception e) {
      return false;
    }
  }
}
