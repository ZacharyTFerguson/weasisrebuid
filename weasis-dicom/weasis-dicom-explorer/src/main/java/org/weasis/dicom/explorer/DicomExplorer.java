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
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.docking.PluginTool;

/** DICOM Explorer tree/list. Instances created on demand from {@link DicomExplorerFactory}. */
public class DicomExplorer extends PluginTool implements DataExplorerView {

  public static final String NAME = "DICOM Explorer";

  private final DicomModel model;
  private final DefaultListModel<String> listModel = new DefaultListModel<>();
  private final JList<String> list = new JList<>(listModel);

  public DicomExplorer(DicomModel model) {
    super(NAME, 0);
    this.model = model == null ? new DicomModel() : model;
    add(new JScrollPane(list), BorderLayout.CENTER);
    refresh();
  }

  public void refresh() {
    listModel.clear();
    for (ImportedInstance inst : DicomSorter.sortSeries(model.getInstances())) {
      listModel.addElement(
          inst.patientName()
              + " / "
              + inst.modality()
              + " #"
              + inst.seriesNumber()
              + " "
              + inst.seriesDescription());
    }
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
