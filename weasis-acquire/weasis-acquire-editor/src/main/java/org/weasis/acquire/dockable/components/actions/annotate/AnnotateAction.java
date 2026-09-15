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

import java.awt.geom.Point2D;
import org.weasis.core.ui.model.AbstractGraphicModel;
import org.weasis.core.ui.model.GraphicModel;
import org.weasis.core.ui.model.graphic.Graphic;

/** Adds WP-5 graphics from {@link AnnotatePanel} onto the photo-editor graphic model. */
public class AnnotateAction {

  private final AnnotatePanel panel = new AnnotatePanel();
  private GraphicModel model = new AbstractGraphicModel();

  public AnnotatePanel panel() {
    return panel;
  }

  public AnnotationOptionsPanel options() {
    return panel.options();
  }

  public void setModel(GraphicModel model) {
    this.model = model == null ? new AbstractGraphicModel() : model;
  }

  public GraphicModel model() {
    return model;
  }

  public void setTool(String tool) {
    panel.setTool(tool);
  }

  public Graphic add(Point2D.Double... pts) {
    Graphic graphic = panel.createGraphic(pts);
    if (graphic != null) {
      model.addGraphic(graphic);
    }
    return graphic;
  }
}
