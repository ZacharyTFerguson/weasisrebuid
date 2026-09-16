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

/**
 * Draw {@code P_OVERLAY} onto the source at (0,0). Missing overlay is a passthrough. Output is a
 * copy.
 */
public class MergeImgOp extends AbstractOp {

  public static final String P_OVERLAY = "overlay";

  public MergeImgOp() {
    super("op.merge");
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, mergeIfNeeded(getParam(INPUT_IMG)));
  }

  Object mergeIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src)) {
      return in;
    }
    Object value = getParam(P_OVERLAY);
    if (!(value instanceof BufferedImage overlay)) {
      return in;
    }
    return merge(src, overlay);
  }

  static BufferedImage merge(BufferedImage src, BufferedImage overlay) {
    BufferedImage dst = BrightnessOp.canvas(src);
    Graphics2D g = dst.createGraphics();
    try {
      g.drawImage(src, 0, 0, null);
      g.drawImage(overlay, 0, 0, null);
    } finally {
      g.dispose();
    }
    return dst;
  }
}
