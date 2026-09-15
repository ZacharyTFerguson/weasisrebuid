/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions.calibrate;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.graphics.CalibrationGraphic;
import org.weasis.acquire.utils.GraphicHelper;
import org.weasis.core.api.image.util.Unit;

/**
 * Known real-world length on a {@link CalibrationGraphic} writes mm/pixel onto {@link
 * AcquireImageValues} (dicomizer photos; independent of MX-07 monitor pitch).
 */
public class CalibrationPanel extends JPanel {

  private CalibrationGraphic graphic;
  private double knownLength = 10.0;
  private Unit unit = Unit.MILLIMETER;

  public CalibrationPanel() {
    super(new BorderLayout());
    add(new JLabel("Calibration"), BorderLayout.NORTH);
  }

  public void setGraphic(CalibrationGraphic graphic) {
    this.graphic = graphic;
  }

  public CalibrationGraphic getGraphic() {
    return graphic;
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

  public double apply(AcquireImageValues values) {
    return apply(values, graphic);
  }

  public double apply(AcquireImageValues values, CalibrationGraphic line) {
    if (values == null) {
      return 0.0;
    }
    double pixels = line == null ? 0.0 : line.pixelLength();
    double mmPerPixel = GraphicHelper.mmPerPixel(knownLength, unit, pixels);
    values.setCalibrationMmPerPixel(mmPerPixel);
    return mmPerPixel;
  }
}
