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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.GuiExecutor;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.core.ui.editor.image.ViewTransferHandler;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.explorer.main.DicomPaneManager;
import org.weasis.dicom.explorer.main.DicomTaskManager;
import org.weasis.dicom.explorer.main.PatientPane;
import org.weasis.dicom.explorer.main.SeriesFilter;
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
  private final DicomPaneManager panes;
  private final PatientPane patientPane;
  private final StudyPane studyPane;
  private final SeriesFilter seriesFilter = new SeriesFilter();
  private final PluginOpeningStrategy opening = new PluginOpeningStrategy();

  public DicomExplorer(DicomModel model) {
    super(NAME, 0);
    this.model = model == null ? LocalPersistence.getDicomModel() : model;
    this.panes = new DicomPaneManager(this.model);
    this.patientPane = panes.getPatientPane();
    this.studyPane = panes.getStudyPane();
    this.studyPane.getSeriesPane().setOnOpen(this::openSelected);
    layoutChrome();
    bindModel();
  }

  void layoutChrome() {
    JPanel hierarchy = new JPanel(new BorderLayout());
    hierarchy.add(patientPane, BorderLayout.NORTH);
    hierarchy.add(studyPane, BorderLayout.CENTER);
    add(hierarchy, BorderLayout.NORTH);
    add(new JScrollPane(list), BorderLayout.CENTER);
    add(DicomTaskManager.getInstance().getLoadingPanel(), BorderLayout.SOUTH);
    list.setName("explorer-series");
    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    list.addMouseListener(seriesRowPress());
  }

  MouseAdapter seriesRowPress() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        onSeriesRowPress(e);
      }
    };
  }

  void onSeriesRowPress(MouseEvent e) {
    int i = list.locationToIndex(e.getPoint());
    if (i >= 0) {
      thumbs.pressed(i, e);
    }
    ViewTransferHandler.pressSeries(list, e);
  }

  void bindModel() {
    list.addKeyListener(thumbs.keyAdapter());
    list.addKeyListener(enterOpensSelected());
    this.model.addPropertyChangeListener(evt -> GuiExecutor.invokeAndWait(this::refresh));
    refresh();
  }

  KeyAdapter enterOpensSelected() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          openSelected();
        }
      }
    };
  }

  public PatientPane patientPane() {
    return patientPane;
  }

  public StudyPane studyPane() {
    return studyPane;
  }

  public DicomPaneManager panes() {
    return panes;
  }

  public SeriesFilter seriesFilter() {
    return seriesFilter;
  }

  public PluginOpeningStrategy openingStrategy() {
    return opening;
  }

  public void refresh() {
    panes.refresh();
    listModel.clear();
    List<String> labels = new ArrayList<>();
    List<MediaSeries<?>> rows = new ArrayList<>();
    for (ImportedInstance inst : filteredInstances()) {
      labels.add(labelOf(inst));
      rows.add(seriesOf(inst));
      listModel.addElement(labels.getLast());
    }
    list.putClientProperty(ViewTransferHandler.SERIES_ROWS, List.copyOf(rows));
    selection.setItems(labels);
  }

  List<ImportedInstance> filteredInstances() {
    List<ImportedInstance> out = new ArrayList<>();
    for (ImportedInstance inst :
        DicomSorter.sortSeries(patientPane.getSelectionManager().selectedInstances())) {
      if (seriesFilter.accept(inst)) {
        out.add(inst);
      }
    }
    return out;
  }

  static String labelOf(ImportedInstance inst) {
    return inst.patientName()
        + " / "
        + inst.modality()
        + " #"
        + inst.seriesNumber()
        + " "
        + inst.seriesDescription();
  }

  MediaSeries<?> seriesOf(ImportedInstance inst) {
    return studyPane.getSeriesPane().seriesFor(inst);
  }

  public JList<String> seriesList() {
    return list;
  }

  public List<ViewerPlugin<?>> openSelected() {
    List<ImportedInstance> instances =
        DicomSorter.sortSeries(patientPane.getSelectionManager().selectedInstances());
    List<ImportedInstance> chosen = new ArrayList<>();
    Set<Integer> idxs = selection.selectedIndices();
    if (idxs.isEmpty()) {
      chosen.addAll(instances);
    } else {
      for (int i : idxs) {
        if (i >= 0 && i < instances.size()) {
          chosen.add(instances.get(i));
        }
      }
    }
    return opening.open(chosen);
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
