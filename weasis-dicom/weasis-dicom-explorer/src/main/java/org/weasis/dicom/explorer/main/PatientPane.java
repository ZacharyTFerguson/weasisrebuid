/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.weasis.dicom.explorer.DicomModel;

/** Explorer patient combobox (Weasis: searchable patient selector). */
public class PatientPane extends JPanel {

  private final DicomModel model;
  private final PatientSelectionManager selection;
  private final DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
  private final JComboBox<String> combo = new JComboBox<>(comboModel);
  private Runnable onSelect = () -> {};
  private boolean syncing;

  public PatientPane() {
    this(new DicomModel());
  }

  public PatientPane(DicomModel model) {
    this(model, new PatientSelectionManager());
  }

  public PatientPane(DicomModel model, PatientSelectionManager selection) {
    super(new BorderLayout());
    this.model = model == null ? new DicomModel() : model;
    this.selection = selection == null ? new PatientSelectionManager() : selection;
    this.selection.bind(this.model);
    combo.setName("patient-combo");
    combo.setEditable(true);
    combo.setToolTipText("Search patient…");
    add(combo, BorderLayout.CENTER);
    combo.addItemListener(
        e -> {
          if (!syncing && e.getStateChange() == ItemEvent.SELECTED) {
            this.selection.selectPatientIndex(combo.getSelectedIndex());
            onSelect.run();
          }
        });
    refresh();
  }

  public void setOnSelect(Runnable onSelect) {
    this.onSelect = onSelect == null ? () -> {} : onSelect;
  }

  public PatientSelectionManager getSelectionManager() {
    return selection;
  }

  public JComboBox<String> getCombo() {
    return combo;
  }

  public void refresh() {
    syncing = true;
    try {
      selection.refresh();
      comboModel.removeAllElements();
      for (String label : List.copyOf(selection.patientLabels())) {
        comboModel.addElement(label);
      }
      int idx = selection.patientKeys().indexOf(selection.selectedPatientKey());
      if (idx >= 0) {
        combo.setSelectedIndex(idx);
      }
    } finally {
      syncing = false;
    }
  }
}
