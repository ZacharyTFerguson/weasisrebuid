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
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

/** Overlay pane that paints the bound view's graphic model. */
public class GraphicsPane extends JPanel {

  public static final String STATE = "sel-state";

  private final JLabel count = new JLabel("0");
  private final JButton sample = new JButton("Sample");
  private final JButton select = new JButton("Select");
  private final JButton delete = new JButton("Delete");
  private final JLabel state = new JLabel("none");
  private DefaultView2d<?> view;
  private DefaultView2d<?> wired;

  public GraphicsPane() {
    setOpaque(false);
    setName("graphics-pane");
    setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
    count.setName("graphics-count");
    add(count);
    bindSelChrome();
    add(sample);
    add(select);
    add(delete);
    add(state);
  }

  void bindSelChrome() {
    sample.setName("sel-sample");
    sample.addActionListener(e -> applySample());
    select.setName("select-graphic");
    select.addActionListener(e -> applySelect());
    delete.setName("delete-graphic");
    delete.addActionListener(e -> applyDelete());
    state.setName(STATE);
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    wire(view);
    showState();
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

  public JButton sampleButton() {
    return sample;
  }

  public JButton selectButton() {
    return select;
  }

  public JButton deleteButton() {
    return delete;
  }

  public JLabel stateLabel() {
    return state;
  }

  public String stateText() {
    return state.getText();
  }

  void applySample() {
    if (view == null) {
      return;
    }
    view.addGraphic(sampleLine());
    showState();
  }

  void applySelect() {
    if (view == null) {
      return;
    }
    view.selectAllGraphics();
    showState();
  }

  void applyDelete() {
    if (view == null) {
      return;
    }
    boolean had = !view.getSelectedGraphics().isEmpty();
    view.deleteSelectedGraphics();
    state.setText(had ? "deleted" : token());
    refreshCount();
  }

  void showState() {
    state.setText(token());
    refreshCount();
  }

  String token() {
    if (view == null || view.getGraphicList().isEmpty()) {
      return "none";
    }
    if (view.getSelectedGraphics().isEmpty()) {
      return "none";
    }
    return "selected";
  }

  static LineGraphic sampleLine() {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(10, 0));
    line.buildShape();
    return line;
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
