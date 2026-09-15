/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.gui.util;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class GhostGlassPane extends JPanel {
  private BufferedImage image;
  private Point location = new Point();

  public GhostGlassPane() {
    setOpaque(false);
  }

  public void setImage(BufferedImage image) {
    this.image = image;
    repaint();
  }

  public void setPoint(Point location) {
    this.location = location == null ? new Point() : location;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (image == null) {
      return;
    }
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
    g2.drawImage(image, location.x, location.y, null);
    g2.dispose();
  }
}
