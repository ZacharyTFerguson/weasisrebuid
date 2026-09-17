/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Horizontal flip (Alt+F). {@code P_HORIZONTAL=false} is a passthrough. */
public class FlipOp extends AbstractOp {

  public static final String P_HORIZONTAL = "horizontal";

  public FlipOp() {
    super("op.flip");
    setParam(P_HORIZONTAL, Boolean.FALSE);
  }

  @Override
  protected void processEnabled() {
    Object in = getParam(INPUT_IMG);
    setParam(OUTPUT_IMG, flipIfNeeded(in));
  }

  Object flipIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src) || !horizontal()) {
      return in;
    }
    return flipHorizontal(src);
  }

  boolean horizontal() {
    Object value = getParam(P_HORIZONTAL);
    return Boolean.TRUE.equals(value) || "true".equalsIgnoreCase(String.valueOf(value));
  }

  static BufferedImage flipHorizontal(BufferedImage src) {
    int type =
        src.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : src.getType();
    BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), type);
    Graphics2D g = dst.createGraphics();
    try {
      g.drawImage(
          src, src.getWidth(), 0, 0, src.getHeight(), 0, 0, src.getWidth(), src.getHeight(), null);
    } finally {
      g.dispose();
    }
    return dst;
  }
}
