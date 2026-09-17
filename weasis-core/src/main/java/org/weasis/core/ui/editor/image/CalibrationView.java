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

import java.awt.geom.Point2D;
import javax.swing.JButton;
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

  public static final String STATE = "cal-state";
  public static final String NONE = "none";
  public static final String LINE = "line";
  public static final String SESSION = "session";

  private final DefaultView2d<?> view;
  private final JButton sample = new JButton(LINE);
  private final JButton applyBtn = new JButton(SESSION);
  private final JLabel state = new JLabel(NONE);
  private LineGraphic line;
  private double knownLength = 10.0;
  private Unit unit = Unit.MILLIMETER;

  public CalibrationView() {
    this(new DefaultView2d<>());
  }

  public CalibrationView(DefaultView2d<?> view) {
    this.view = view;
    setName("cal-view");
    bindCalChrome();
    add(sample);
    add(applyBtn);
    add(state);
  }

  void bindCalChrome() {
    sample.setName("cal-line");
    applyBtn.setName("cal-apply");
    state.setName(STATE);
    sample.addActionListener(e -> applyLine());
    applyBtn.addActionListener(e -> applySession());
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

  public JButton lineButton() {
    return sample;
  }

  public JButton applyButton() {
    return applyBtn;
  }

  public JLabel stateLabel() {
    return state;
  }

  public String stateText() {
    return state.getText();
  }

  void applyLine() {
    LineGraphic distance = sampleLine();
    setLine(distance);
    setKnownLength(20.0, Unit.MILLIMETER);
    state.setText(LINE);
  }

  void applySession() {
    apply();
    state.setText(SESSION);
  }

  static LineGraphic sampleLine() {
    LineGraphic distance = new LineGraphic();
    distance.setHandlePoint(0, new Point2D.Double(0, 0));
    distance.setHandlePoint(1, new Point2D.Double(10, 0));
    return distance;
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
