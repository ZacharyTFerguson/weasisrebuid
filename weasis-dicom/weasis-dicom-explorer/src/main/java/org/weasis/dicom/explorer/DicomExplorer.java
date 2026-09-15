/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.dicom.explorer.main.PatientPane;
import org.weasis.dicom.explorer.main.SeriesSelectionModel;
import org.weasis.dicom.explorer.main.StudyPane;
import org.weasis.dicom.explorer.main.ThumbnailMouseAndKeyAdapter;

/** DICOM Explorer tree/list. Instances created on demand from {@link DicomExplorerFactory}. */
public class DicomExplorer extends PluginTool implements DataExplorerView {

  public static final String NAME = "DICOM Explorer";

  private final DicomModel model;
  private final DefaultListModel<String> listModel = new DefaultListModel<>();
  private final JList<String> list = new JList<>(listModel);
  private final SeriesSelectionModel selection = new SeriesSelectionModel();
  private final ThumbnailMouseAndKeyAdapter thumbs = new ThumbnailMouseAndKeyAdapter(selection);
  private final PatientPane patientPane;
  private final StudyPane studyPane;

  public DicomExplorer(DicomModel model) {
    super(NAME, 0);
    this.model = model == null ? new DicomModel() : model;
    this.patientPane = new PatientPane(this.model);
    this.studyPane = new StudyPane(patientPane.getSelectionManager());
    JPanel hierarchy = new JPanel(new BorderLayout());
    hierarchy.add(patientPane, BorderLayout.NORTH);
    hierarchy.add(studyPane, BorderLayout.CENTER);
    add(hierarchy, BorderLayout.NORTH);
    add(new JScrollPane(list), BorderLayout.CENTER);
    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    list.addKeyListener(thumbs.keyAdapter());
    refresh();
  }

  public PatientPane patientPane() {
    return patientPane;
  }

  public StudyPane studyPane() {
    return studyPane;
  }

  public void refresh() {
    patientPane.refresh();
    studyPane.refresh();
    listModel.clear();
    for (ImportedInstance inst :
        DicomSorter.sortSeries(patientPane.getSelectionManager().selectedInstances())) {
      listModel.addElement(
          inst.patientName()
              + " / "
              + inst.modality()
              + " #"
              + inst.seriesNumber()
              + " "
              + inst.seriesDescription());
    }
    List<String> labels = new ArrayList<>();
    for (int i = 0; i < listModel.size(); i++) {
      labels.add(listModel.get(i));
    }
    selection.setItems(labels);
  }

  public SeriesSelectionModel seriesSelection() {
    return selection;
  }

  public ThumbnailMouseAndKeyAdapter thumbnailAdapter() {
    return thumbs;
  }

  @Override
  public Insertable.Type getType() {
    return Insertable.Type.EXPLORER;
  }

  @Override
  public DataExplorerModel getDataExplorerModel() {
    return model;
  }

  public DicomModel getDicomModel() {
    return model;
  }

  @Override
  public void dispose() {
    closeDockable();
  }
}
