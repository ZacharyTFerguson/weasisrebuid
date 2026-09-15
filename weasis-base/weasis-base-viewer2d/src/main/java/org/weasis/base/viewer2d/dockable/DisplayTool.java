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
import javax.swing.JComboBox;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.core.ui.model.layer.AbstractInfoLayer;
import org.weasis.core.ui.model.layer.AbstractInfoLayer.Visibility;

/** Display dock for non-DICOM 2D: annotation visibility FULL / MINIMAL / HIDDEN. */
public class DisplayTool extends PluginTool {

  public static final String NAME = "Display";

  private final JComboBox<Visibility> visibility = new JComboBox<>(Visibility.values());
  private AbstractInfoLayer layer;

  public DisplayTool() {
    super(NAME, 10);
    add(visibility, BorderLayout.NORTH);
    visibility.addActionListener(e -> apply());
  }

  public void bind(AbstractInfoLayer layer) {
    this.layer = layer;
    if (layer != null) {
      visibility.setSelectedItem(layer.getVisibility());
    }
  }

  public void apply() {
    if (layer != null && visibility.getSelectedItem() instanceof Visibility selected) {
      layer.setVisibility(selected);
    }
  }

  public void cycle() {
    if (layer != null) {
      layer.cycle();
      visibility.setSelectedItem(layer.getVisibility());
    }
  }
}
