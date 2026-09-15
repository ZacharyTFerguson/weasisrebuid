/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions.annotate;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.weasis.core.ui.model.graphic.Graphic;

/** Color, line thickness, fill, and label visibility applied to WP-5 graphics. */
public class AnnotationOptionsPanel extends JPanel {

  private Color color = Color.YELLOW;
  private float lineThickness = 1.0f;
  private boolean filled;
  private boolean labelVisible = true;
  private final JSpinner thickness = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 16.0, 0.5));
  private final JCheckBox fill = new JCheckBox("Fill");
  private final JCheckBox labels = new JCheckBox("Labels");

  public AnnotationOptionsPanel() {
    super(new GridLayout(0, 2, 4, 4));
    thickness.setName("lineThickness");
    fill.setName("filled");
    labels.setName("labelVisible");
    labels.setSelected(true);
    add(new JLabel("Thickness"));
    add(thickness);
    add(fill);
    add(labels);
    thickness.addChangeListener(e -> lineThickness = ((Number) thickness.getValue()).floatValue());
    fill.addActionListener(e -> filled = fill.isSelected());
    labels.addActionListener(e -> labelVisible = labels.isSelected());
  }

  public void setColor(Color color) {
    this.color = color == null ? Color.YELLOW : color;
  }

  public Color getColor() {
    return color;
  }

  public void setLineThickness(float lineThickness) {
    this.lineThickness = lineThickness <= 0 ? 1.0f : lineThickness;
    thickness.setValue((double) this.lineThickness);
  }

  public float getLineThickness() {
    return lineThickness;
  }

  public void setFilled(boolean filled) {
    this.filled = filled;
    fill.setSelected(filled);
  }

  public boolean isFilled() {
    return filled;
  }

  public void setLabelVisible(boolean labelVisible) {
    this.labelVisible = labelVisible;
    labels.setSelected(labelVisible);
  }

  public boolean isLabelVisible() {
    return labelVisible;
  }

  public void applyTo(Graphic graphic) {
    if (graphic == null) {
      return;
    }
    graphic.setColorPaint(color);
    graphic.setLineThickness(lineThickness);
    graphic.setFilled(filled);
    graphic.setLabelVisible(labelVisible);
  }
}
