/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.weasis.core.ui.editor.image.DefaultView2d;

/**
 * Prefs &gt; Monitors spatial calibration (MX-07). Independent from session Manual Calibration on the
 * canvas.
 */
public class ScreenPrefView extends ShellPrefPage {

  public static final String TITLE = "Monitors";
  public static final double DEFAULT_PITCH_MM = 0.25;

  private static final Monitor SHARED = new Monitor(null);

  private final Monitor monitor;
  private final JSpinner pitchSpinner;

  public ScreenPrefView() {
    this(SHARED);
  }

  public ScreenPrefView(Monitor monitor) {
    super(TITLE, 650);
    this.monitor = monitor == null ? SHARED : monitor;
    pitchSpinner =
        new JSpinner(
            new SpinnerNumberModel(
                Double.valueOf(this.monitor.getPitchXmm()),
                Double.valueOf(0.01),
                Double.valueOf(5.0),
                Double.valueOf(0.01)));
    JPanel form = new JPanel();
    form.add(new JLabel("Pixel pitch (mm)"));
    form.add(pitchSpinner);
    add(form);
  }

  public double pitchXmm() {
    return ((Number) pitchSpinner.getValue()).doubleValue();
  }

  public void setPitchXmm(double pitchXmm) {
    pitchSpinner.setValue(pitchXmm);
  }

  public Monitor monitor() {
    return monitor;
  }

  public void applyTo(DefaultView2d<?> view) {
    if (view != null) {
      view.setMonitorCalibrationMmPerPixel(pitchXmm());
    }
  }

  @Override
  public void closeAdditionalWindow() {
    monitor.setPitchXmm(pitchXmm());
  }

  @Override
  public void resetToDefaultValues() {
    pitchSpinner.setValue(DEFAULT_PITCH_MM);
  }
}
