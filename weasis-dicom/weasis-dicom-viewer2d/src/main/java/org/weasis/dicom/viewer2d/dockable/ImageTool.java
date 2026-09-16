/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.dockable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.dicom.viewer2d.View2d;
import org.weasis.dicom.viewer2d.View2dContainer;

/** Image dock: window/level, zoom, pixel size, and paint-time horizontal flip (Alt+F). */
public class ImageTool extends PluginTool {

  public static final String NAME = "Image";

  private final JLabel summary = new JLabel(" ");
  private final JToggleButton window = new JToggleButton("Window");
  private final JToggleButton flip = new JToggleButton("Flip");
  private View2d view;

  public ImageTool() {
    super(NAME, 20);
    window.setName(ActionW.WINDOW.cmd());
    window.addActionListener(e -> applyWindow());
    flip.setName(ActionW.FLIP.cmd());
    flip.addActionListener(e -> applyFlip());
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    row.add(summary);
    row.add(window);
    row.add(flip);
    add(row, BorderLayout.NORTH);
  }

  public void bind(View2d view) {
    this.view = view;
    if (view != null) {
      window.setSelected(view.isWindowChrome());
      flip.setSelected(view.isFlip());
    }
    refresh();
  }

  public JToggleButton windowButton() {
    return window;
  }

  public JToggleButton flipButton() {
    return flip;
  }

  void applyWindow() {
    boolean on = window.isSelected();
    View2dContainer host = hostOf(view);
    if (host != null) {
      host.applyWindow(on);
    } else if (view != null) {
      view.applyWindowChrome(on);
    }
    refresh();
  }

  void applyFlip() {
    boolean on = flip.isSelected();
    View2dContainer host = hostOf(view);
    if (host != null) {
      host.applyFlip(on);
    } else if (view != null) {
      view.setFlip(on);
    }
    refresh();
  }

  static View2dContainer hostOf(View2d view) {
    if (view == null) {
      return null;
    }
    Object property = view.getClientProperty(View2dContainer.class);
    if (property instanceof View2dContainer container) {
      return container;
    }
    return hostFromParent(view);
  }

  static View2dContainer hostFromParent(Component c) {
    while (c != null) {
      if (c instanceof View2dContainer container) {
        return container;
      }
      c = c.getParent();
    }
    return null;
  }

  public View2d boundView() {
    return view;
  }

  public void refresh() {
    summary.setText(summaryText());
  }

  public String summaryText() {
    if (view == null) {
      return "";
    }
    int w = view.getSourceImage() == null ? 0 : view.getSourceImage().getWidth();
    int h = view.getSourceImage() == null ? 0 : view.getSourceImage().getHeight();
    String base =
        "W:"
            + (int) view.getWindow()
            + " L:"
            + (int) view.getLevel()
            + " zoom="
            + view.getZoom()
            + " "
            + w
            + "x"
            + h;
    if (view.hasCrosshair() && view.getPixelInfo() != null) {
      return base + " " + view.getPixelInfo().getText();
    }
    return base;
  }
}
