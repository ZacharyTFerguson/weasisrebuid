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

import org.weasis.dicom.explorer.DicomModel;

/** Patient → study → series pane trio used by {@code DicomExplorer}. */
public class DicomPaneManager {

  private final PatientPane patientPane;
  private final StudyPane studyPane;

  public DicomPaneManager() {
    this(new DicomModel());
  }

  public DicomPaneManager(DicomModel model) {
    this.patientPane = new PatientPane(model);
    this.studyPane = new StudyPane(patientPane.getSelectionManager());
    this.patientPane.setOnSelect(studyPane::refresh);
  }

  public PatientPane getPatientPane() {
    return patientPane;
  }

  public StudyPane getStudyPane() {
    return studyPane;
  }

  public SeriesPane getSeriesPane() {
    return studyPane.getSeriesPane();
  }

  public void refresh() {
    patientPane.refresh();
    studyPane.refresh();
  }
}
