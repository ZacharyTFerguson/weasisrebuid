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
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.weasis.dicom.explorer.DicomSorter;
import org.weasis.dicom.explorer.ImportedInstance;

/** Explorer study combobox for the selected patient. */
public class StudyPane extends JPanel {

  private final PatientSelectionManager selection;
  private final SeriesPane seriesPane = new SeriesPane();
  private final DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
  private final JComboBox<String> combo = new JComboBox<>(comboModel);
  private boolean syncing;

  public StudyPane() {
    this(new PatientSelectionManager());
  }

  public StudyPane(PatientSelectionManager selection) {
    super(new BorderLayout());
    this.selection = selection == null ? new PatientSelectionManager() : selection;
    combo.setName("study-combo");
    add(combo, BorderLayout.NORTH);
    add(seriesPane, BorderLayout.CENTER);
    combo.addItemListener(
        e -> {
          if (!syncing && e.getStateChange() == ItemEvent.SELECTED) {
            this.selection.selectStudyIndex(combo.getSelectedIndex());
            refreshSeries();
          }
        });
    refresh();
  }

  public PatientSelectionManager getSelectionManager() {
    return selection;
  }

  public SeriesPane getSeriesPane() {
    return seriesPane;
  }

  public JComboBox<String> getCombo() {
    return combo;
  }

  public void refresh() {
    syncing = true;
    try {
      selection.refresh();
      comboModel.removeAllElements();
      for (String label : List.copyOf(selection.studyLabels())) {
        comboModel.addElement(label);
      }
      int idx = selection.studyUids().indexOf(selection.selectedStudyUid());
      if (idx >= 0) {
        combo.setSelectedIndex(idx);
      }
    } finally {
      syncing = false;
    }
    refreshSeries();
  }

  void refreshSeries() {
    List<ImportedInstance> instances = DicomSorter.sortSeries(selection.selectedInstances());
    List<String> labels = new ArrayList<>();
    for (ImportedInstance inst : instances) {
      labels.add(inst.modality() + " #" + inst.seriesNumber() + " " + inst.seriesDescription());
    }
    seriesPane.getSelectionModel().setItems(labels);
    seriesPane.showThumbnails(instances);
  }
}
