/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image.dockable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.api.gui.util.JSliderW;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.Panner;

/**
 * Compact 2D tool dock: zoom / rotation / series sliders plus the image panner (WP-6 Mini Tool).
 */
public class MiniTool extends PluginTool {

  public static final String NAME = "Mini Tool";

  private final JSliderW zoom = new JSliderW(1, 400, 100);
  private final JSliderW rotation = new JSliderW(0, 359, 0);
  private final JSliderW series = new JSliderW(0, 0, 0);
  private final Panner panner = new Panner();
  private final JLabel zoomValue = new JLabel("100%");
  private final JLabel rotationValue = new JLabel("0°");
  private DefaultView2d<?> view;
  private boolean syncing;

  public MiniTool() {
    super(NAME, 5);
    setName("mini-tool");
    setPreferredSize(new Dimension(160, 280));
    nameChrome();
    JPanel sliders = new JPanel();
    sliders.setName("mini-sliders");
    sliders.setLayout(new BoxLayout(sliders, BoxLayout.Y_AXIS));
    sliders.add(labeled("Zoom", zoom, zoomValue));
    sliders.add(labeled("Rotation", rotation, rotationValue));
    sliders.add(labeled("Series", series, null));
    add(sliders, BorderLayout.CENTER);
    add(panner, BorderLayout.SOUTH);
    zoom.addChangeListener(e -> applyZoom());
    rotation.addChangeListener(e -> applyRotation());
    series.addChangeListener(e -> applySeries());
  }

  void nameChrome() {
    zoom.setName("mini-zoom");
    rotation.setName("mini-rotation");
    series.setName("mini-series");
    panner.setName("mini-panner");
    zoomValue.setName("mini-zoom-value");
    rotationValue.setName("mini-rotation-value");
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    panner.bind(view);
    refresh();
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public Panner getPanner() {
    return panner;
  }

  public JSliderW getZoomSlider() {
    return zoom;
  }

  public JSliderW getRotationSlider() {
    return rotation;
  }

  public JSliderW getSeriesSlider() {
    return series;
  }

  public JLabel zoomValueLabel() {
    return zoomValue;
  }

  public String zoomValueText() {
    return zoomValue.getText();
  }

  public JLabel rotationValueLabel() {
    return rotationValue;
  }

  public void refresh() {
    if (view == null) {
      return;
    }
    syncing = true;
    try {
      double z = view.getZoom();
      if (z > 0) {
        zoom.setValue((int) Math.max(1, Math.min(400, Math.round(z * 100.0))));
      }
      int rot = (int) Math.round(view.getRotation()) % 360;
      if (rot < 0) {
        rot += 360;
      }
      rotation.setValue(rot);
      series.setMaximum(Math.max(0, view.getFrameCount() - 1));
      series.setValue(Math.min(series.getMaximum(), view.getFrameIndex()));
      syncValueLabels();
    } finally {
      syncing = false;
    }
  }

  private void applyZoom() {
    if (syncing || view == null) {
      return;
    }
    view.setZoom(zoom.getValue() / 100.0);
    syncValueLabels();
  }

  private void applyRotation() {
    if (syncing || view == null) {
      return;
    }
    view.setRotation(rotation.getValue());
    syncValueLabels();
  }

  void syncValueLabels() {
    zoomValue.setText(zoom.getValue() + "%");
    rotationValue.setText(rotation.getValue() + "°");
  }

  private void applySeries() {
    if (syncing || view == null) {
      return;
    }
    view.setFrameIndex(series.getValue());
  }

  static JPanel labeled(String title, JSliderW slider, JLabel value) {
    JPanel row = new JPanel(new BorderLayout());
    JPanel header = new JPanel(new BorderLayout());
    header.add(new JLabel(title), BorderLayout.WEST);
    if (value != null) {
      header.add(value, BorderLayout.EAST);
    }
    row.add(header, BorderLayout.NORTH);
    row.add(slider, BorderLayout.CENTER);
    return row;
  }
}
