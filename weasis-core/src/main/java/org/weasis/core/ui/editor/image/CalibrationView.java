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
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.api.image.util.Unit;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

/**
 * Session Manual Calibration (MX-07). A known real-world length on a distance line sets mm/pixel on
 * the view without touching Prefs &gt; Monitors spatial calibration.
 */
public class CalibrationView extends JPanel {

  private final DefaultView2d<?> view;
  private LineGraphic line;
  private double knownLength = 10.0;
  private Unit unit = Unit.MILLIMETER;

  public CalibrationView(DefaultView2d<?> view) {
    super(new BorderLayout());
    this.view = view;
    add(new JLabel("Manual Calibration"), BorderLayout.NORTH);
  }

  public DefaultView2d<?> getView() {
    return view;
  }

  public void setLine(LineGraphic line) {
    this.line = line;
  }

  public LineGraphic getLine() {
    return line;
  }

  public void setKnownLength(double knownLength, Unit unit) {
    this.knownLength = knownLength;
    this.unit = unit == null ? Unit.MILLIMETER : unit;
  }

  public double getKnownLength() {
    return knownLength;
  }

  public Unit getUnit() {
    return unit;
  }

  public double apply() {
    LineGraphic target = line;
    if (target == null && view != null) {
      for (Graphic graphic : view.getSelectedGraphics()) {
        if (graphic instanceof LineGraphic distance) {
          target = distance;
          break;
        }
      }
    }
    if (view == null || target == null || target.getLength() <= 0 || unit == Unit.PIXEL) {
      return view == null ? 0.0 : view.getSessionManualCalibrationMmPerPixel();
    }
    double mmPerPixel = (knownLength * unit.getConvMm()) / target.getLength();
    view.setSessionManualCalibrationMmPerPixel(mmPerPixel);
    return mmPerPixel;
  }
}
