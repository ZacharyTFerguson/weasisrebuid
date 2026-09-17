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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.api.gui.util.JSliderW;
import org.weasis.core.ui.docking.PluginTool;

/**
 * 2D lens / magnifier (WP-6). Factor multiplies the view's resolved scale; default 2× around the
 * pointer. Magnification is paint-time (Graphics2D affine), not {@code
 * AffineTransformOp.process()}.
 */
public class ZoomWin extends PluginTool {

  public static final String NAME = "Lens";
  public static final double DEFAULT_FACTOR = 2.0;

  private final JSliderW factorSlider = new JSliderW(100, 800, 200);
  private final JLabel factorValue = new JLabel("2x");
  private final JPanel preview =
      new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          paintLens(g);
        }
      };
  private double factor = DEFAULT_FACTOR;
  private double originX;
  private double originY;
  private DefaultView2d<?> view;
  private boolean syncing;

  public ZoomWin() {
    super(NAME, 15);
    setName("lens");
    setPreferredSize(new Dimension(200, 260));
    nameChrome();
    add(factorRow(), BorderLayout.NORTH);
    add(preview, BorderLayout.CENTER);
    factorSlider.addChangeListener(e -> applyFactor());
  }

  void nameChrome() {
    factorSlider.setName("lens-factor");
    factorValue.setName("lens-factor-value");
    preview.setName("lens-panel");
  }

  JPanel factorRow() {
    JPanel row = new JPanel(new BorderLayout());
    row.setName("lens-factor-row");
    row.add(factorValue, BorderLayout.EAST);
    row.add(factorSlider, BorderLayout.CENTER);
    return row;
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    refresh();
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public JSliderW getFactorSlider() {
    return factorSlider;
  }

  public JLabel factorValueLabel() {
    return factorValue;
  }

  public String factorValueText() {
    return factorValue.getText();
  }

  public JPanel previewPanel() {
    return preview;
  }

  public double getFactor() {
    return factor;
  }

  public void setFactor(double factor) {
    this.factor = factor <= 0 ? DEFAULT_FACTOR : factor;
    syncChrome();
  }

  public void setOrigin(double x, double y) {
    this.originX = x;
    this.originY = y;
    preview.repaint();
  }

  public double originX() {
    return originX;
  }

  public double originY() {
    return originY;
  }

  public double magnifiedScale(DefaultView2d<?> view, int viewW, int viewH) {
    double base = view == null ? 1.0 : view.resolvedScale(viewW, viewH);
    return base * factor;
  }

  public void refresh() {
    syncChrome();
    preview.repaint();
  }

  void applyFactor() {
    if (syncing) {
      return;
    }
    factor = factorSlider.getValue() / 100.0;
    syncValueLabel();
    preview.repaint();
  }

  void syncChrome() {
    syncing = true;
    try {
      int percent = (int) Math.round(factor * 100.0);
      factorSlider.setValue(Math.max(100, Math.min(800, percent)));
      syncValueLabel();
    } finally {
      syncing = false;
    }
  }

  void syncValueLabel() {
    factorValue.setText(strippedFactor() + "x");
  }

  int strippedFactor() {
    return (int) Math.round(factor);
  }

  void paintLens(Graphics g) {
    if (!(g instanceof Graphics2D g2) || view == null) {
      return;
    }
    BufferedImage src = view.getSourceImage();
    if (src == null) {
      return;
    }
    int w = Math.max(1, preview.getWidth());
    int h = Math.max(1, preview.getHeight());
    double mag = magnifiedScale(view, w, h);
    AffineTransform tx = AffineTransform.getScaleInstance(mag, mag);
    tx.translate(-originX, -originY);
    g2.drawImage(src, tx, this);
  }
}
