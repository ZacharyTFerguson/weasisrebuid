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
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.dcm4che3.data.Attributes;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.GuiExecutor;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.docking.PluginTool;
import org.weasis.core.ui.editor.image.ViewTransferHandler;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.explorer.exp.DicomExport;
import org.weasis.dicom.explorer.exp.ExportDicomView;
import org.weasis.dicom.explorer.imp.DicomImport;
import org.weasis.dicom.explorer.main.DicomPaneManager;
import org.weasis.dicom.explorer.main.DicomTaskManager;
import org.weasis.dicom.explorer.main.PatientPane;
import org.weasis.dicom.explorer.main.SeriesFilter;
import org.weasis.dicom.explorer.main.SeriesSelectionModel;
import org.weasis.dicom.explorer.main.StudyPane;
import org.weasis.dicom.explorer.main.ThumbnailMouseAndKeyAdapter;
import org.weasis.dicom.explorer.pr.InterpolatedPath2D;
import org.weasis.dicom.explorer.pr.PrGraphicUtil;
import org.weasis.dicom.explorer.tag.DicomFieldsView;

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
  private final JComboBox<String> filterMode =
      new JComboBox<>(new String[] {SeriesFilter.TEXT, SeriesFilter.DATE, SeriesFilter.MODALITY});
  private final JTextField filterQuery = new JTextField();
  private final JLabel filterHits = new JLabel("0");
  private final DicomFieldsView fields = new DicomFieldsView();
  private final PrGraphicUtil prUtil = new PrGraphicUtil();
  private final JButton applyPr = new JButton("Apply PR");
  private final JLabel prGraphics = new JLabel("0");
  private final JLabel prMapped = new JLabel("none");
  private final JButton applyInterp = new JButton("Interp");
  private final JLabel interpShape = new JLabel("none");
  private final JLabel interpThrough = new JLabel("none");

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
    JPanel north = new JPanel(new BorderLayout());
    north.add(filterBar(), BorderLayout.NORTH);
    north.add(hierarchy, BorderLayout.CENTER);
    add(north, BorderLayout.NORTH);
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(list), fields);
    split.setName("explorer-fields-split");
    split.setResizeWeight(0.55);
    add(split, BorderLayout.CENTER);
    add(DicomTaskManager.getInstance().getLoadingPanel(), BorderLayout.SOUTH);
    list.setName("explorer-series");
    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    list.addMouseListener(seriesRowPress());
  }

  JPanel filterBar() {
    JPanel bar = new JPanel(new BorderLayout());
    bindFilterMode();
    bindFilterQuery();
    bindFilterHits();
    bar.add(filterMode, BorderLayout.WEST);
    bar.add(queryRow(), BorderLayout.CENTER);
    bar.add(prMapChrome(), BorderLayout.EAST);
    return bar;
  }

  JPanel queryRow() {
    JPanel row = new JPanel(new BorderLayout());
    row.add(filterQuery, BorderLayout.CENTER);
    row.add(filterHits, BorderLayout.EAST);
    return row;
  }

  JPanel prMapChrome() {
    JPanel box = new JPanel();
    applyPr.setName("apply-pr");
    applyPr.setToolTipText("Apply PR");
    applyPr.addActionListener(e -> applyPr());
    prGraphics.setName("pr-graphics");
    prMapped.setName("pr-mapped");
    box.add(applyPr);
    box.add(prGraphics);
    box.add(prMapped);
    applyInterp.setName("apply-interp");
    applyInterp.setToolTipText("Interp");
    applyInterp.addActionListener(e -> applyInterp());
    interpShape.setName("interp-shape");
    interpThrough.setName("interp-through");
    box.add(applyInterp);
    box.add(interpShape);
    box.add(interpThrough);
    return box;
  }

  public JButton applyPrButton() {
    return applyPr;
  }

  public JLabel prGraphicsLabel() {
    return prGraphics;
  }

  public JLabel prMappedLabel() {
    return prMapped;
  }

  public String prGraphicsText() {
    return prGraphics.getText();
  }

  public String prMappedText() {
    return prMapped.getText();
  }

  public String applyPr() {
    return showMapped(prUtil.mapSamplePolyline());
  }

  public JButton applyInterpButton() {
    return applyInterp;
  }

  public JLabel interpShapeLabel() {
    return interpShape;
  }

  public JLabel interpThroughLabel() {
    return interpThrough;
  }

  public String interpShapeText() {
    return interpShape.getText();
  }

  public String interpThroughText() {
    return interpThrough.getText();
  }

  public String applyInterp() {
    return showInterp(prUtil.mapSampleInterpolated());
  }

  String showInterp(Graphic graphic) {
    InterpolatedPath2D path = interpPath(graphic);
    if (path == null) {
      interpShape.setText("none");
      interpThrough.setText("none");
      return "none";
    }
    interpShape.setText("InterpolatedPath2D");
    interpThrough.setText(throughControls(path) ? "through" : "miss");
    return interpShape.getText();
  }

  static InterpolatedPath2D interpPath(Graphic graphic) {
    if (graphic == null || !(graphic.getShape() instanceof InterpolatedPath2D path)) {
      return null;
    }
    return path;
  }

  static boolean throughControls(InterpolatedPath2D path) {
    return path.distanceTo(0, 0) < 0.05
        && path.distanceTo(10, 0) < 0.05
        && path.distanceTo(10, 10) < 0.05
        && path.distanceTo(0, 10) < 0.05;
  }

  String showMapped(Graphic graphic) {
    if (graphic == null) {
      prGraphics.setText("0");
      prMapped.setText("none");
      return "none";
    }
    prGraphics.setText("1");
    String name = graphic.getClass().getSimpleName();
    prMapped.setText(name);
    return name;
  }

  void bindFilterMode() {
    filterMode.setName("explorer-filter-mode");
    filterMode.setSelectedItem(seriesFilter.getMode());
    filterMode.addItemListener(this::onFilterMode);
  }

  void bindFilterQuery() {
    filterQuery.setName("explorer-filter-query");
    filterQuery.getDocument().addDocumentListener(queryListener());
  }

  void bindFilterHits() {
    filterHits.setName("explorer-filter-hits");
  }

  void showHits(int n) {
    filterHits.setText(Integer.toString(n));
  }

  void onFilterMode(ItemEvent e) {
    if (e.getStateChange() != ItemEvent.SELECTED) {
      return;
    }
    seriesFilter.setMode(String.valueOf(filterMode.getSelectedItem()));
    refresh();
  }

  DocumentListener queryListener() {
    return new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        applyQueryField();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        applyQueryField();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        applyQueryField();
      }
    };
  }

  void applyQueryField() {
    seriesFilter.setQuery(filterQuery.getText());
    refresh();
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
    bindFields();
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

  public JComboBox<String> filterModeCombo() {
    return filterMode;
  }

  public JTextField filterQueryField() {
    return filterQuery;
  }

  public JLabel filterHitsLabel() {
    return filterHits;
  }

  public String filterHitsText() {
    return filterHits.getText();
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
    showHits(labels.size());
    bindFields();
  }

  public DicomFieldsView fieldsView() {
    return fields;
  }

  public ExportDicomView createExportView(Frame owner) {
    return DicomExport.open(owner, model);
  }

  public ImportDicomDialog createImportView(Frame owner, boolean cd) {
    return new ImportDicomDialog(owner, model, cd);
  }

  @Override
  public void openExport(Frame owner) {
    DicomExport.show(owner, model);
  }

  @Override
  public void openImport(Frame owner, boolean cd) {
    DicomImport.show(owner, model, cd);
  }

  void bindFields() {
    fields.changeDicomInfo(datasetOf(selectedInstance()));
  }

  ImportedInstance selectedInstance() {
    List<ImportedInstance> instances = filteredInstances();
    if (instances.isEmpty()) {
      return null;
    }
    Integer idx = firstSelectedIndex();
    if (idx != null && idx >= 0 && idx < instances.size()) {
      return instances.get(idx);
    }
    return instances.get(0);
  }

  Integer firstSelectedIndex() {
    Set<Integer> idxs = selection.selectedIndices();
    if (idxs.isEmpty()) {
      return null;
    }
    return idxs.iterator().next();
  }

  static Attributes datasetOf(ImportedInstance inst) {
    File file = inst == null ? null : inst.file();
    if (file == null) {
      return null;
    }
    try {
      return DicomMediaIO.open(file).getDataset();
    } catch (IOException e) {
      return null;
    }
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
