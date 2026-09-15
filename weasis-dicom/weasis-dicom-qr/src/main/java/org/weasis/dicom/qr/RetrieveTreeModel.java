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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

/** Patient → study → series tree filled from C-FIND identifiers (no DIMSE I/O). */
public class RetrieveTreeModel extends DefaultTreeModel {

  private final List<Attributes> results = new ArrayList<>();
  private final Set<String> studyUids = new LinkedHashSet<>();
  private final Map<String, Set<String>> seriesByStudy = new LinkedHashMap<>();

  public RetrieveTreeModel() {
    super(new DefaultMutableTreeNode("Q/R"));
  }

  public void addCFindResults(List<Attributes> findResults) {
    if (findResults == null) {
      return;
    }
    for (Attributes attrs : findResults) {
      addCFindResult(attrs);
    }
  }

  public void addCFindResult(Attributes attrs) {
    if (attrs == null) {
      return;
    }
    results.add(attrs);
    String study = attrs.getString(Tag.StudyInstanceUID, "");
    if (study.isBlank()) {
      reload();
      return;
    }
    studyUids.add(study);
    String patient =
        firstNonBlank(attrs.getString(Tag.PatientID), attrs.getString(Tag.PatientName), "UNKNOWN");
    String series = attrs.getString(Tag.SeriesInstanceUID, "");
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
    DefaultMutableTreeNode patientNode = child(root, patient);
    DefaultMutableTreeNode studyNode = child(patientNode, study);
    if (!series.isBlank()) {
      seriesByStudy.computeIfAbsent(study, key -> new LinkedHashSet<>()).add(series);
      child(studyNode, series);
    }
    reload();
  }

  public void clearResults() {
    results.clear();
    studyUids.clear();
    seriesByStudy.clear();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
    root.removeAllChildren();
    reload();
  }

  public List<Attributes> results() {
    return Collections.unmodifiableList(results);
  }

  public List<String> studyUids() {
    return List.copyOf(studyUids);
  }

  public List<String> seriesUids(String studyUid) {
    Set<String> series = seriesByStudy.get(studyUid);
    if (series == null) {
      return List.of();
    }
    return List.copyOf(series);
  }

  static DefaultMutableTreeNode child(DefaultMutableTreeNode parent, String key) {
    for (int i = 0; i < parent.getChildCount(); i++) {
      DefaultMutableTreeNode existing = (DefaultMutableTreeNode) parent.getChildAt(i);
      if (key.equals(String.valueOf(existing.getUserObject()))) {
        return existing;
      }
    }
    DefaultMutableTreeNode created = new DefaultMutableTreeNode(key);
    parent.add(created);
    return created;
  }

  static String firstNonBlank(String... values) {
    if (values == null) {
      return "UNKNOWN";
    }
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return "UNKNOWN";
  }
}
