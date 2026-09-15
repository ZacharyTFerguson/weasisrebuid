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
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.weasis.dicom.explorer.DicomModel;

/** Explorer patient list. */
public class PatientPane extends JPanel {

  private final DicomModel model;
  private final PatientSelectionManager selection;
  private final DefaultListModel<String> listModel = new DefaultListModel<>();
  private final JList<String> list = new JList<>(listModel);

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
    add(new JScrollPane(list), BorderLayout.CENTER);
    list.addListSelectionListener(
        e -> {
          if (!e.getValueIsAdjusting()) {
            this.selection.selectPatientIndex(list.getSelectedIndex());
          }
        });
    refresh();
  }

  public PatientSelectionManager getSelectionManager() {
    return selection;
  }

  public JList<String> getList() {
    return list;
  }

  public void refresh() {
    selection.refresh();
    listModel.clear();
    for (String label : selection.patientLabels()) {
      listModel.addElement(label);
    }
    int idx = selection.patientKeys().indexOf(selection.selectedPatientKey());
    if (idx >= 0) {
      list.setSelectedIndex(idx);
    }
  }
}
