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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.JPanel;
import org.weasis.core.ui.model.graphic.Graphic;

/** Overlay pane that paints the bound view's graphic model. */
public class GraphicsPane extends JPanel {

  private DefaultView2d<?> view;

  public GraphicsPane() {
    setOpaque(false);
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    repaint();
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public List<Graphic> getGraphics() {
    return view == null ? List.of() : view.getGraphicList();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (view == null) {
      return;
    }
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      for (Graphic graphic : view.getGraphicList()) {
        if (graphic.getShape() == null) {
          continue;
        }
        g2.setPaint(graphic.getColorPaint() == null ? Color.YELLOW : graphic.getColorPaint());
        g2.draw(graphic.getShape());
      }
    } finally {
      g2.dispose();
    }
  }
}
