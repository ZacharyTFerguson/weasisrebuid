/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.print;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

/** Film-session options for File &gt; Print DICOM. */
public class DicomPrintOptionPane extends AbstractItemDialogPage {

  private final JSpinner copies = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
  private final JComboBox<DicomPrintOptions.FilmOrientation> orientation =
      new JComboBox<>(DicomPrintOptions.FilmOrientation.values());
  private final JComboBox<DicomPrintOptions.FilmSize> filmSize =
      new JComboBox<>(DicomPrintOptions.FilmSize.values());

  public DicomPrintOptionPane() {
    super("DICOM Print", 0);
    JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
    form.add(new JLabel("Copies"));
    form.add(copies);
    form.add(new JLabel("Orientation"));
    form.add(orientation);
    form.add(new JLabel("Film size"));
    form.add(filmSize);
    add(form, java.awt.BorderLayout.NORTH);
    setOptions(new DicomPrintOptions());
  }

  public void setOptions(DicomPrintOptions options) {
    DicomPrintOptions src = options == null ? new DicomPrintOptions() : options;
    copies.setValue(src.copies());
    orientation.setSelectedItem(src.orientation());
    filmSize.setSelectedItem(src.filmSize());
  }

  public DicomPrintOptions getOptions() {
    DicomPrintOptions options = new DicomPrintOptions();
    options.setCopies((Integer) copies.getValue());
    options.setOrientation((DicomPrintOptions.FilmOrientation) orientation.getSelectedItem());
    options.setFilmSize((DicomPrintOptions.FilmSize) filmSize.getSelectedItem());
    return options;
  }

  @Override
  public void closeAdditionalWindow() {
    // no-op
  }

  @Override
  public void resetToDefaultValues() {
    setOptions(new DicomPrintOptions());
  }
}
