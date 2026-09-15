/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.exp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportedInstance;

/** Patient → study → series → instance tree with checkboxes (all selected by default). */
public class CheckTreeModel extends DefaultTreeModel {

  public CheckTreeModel(DicomModel model) {
    super(buildRoot(model));
  }

  public List<ImportedInstance> selectedInstances() {
    List<ImportedInstance> selected = new ArrayList<>();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
    Enumeration<?> depth = root.depthFirstEnumeration();
    while (depth.hasMoreElements()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) depth.nextElement();
      if (!(node.getUserObject() instanceof Item item) || item.instance() == null) {
        continue;
      }
      if (isEffectivelyChecked(node)) {
        selected.add(item.instance());
      }
    }
    return selected;
  }

  public void setSeriesChecked(String seriesUid, boolean checked) {
    if (seriesUid == null) {
      return;
    }
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
    Enumeration<?> depth = root.depthFirstEnumeration();
    while (depth.hasMoreElements()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) depth.nextElement();
      if (!(node.getUserObject() instanceof Item item)) {
        continue;
      }
      if (seriesUid.equals(item.seriesUid())) {
        setSubtreeChecked(node, checked);
      }
    }
  }

  static boolean isEffectivelyChecked(DefaultMutableTreeNode node) {
    DefaultMutableTreeNode cur = node;
    while (cur != null) {
      if (cur.getUserObject() instanceof Item item && !item.checked()) {
        return false;
      }
      Object parent = cur.getParent();
      cur = parent instanceof DefaultMutableTreeNode n ? n : null;
    }
    return true;
  }

  static void setSubtreeChecked(DefaultMutableTreeNode node, boolean checked) {
    if (node.getUserObject() instanceof Item item) {
      item.setChecked(checked);
    }
    for (int i = 0; i < node.getChildCount(); i++) {
      setSubtreeChecked((DefaultMutableTreeNode) node.getChildAt(i), checked);
    }
  }

  static DefaultMutableTreeNode buildRoot(DicomModel model) {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new Item("DICOM", null, null, true));
    if (model == null) {
      return root;
    }
    Map<String, DefaultMutableTreeNode> patients = new LinkedHashMap<>();
    Map<String, DefaultMutableTreeNode> studies = new LinkedHashMap<>();
    Map<String, DefaultMutableTreeNode> series = new LinkedHashMap<>();
    for (ImportedInstance inst : model.getInstances()) {
      String patientKey = inst.patientKey();
      DefaultMutableTreeNode patient =
          patients.computeIfAbsent(
              patientKey,
              k -> {
                DefaultMutableTreeNode n =
                    new DefaultMutableTreeNode(
                        new Item(inst.patientName() + " " + inst.patientId(), null, null, true));
                root.add(n);
                return n;
              });
      String studyKey = patientKey + "\t" + inst.studyUid();
      DefaultMutableTreeNode study =
          studies.computeIfAbsent(
              studyKey,
              k -> {
                DefaultMutableTreeNode n =
                    new DefaultMutableTreeNode(new Item(inst.studyUid(), null, null, true));
                patient.add(n);
                return n;
              });
      String seriesKey = studyKey + "\t" + inst.seriesUid();
      DefaultMutableTreeNode seriesNode =
          series.computeIfAbsent(
              seriesKey,
              k -> {
                DefaultMutableTreeNode n =
                    new DefaultMutableTreeNode(
                        new Item(
                            inst.modality() + " " + inst.seriesUid(),
                            null,
                            inst.seriesUid(),
                            true));
                study.add(n);
                return n;
              });
      seriesNode.add(
          new DefaultMutableTreeNode(new Item(inst.sopUid(), inst, inst.seriesUid(), true)));
    }
    return root;
  }

  public static final class Item {
    private final String label;
    private final ImportedInstance instance;
    private final String seriesUid;
    private boolean checked;

    public Item(String label, ImportedInstance instance, String seriesUid, boolean checked) {
      this.label = label == null ? "" : label;
      this.instance = instance;
      this.seriesUid = seriesUid;
      this.checked = checked;
    }

    public String label() {
      return label;
    }

    public ImportedInstance instance() {
      return instance;
    }

    public String seriesUid() {
      return seriesUid;
    }

    public boolean checked() {
      return checked;
    }

    public void setChecked(boolean checked) {
      this.checked = checked;
    }

    @Override
    public String toString() {
      return label;
    }
  }
}
