/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.editor.image.DefaultView2d;

/**
 * Prefs &gt; Monitors spatial calibration (MX-07). Independent from session Manual Calibration on
 * the canvas. Preferences OK stores pitch so reopen reloads the same value.
 */
public class ScreenPrefView extends ShellPrefPage {

  public static final String TITLE = "Monitors";
  public static final String PREF_PITCH = "weasis.monitor.pitch";
  public static final double DEFAULT_PITCH_MM = 0.25;

  private static final Monitor SHARED = new Monitor(null);

  private final WProperties prefs;
  private final Monitor monitor;
  private final JSpinner pitchSpinner;

  public ScreenPrefView() {
    this(SHARED, UICore.getInstance().getSystemPreferences());
  }

  public ScreenPrefView(Monitor monitor) {
    this(monitor, null);
  }

  public ScreenPrefView(Monitor monitor, WProperties prefs) {
    super(TITLE, 650);
    this.prefs = prefs;
    this.monitor = monitor == null ? SHARED : monitor;
    this.monitor.setPitchXmm(loadedPitch());
    pitchSpinner =
        new JSpinner(
            new SpinnerNumberModel(
                Double.valueOf(this.monitor.getPitchXmm()),
                Double.valueOf(0.01),
                Double.valueOf(5.0),
                Double.valueOf(0.01)));
    installUsEditor();
    JPanel form = new JPanel();
    form.add(new JLabel("Pixel pitch (mm)"));
    form.add(pitchSpinner);
    add(form);
  }

  public double pitchXmm() {
    commitSpinner();
    return parseEditorOrModel();
  }

  public void setPitchXmm(double pitchXmm) {
    pitchSpinner.setValue(pitchXmm);
  }

  public Monitor monitor() {
    return monitor;
  }

  public JSpinner pitchSpinner() {
    return pitchSpinner;
  }

  public void applyTo(DefaultView2d<?> view) {
    if (view != null) {
      view.setMonitorCalibrationMmPerPixel(pitchXmm());
    }
  }

  @Override
  public void closeAdditionalWindow() {
    double pitch = pitchXmm();
    monitor.setPitchXmm(pitch);
    store(pitch);
  }

  @Override
  public void resetToDefaultValues() {
    pitchSpinner.setValue(DEFAULT_PITCH_MM);
  }

  double loadedPitch() {
    if (prefs == null) {
      return monitor.getPitchXmm();
    }
    return prefs.getDoubleProperty(PREF_PITCH, systemPitch());
  }

  double systemPitch() {
    return parseDouble(System.getProperty(PREF_PITCH), monitor.getPitchXmm());
  }

  void store(double pitch) {
    if (prefs == null) {
      return;
    }
    SHARED.setPitchXmm(pitch);
    prefs.putDoubleProperty(PREF_PITCH, pitch);
    System.setProperty(PREF_PITCH, Double.toString(pitch));
  }

  void commitSpinner() {
    try {
      pitchSpinner.commitEdit();
    } catch (ParseException e) {
      // keep last valid model value; editor text still wins in parseEditorOrModel
    }
  }

  void installUsEditor() {
    JSpinner.NumberEditor editor = new JSpinner.NumberEditor(pitchSpinner, "0.00");
    DecimalFormat format = editor.getFormat();
    format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
    pitchSpinner.setEditor(editor);
    editor.getTextField().setFocusLostBehavior(JFormattedTextField.COMMIT);
    editor.getTextField().setLocale(Locale.US);
  }

  double parseEditorOrModel() {
    double model = ((Number) pitchSpinner.getValue()).doubleValue();
    return parsePitchText(editorText(), model);
  }

  String editorText() {
    if (pitchSpinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
      return editor.getTextField().getText();
    }
    return "";
  }

  static double parsePitchText(String raw, double documented) {
    if (raw == null || raw.isBlank()) {
      return documented;
    }
    return parseDoubleValue(raw.trim().replace(',', '.'), documented);
  }

  static void resetSharedPitch() {
    SHARED.setPitchXmm(DEFAULT_PITCH_MM);
  }

  static double parseDouble(String raw, double documented) {
    if (raw == null || raw.isBlank()) {
      return documented;
    }
    return parseDoubleValue(raw.trim(), documented);
  }

  static double parseDoubleValue(String raw, double documented) {
    try {
      return Double.parseDouble(raw);
    } catch (NumberFormatException e) {
      return documented;
    }
  }
}
