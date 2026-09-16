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
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.util.WtoolBar;

/**
 * 2D viewer mouse-action chrome. Buttons are the Gogo {@code dcmview2d:mouseLeftAction} tokens from
 * COMMANDS.md.
 */
public class ViewerToolBar extends WtoolBar {

  public static final String NAME = "Viewer";

  public static final String[] ACTIONS = {
    ActionW.SCROLL_SERIES.cmd(),
    ActionW.WINLEVEL.cmd(),
    ActionW.ZOOM.cmd(),
    ActionW.PAN.cmd(),
    ActionW.ROTATION.cmd(),
    ActionW.CROSSHAIR.cmd(),
    ActionW.MEASURE.cmd(),
    ActionW.DRAW.cmd(),
    ActionW.CONTEXTMENU.cmd(),
    ActionW.NONE.cmd()
  };

  private String selected = ActionW.WINLEVEL.cmd();
  private DefaultView2d<?> view;

  public ViewerToolBar() {
    super(NAME, 10);
    for (String action : ACTIONS) {
      add(button(action));
    }
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    if (view != null) {
      apply(view);
    }
  }

  public String getSelected() {
    return selected;
  }

  public void setSelected(String selected) {
    this.selected = selected == null || selected.isBlank() ? ActionW.WINLEVEL.cmd() : selected;
  }

  public void apply(DefaultView2d<?> view) {
    if (view == null) {
      return;
    }
    view.getMouseActions().setLeft(selected);
    bindMeasureTool(view);
  }

  public static void bindMeasureTool(DefaultView2d<?> view) {
    String left = MouseActions.normalize(view.getMouseActions().getLeft());
    if (MouseActions.MEASURE.equals(left)) {
      if (!MeasureTool.measureFamily(view.getMeasureTool())) {
        view.setMeasureTool(MeasureTool.DISTANCE);
      }
    } else if (MouseActions.DRAW.equals(left)) {
      if (!MeasureTool.drawFamily(view.getMeasureTool())) {
        view.setMeasureTool(MeasureTool.RECTANGLE);
      }
    }
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  private JButton button(String action) {
    JButton button =
        new JButton(
            new AbstractAction(action) {
              @Override
              public void actionPerformed(ActionEvent e) {
                setSelected(action);
                apply(view);
              }
            });
    button.setToolTipText(action);
    button.setName(action);
    return button;
  }
}
