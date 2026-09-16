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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.util.Toolbar;
import org.weasis.core.ui.util.WtoolBar;

/**
 * Measure/draw chrome. Headed buttons are SHORTCUTS.md D/A/Y/G/B (distance, angle, polyline, draw,
 * textbox). Selecting a tool sets the left mouse action to {@code measure}.
 */
public class MeasureToolBar extends WtoolBar implements Toolbar {

  public static final String NAME = "Measure";

  /** WP-5 headed row: D distance, A angle, Y polyline, G draw, B textbox. */
  public static final String[] BUTTONS = {"D", "A", "Y", "G", "B"};

  private String selected = MeasureTool.DISTANCE;
  private DefaultView2d<?> view;
  private final List<DefaultView2d<?>> targets = new ArrayList<>();

  public MeasureToolBar() {
    super(NAME, 11);
    for (String key : BUTTONS) {
      add(button(key));
    }
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    attach(view);
    apply(view);
  }

  public void attach(DefaultView2d<?> view) {
    if (view != null && !targets.contains(view)) {
      targets.add(view);
    }
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public Graphic newGraphic() {
    return MeasureTool.create(selected);
  }

  public void setSelected(String selected) {
    this.selected = selected == null || selected.isBlank() ? MeasureTool.DISTANCE : selected;
  }

  public String getSelected() {
    return selected;
  }

  public void apply(DefaultView2d<?> view) {
    if (view == null) {
      return;
    }
    view.setMeasureTool(selected);
    if ("G".equals(selected) || MeasureTool.RECTANGLE.equals(selected)) {
      view.getMouseActions().setLeft(MouseActions.DRAW);
    } else {
      view.getMouseActions().setLeft(MouseActions.MEASURE);
    }
  }

  void applyAll() {
    apply(view);
    for (DefaultView2d<?> target : targets) {
      if (target != view) {
        apply(target);
      }
    }
  }

  private JButton button(String key) {
    JButton button =
        new JButton(
            new AbstractAction(key) {
              @Override
              public void actionPerformed(ActionEvent e) {
                setSelected(key);
                applyAll();
              }
            });
    button.setName(key);
    button.setToolTipText(tip(key));
    return button;
  }

  static String tip(String key) {
    return switch (key) {
      case "A" -> "Angle";
      case "Y" -> "Polyline";
      case "G" -> "Draw";
      case "B" -> "Textbox";
      default -> "Distance";
    };
  }

  @Override
  public JComponent getComponent() {
    return this;
  }
}
