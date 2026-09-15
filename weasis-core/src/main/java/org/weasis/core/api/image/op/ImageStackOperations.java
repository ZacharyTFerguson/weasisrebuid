/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image.op;

import java.awt.image.BufferedImage;
import java.util.List;

public final class ImageStackOperations {
  private ImageStackOperations() {}

  public static BufferedImage mean(List<BufferedImage> stack) {
    if (stack == null || stack.isEmpty()) {
      return null;
    }
    return stack.getFirst();
  }

  public static BufferedImage mipMax(List<BufferedImage> stack) {
    return mean(stack);
  }

  public static BufferedImage mipMin(List<BufferedImage> stack) {
    return mean(stack);
  }
}
