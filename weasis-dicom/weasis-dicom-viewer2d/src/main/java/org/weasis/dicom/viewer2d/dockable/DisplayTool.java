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
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.model.layer.AbstractInfoLayer;
import org.weasis.core.ui.model.layer.AbstractInfoLayer.Visibility;
import org.weasis.core.ui.model.layer.LayerItem;
import org.weasis.core.ui.model.layer.LayerType;

/** Display dock: annotation visibility FULL / MINIMAL / HIDDEN and layer items. */
public class DisplayTool extends PluginTool {

  public static final String NAME = "Display";

  private final JComboBox<Visibility> visibility = new JComboBox<>(Visibility.values());
  private final JLabel value = new JLabel(Visibility.FULL.name());
  private JButton fullButton;
  private JButton minimalButton;
  private JButton hiddenButton;
  private AbstractInfoLayer layer;
  private DefaultView2d<?> view;

  public DisplayTool() {
    super(NAME, 10);
    setName("display");
    nameChrome();
    add(chromeBar(), BorderLayout.NORTH);
  }

  void nameChrome() {
    visibility.setName("display-visibility");
    value.setName("display-visibility-value");
    visibility.addActionListener(e -> apply());
  }

  JPanel chromeBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    bar.setName("display-chrome");
    bar.add(visibility);
    bar.add(value);
    fullButton = stateButton("FULL", "display-full", Visibility.FULL);
    minimalButton = stateButton("MINIMAL", "display-minimal", Visibility.MINIMAL);
    hiddenButton = stateButton("HIDDEN", "display-hidden", Visibility.HIDDEN);
    bar.add(fullButton);
    bar.add(minimalButton);
    bar.add(hiddenButton);
    return bar;
  }

  JButton stateButton(String title, String name, Visibility state) {
    JButton button = new JButton(title);
    button.setName(name);
    button.addActionListener(e -> setVisibility(state));
    return button;
  }

  public void bind(AbstractInfoLayer layer) {
    this.layer = layer;
    if (layer != null) {
      visibility.setSelectedItem(layer.getVisibility());
    }
    refreshValue();
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    if (view != null) {
      bind(view.getInfoLayer());
    }
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public AbstractInfoLayer boundLayer() {
    return layer;
  }

  public List<LayerItem> layerItems() {
    if (view != null) {
      return view.displayLayers();
    }
    return List.of();
  }

  public void setLayerVisible(LayerType type, boolean visible) {
    if (view != null) {
      view.setLayerVisible(type, visible);
    }
  }

  public void setVisibility(Visibility state) {
    Visibility next = state == null ? Visibility.FULL : state;
    if (layer != null) {
      layer.setVisibility(next);
    }
    visibility.setSelectedItem(next);
    refreshValue();
  }

  public void apply() {
    if (visibility.getSelectedItem() instanceof Visibility selected) {
      if (layer != null) {
        layer.setVisibility(selected);
      }
      refreshValue();
    }
  }

  public void cycle() {
    if (layer != null) {
      layer.cycle();
      visibility.setSelectedItem(layer.getVisibility());
    }
    refreshValue();
  }

  void refreshValue() {
    Object selected = visibility.getSelectedItem();
    value.setText(selected == null ? Visibility.FULL.name() : selected.toString());
  }

  public JComboBox<Visibility> visibilityCombo() {
    return visibility;
  }

  public JLabel visibilityValueLabel() {
    return value;
  }

  public String visibilityValueText() {
    return value.getText();
  }

  public JButton fullButton() {
    return fullButton;
  }

  public JButton minimalButton() {
    return minimalButton;
  }

  public JButton hiddenButton() {
    return hiddenButton;
  }
}
