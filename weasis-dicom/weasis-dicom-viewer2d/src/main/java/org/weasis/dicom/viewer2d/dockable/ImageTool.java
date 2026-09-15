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
import javax.swing.JLabel;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.dicom.viewer2d.View2d;

/** Image dock: window/level, zoom, and pixel size for the selected 2D view. */
public class ImageTool extends PluginTool {

  public static final String NAME = "Image";

  private final JLabel summary = new JLabel(" ");
  private View2d view;

  public ImageTool() {
    super(NAME, 20);
    add(summary, BorderLayout.NORTH);
  }

  public void bind(View2d view) {
    this.view = view;
    refresh();
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
    return "W:"
        + (int) view.getWindow()
        + " L:"
        + (int) view.getLevel()
        + " zoom="
        + view.getZoom()
        + " "
        + w
        + "x"
        + h;
  }
}
