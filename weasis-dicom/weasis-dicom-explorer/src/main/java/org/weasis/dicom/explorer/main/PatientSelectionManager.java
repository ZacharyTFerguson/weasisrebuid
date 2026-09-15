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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportedInstance;

/** Patient then study selection for explorer chrome. */
public class PatientSelectionManager {

  private DicomModel model = new DicomModel();
  private final List<String> patientKeys = new ArrayList<>();
  private final List<String> patientLabels = new ArrayList<>();
  private final List<String> studyUids = new ArrayList<>();
  private final List<String> studyLabels = new ArrayList<>();
  private String selectedPatientKey;
  private String selectedStudyUid;

  public void bind(DicomModel model) {
    this.model = model == null ? new DicomModel() : model;
    refresh();
  }

  public DicomModel getModel() {
    return model;
  }

  public void refresh() {
    patientKeys.clear();
    patientLabels.clear();
    Map<String, List<ImportedInstance>> patients = model.patients();
    for (Map.Entry<String, List<ImportedInstance>> entry : patients.entrySet()) {
      patientKeys.add(entry.getKey());
      ImportedInstance first = entry.getValue().get(0);
      patientLabels.add(first.patientName() + " [" + first.patientId() + "]");
    }
    if (selectedPatientKey == null || !patientKeys.contains(selectedPatientKey)) {
      selectedPatientKey = patientKeys.isEmpty() ? null : patientKeys.get(0);
    }
    refreshStudies();
  }

  void refreshStudies() {
    studyUids.clear();
    studyLabels.clear();
    if (selectedPatientKey == null) {
      selectedStudyUid = null;
      return;
    }
    Map<String, List<ImportedInstance>> studies = model.studies(selectedPatientKey);
    for (Map.Entry<String, List<ImportedInstance>> entry : studies.entrySet()) {
      studyUids.add(entry.getKey());
      ImportedInstance first = entry.getValue().get(0);
      String date = first.studyDate();
      if (date == null || date.isEmpty()) {
        studyLabels.add(entry.getKey());
      } else {
        studyLabels.add(date + " " + entry.getKey());
      }
    }
    if (selectedStudyUid == null || !studyUids.contains(selectedStudyUid)) {
      selectedStudyUid = studyUids.isEmpty() ? null : studyUids.get(0);
    }
  }

  public List<String> patientKeys() {
    return Collections.unmodifiableList(patientKeys);
  }

  public List<String> patientLabels() {
    return Collections.unmodifiableList(patientLabels);
  }

  public List<String> studyUids() {
    return Collections.unmodifiableList(studyUids);
  }

  public List<String> studyLabels() {
    return Collections.unmodifiableList(studyLabels);
  }

  public String selectedPatientKey() {
    return selectedPatientKey;
  }

  public String selectedStudyUid() {
    return selectedStudyUid;
  }

  public void selectPatientIndex(int index) {
    if (index < 0 || index >= patientKeys.size()) {
      return;
    }
    String next = patientKeys.get(index);
    if (!next.equals(selectedPatientKey)) {
      selectedPatientKey = next;
      selectedStudyUid = null;
    }
    refreshStudies();
  }

  public void selectStudyIndex(int index) {
    if (index < 0 || index >= studyUids.size()) {
      return;
    }
    selectedStudyUid = studyUids.get(index);
  }

  public List<ImportedInstance> selectedInstances() {
    if (selectedPatientKey == null) {
      return model.getInstances();
    }
    List<ImportedInstance> ofPatient = model.patients().getOrDefault(selectedPatientKey, List.of());
    if (selectedStudyUid == null) {
      return List.copyOf(ofPatient);
    }
    List<ImportedInstance> ofStudy = new ArrayList<>();
    for (ImportedInstance inst : ofPatient) {
      if (selectedStudyUid.equals(inst.studyUid())) {
        ofStudy.add(inst);
      }
    }
    return ofStudy;
  }
}
