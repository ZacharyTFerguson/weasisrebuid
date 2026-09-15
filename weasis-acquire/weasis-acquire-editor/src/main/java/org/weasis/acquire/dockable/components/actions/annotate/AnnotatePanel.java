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

import java.awt.BorderLayout;
import java.awt.geom.Point2D;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.Graphic;

/** Dicomizer annotate chrome: WP-5 measure/draw tools plus {@link AnnotationOptionsPanel}. */
public class AnnotatePanel extends JPanel {

  private final AnnotationOptionsPanel options = new AnnotationOptionsPanel();
  private String tool = MeasureTool.DISTANCE;

  public AnnotatePanel() {
    super(new BorderLayout());
    JPanel tools = new JPanel();
    for (String name : MeasureTool.NAMES) {
      JButton button = new JButton(name);
      button.setName(name);
      button.addActionListener(e -> setTool(name));
      tools.add(button);
    }
    add(tools, BorderLayout.NORTH);
    add(options, BorderLayout.CENTER);
  }

  public AnnotationOptionsPanel options() {
    return options;
  }

  public void setTool(String tool) {
    Graphic probe = MeasureTool.create(tool);
    this.tool = probe == null ? MeasureTool.DISTANCE : tool;
  }

  public String getTool() {
    return tool;
  }

  public Graphic createGraphic(Point2D.Double... pts) {
    Graphic graphic = MeasureTool.create(tool);
    if (graphic instanceof AbstractDragGraphic drag && pts != null) {
      for (int i = 0; i < pts.length; i++) {
        drag.setHandlePoint(i, pts[i]);
      }
    }
    options.applyTo(graphic);
    if (graphic != null) {
      graphic.buildShape();
    }
    return graphic;
  }
}
