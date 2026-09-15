/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pref.node;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

/** File &gt; Preferences page listing configured DIMSE nodes. */
public class DicomNodeListView extends AbstractItemDialogPage {

  public static final String TITLE = "DICOM Node";

  private final DefaultListModel<AbstractDicomNode> model = new DefaultListModel<>();
  private final JList<AbstractDicomNode> list = new JList<>(model);

  public DicomNodeListView() {
    super(TITLE, 320);
    list.setName("dicomNodes");
    add(new JScrollPane(list), BorderLayout.CENTER);
    resetToDefaultValues();
  }

  public static AbstractDicomNode defaultNode() {
    return new DefaultDicomNode(
        AbstractDicomNode.DEFAULT_AE_TITLE,
        AbstractDicomNode.DEFAULT_AE_TITLE,
        AbstractDicomNode.DEFAULT_HOSTNAME,
        AbstractDicomNode.DEFAULT_PORT);
  }

  public void addNode(AbstractDicomNode node) {
    if (node != null) {
      model.addElement(node);
    }
  }

  public boolean removeNode(AbstractDicomNode node) {
    return node != null && model.removeElement(node);
  }

  public void replaceAt(int index, AbstractDicomNode node) {
    if (node != null && index >= 0 && index < model.size()) {
      model.set(index, node);
    }
  }

  public void select(int index) {
    if (index >= 0 && index < model.size()) {
      list.setSelectedIndex(index);
    }
  }

  public AbstractDicomNode selected() {
    return list.getSelectedValue();
  }

  public List<AbstractDicomNode> nodes() {
    List<AbstractDicomNode> out = new ArrayList<>();
    for (int i = 0; i < model.size(); i++) {
      out.add(model.getElementAt(i));
    }
    return List.copyOf(out);
  }

  public JList<AbstractDicomNode> nodeList() {
    return list;
  }

  @Override
  public void closeAdditionalWindow() {
    // in-memory node list; DIMSE destinations are applied by send/Q/R pages
  }

  @Override
  public void resetToDefaultValues() {
    model.clear();
    addNode(defaultNode());
    if (!model.isEmpty()) {
      list.setSelectedIndex(0);
    }
  }
}
