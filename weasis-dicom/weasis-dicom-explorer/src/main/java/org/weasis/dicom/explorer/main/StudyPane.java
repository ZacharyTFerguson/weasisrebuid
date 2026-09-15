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
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.weasis.dicom.explorer.DicomSorter;
import org.weasis.dicom.explorer.ImportedInstance;

/** Explorer study list for the selected patient. */
public class StudyPane extends JPanel {

  private final PatientSelectionManager selection;
  private final SeriesPane seriesPane = new SeriesPane();
  private final DefaultListModel<String> listModel = new DefaultListModel<>();
  private final JList<String> list = new JList<>(listModel);

  public StudyPane() {
    this(new PatientSelectionManager());
  }

  public StudyPane(PatientSelectionManager selection) {
    super(new BorderLayout());
    this.selection = selection == null ? new PatientSelectionManager() : selection;
    add(new JScrollPane(list), BorderLayout.CENTER);
    add(seriesPane, BorderLayout.SOUTH);
    list.addListSelectionListener(
        e -> {
          if (!e.getValueIsAdjusting()) {
            this.selection.selectStudyIndex(list.getSelectedIndex());
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

  public JList<String> getList() {
    return list;
  }

  public void refresh() {
    selection.refresh();
    listModel.clear();
    for (String label : selection.studyLabels()) {
      listModel.addElement(label);
    }
    int idx = selection.studyUids().indexOf(selection.selectedStudyUid());
    if (idx >= 0) {
      list.setSelectedIndex(idx);
    }
    refreshSeries();
  }

  void refreshSeries() {
    List<String> labels = new ArrayList<>();
    for (ImportedInstance inst : DicomSorter.sortSeries(selection.selectedInstances())) {
      labels.add(inst.modality() + " #" + inst.seriesNumber() + " " + inst.seriesDescription());
    }
    seriesPane.getSelectionModel().setItems(labels);
  }
}
