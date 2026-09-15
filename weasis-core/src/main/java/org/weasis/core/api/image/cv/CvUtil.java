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

public final class CvUtil {
  private CvUtil() {}

  public static BufferedImage toGray(BufferedImage src) {
    if (src == null) {
      return null;
    }
    if (src.getType() == BufferedImage.TYPE_BYTE_GRAY) {
      return src;
    }
    BufferedImage gray =
        new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    gray.getGraphics().drawImage(src, 0, 0, null);
    return gray;
  }

  public static boolean isNativeAvailable() {
    return false;
  }
}
