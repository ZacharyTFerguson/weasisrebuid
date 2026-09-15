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
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.util.Toolbar;

/**
 * Measure/draw chrome. Buttons are SHORTCUTS.md tools (D distance, A angle, Y polyline, B textbox,
 * G draw rectangle). Selecting a tool sets the left mouse action to {@code measure}.
 */
public class MeasureToolBar implements Toolbar {

  public static final String NAME = "Measure";

  private final JToolBar bar = new JToolBar(NAME);
  private String selected = MeasureTool.DISTANCE;
  private int position = 40;
  private boolean enabled = true;
  private DefaultView2d<?> view;

  public MeasureToolBar() {
    for (String tool : MeasureTool.NAMES) {
      bar.add(button(tool));
    }
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    if (view != null) {
      apply(view);
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
    view.getMouseActions().setLeft(MouseActions.MEASURE);
  }

  private JButton button(String tool) {
    JButton button =
        new JButton(
            new AbstractAction(tool) {
              @Override
              public void actionPerformed(ActionEvent e) {
                setSelected(tool);
                apply(view);
              }
            });
    button.setName(tool);
    button.setToolTipText(tool);
    return button;
  }

  @Override
  public JComponent getComponent() {
    return bar;
  }

  @Override
  public String getComponentName() {
    return NAME;
  }

  @Override
  public Insertable.Type getType() {
    return Insertable.Type.TOOLBAR;
  }

  @Override
  public int getComponentPosition() {
    return position;
  }

  @Override
  public void setComponentPosition(int position) {
    this.position = position;
  }

  @Override
  public boolean isComponentEnabled() {
    return enabled;
  }

  @Override
  public void setComponentEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
