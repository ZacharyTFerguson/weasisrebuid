/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.tp.raven.spinner.render;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class RingSpinner implements SpinnerRender {
  @Override
  public void paint(Graphics2D g2, Rectangle bounds, float fraction) {
    if (g2 == null || bounds == null) {
      return;
    }
    g2.setStroke(new BasicStroke(3f));
    int arc = (int) (360 * (fraction - Math.floor(fraction)));
    g2.drawArc(bounds.x + 4, bounds.y + 4, bounds.width - 8, bounds.height - 8, 0, arc);
  }
}
