/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.ui.model.graphic.Graphic;

/** Overlay pane that paints the bound view's graphic model. */
public class GraphicsPane extends JPanel {

  private final JLabel count = new JLabel("0");
  private DefaultView2d<?> view;
  private DefaultView2d<?> wired;

  public GraphicsPane() {
    setOpaque(false);
    setName("graphics-pane");
    setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
    count.setName("graphics-count");
    add(count);
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    wire(view);
    refreshCount();
    repaint();
  }

  void wire(DefaultView2d<?> view) {
    if (view == null || view == wired) {
      return;
    }
    wired = view;
    view.addGraphicModelChangeListener(this::refreshCount);
  }

  public void refreshCount() {
    count.setText(Integer.toString(getGraphicList().size()));
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public List<Graphic> getGraphicList() {
    return view == null ? List.of() : view.getGraphicList();
  }

  public JLabel countLabel() {
    return count;
  }

  public String countText() {
    return count.getText();
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
        Shape shape = view.viewShape(graphic);
        if (shape == null) {
          continue;
        }
        g2.setPaint(graphic.getColorPaint() == null ? Color.YELLOW : graphic.getColorPaint());
        g2.draw(shape);
      }
    } finally {
      g2.dispose();
    }
  }
}
