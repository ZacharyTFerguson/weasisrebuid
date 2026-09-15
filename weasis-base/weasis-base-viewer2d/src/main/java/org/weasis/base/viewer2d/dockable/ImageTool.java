/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d.dockable;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.core.ui.editor.image.DefaultView2d;

/** Image dock for non-DICOM 2D: zoom and pixel size. */
public class ImageTool extends PluginTool {

  public static final String NAME = "Image";

  private final JLabel summary = new JLabel(" ");
  private DefaultView2d<?> view;

  public ImageTool() {
    super(NAME, 20);
    add(summary, BorderLayout.NORTH);
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    refresh();
  }

  public void refresh() {
    summary.setText(summaryText());
  }

  public String summaryText() {
    if (view == null) {
      return "";
    }
    BufferedImage src = view.getSourceImage();
    int w = src == null ? 0 : src.getWidth();
    int h = src == null ? 0 : src.getHeight();
    return "zoom=" + view.getZoom() + " " + w + "x" + h;
  }
}
