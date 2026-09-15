/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.dcm4che3.data.Attributes;
import org.weasis.core.api.explorer.ImportDicom;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

/** File &gt; Import DICOM Q/R page: C-FIND keys, result tree, C-MOVE/C-GET plan (no sockets). */
public class DicomQrView extends AbstractItemDialogPage implements ImportDicom {

  public static final String PAGE = "DICOM Q/R";

  private final SearchParameters search = new SearchParameters();
  private final RetrieveContext context = new RetrieveContext();
  private final RetrieveTree tree = new RetrieveTree();
  private final GracefulCancel cancel = new GracefulCancel();
  private final JTextField patientIdField = new JTextField();
  private final JTextField patientNameField = new JTextField();
  private final JTextField studyUidField = new JTextField();
  private final JTextField modalityField = new JTextField();
  private final JComboBox<RetrieveContext.RetrieveMethod> methodBox =
      new JComboBox<>(
          new RetrieveContext.RetrieveMethod[] {
            RetrieveContext.RetrieveMethod.C_MOVE, RetrieveContext.RetrieveMethod.C_GET
          });
  private final JLabel status = new JLabel(" ");
  private RetrieveTask lastTask;

  public DicomQrView() {
    super(PAGE, 10);
    JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
    form.add(new JLabel("Patient ID"));
    form.add(patientIdField);
    form.add(new JLabel("Patient name"));
    form.add(patientNameField);
    form.add(new JLabel("Study Instance UID"));
    form.add(studyUidField);
    form.add(new JLabel("Modality"));
    form.add(modalityField);
    form.add(new JLabel("Retrieve"));
    methodBox.addActionListener(e -> applyRetrieveMethod());
    form.add(methodBox);
    JButton searchBtn = new JButton("Search");
    searchBtn.addActionListener(e -> applySearchFields());
    form.add(searchBtn);
    JButton retrieveBtn = new JButton("Retrieve");
    retrieveBtn.addActionListener(e -> startRetrieve());
    form.add(retrieveBtn);
    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(e -> cancelRetrieve());
    form.add(cancelBtn);
    form.add(status);
    add(form, BorderLayout.NORTH);
    add(new JScrollPane(tree), BorderLayout.CENTER);
  }

  public SearchParameters searchParameters() {
    return search;
  }

  public RetrieveContext retrieveContext() {
    return context;
  }

  public RetrieveTree tree() {
    return tree;
  }

  public GracefulCancel gracefulCancel() {
    return cancel;
  }

  public RetrieveTask lastTask() {
    return lastTask;
  }

  public void setPatientId(String patientId) {
    patientIdField.setText(patientId == null ? "" : patientId);
  }

  public void setPatientName(String patientName) {
    patientNameField.setText(patientName == null ? "" : patientName);
  }

  public void setStudyInstanceUid(String studyUid) {
    studyUidField.setText(studyUid == null ? "" : studyUid);
  }

  public void setModality(String modality) {
    modalityField.setText(modality == null ? "" : modality);
  }

  public void setRetrieveMethod(RetrieveContext.RetrieveMethod method) {
    methodBox.setSelectedItem(method == null ? RetrieveContext.RetrieveMethod.C_MOVE : method);
    applyRetrieveMethod();
  }

  public void applySearchFields() {
    search.setPatientId(blankToNull(patientIdField.getText()));
    search.setPatientName(blankToNull(patientNameField.getText()));
    search.setStudyInstanceUid(blankToNull(studyUidField.getText()));
    search.setModality(blankToNull(modalityField.getText()));
    applyRetrieveMethod();
    status.setText("C-FIND keys ready");
  }

  public void loadFindResults(List<Attributes> findResults) {
    tree.loadFindResults(findResults);
    int n = tree.retrieveModel().results().size();
    status.setText("Loaded " + n + " C-FIND result(s)");
  }

  public RetrieveTask startRetrieve() {
    applySearchFields();
    lastTask = new RetrieveTask(context, tree.selection(), cancel);
    lastTask.run();
    if (cancel.isCancelled()) {
      status.setText("Retrieve cancelled");
    } else {
      status.setText("Planned " + lastTask.identifiers().size() + " retrieve identifier(s)");
    }
    return lastTask;
  }

  public void cancelRetrieve() {
    cancel.cancel();
    status.setText("Retrieve cancelled");
  }

  void applyRetrieveMethod() {
    RetrieveContext.RetrieveMethod selected =
        (RetrieveContext.RetrieveMethod) methodBox.getSelectedItem();
    context.setMethod(selected);
  }

  static String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value;
  }

  @Override
  public void importFiles(List<File> files, String zipPassword) {
    // Q/R import is DIMSE / DICOMweb, not a local path.
  }

  @Override
  public void closeAdditionalWindow() {
    cancelRetrieve();
  }

  @Override
  public void resetToDefaultValues() {
    patientIdField.setText("");
    patientNameField.setText("");
    studyUidField.setText("");
    modalityField.setText("");
    methodBox.setSelectedItem(RetrieveContext.RetrieveMethod.C_MOVE);
    context.setMethod(RetrieveContext.RetrieveMethod.C_MOVE);
    tree.clear();
    cancel.reset();
    lastTask = null;
    status.setText(" ");
  }
}
