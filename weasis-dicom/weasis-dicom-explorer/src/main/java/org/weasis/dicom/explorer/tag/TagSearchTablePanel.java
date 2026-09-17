/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.tag;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Tabular DICOM-tag search results (keyword, tag, VR, value). */
public class TagSearchTablePanel extends AbstractTagSearchPanel {

  public static final String COL_KEYWORD = "Keyword";
  public static final String COL_TAG = "Tag";
  public static final String COL_VR = "VR";
  public static final String COL_VALUE = "Value";

  private final DefaultTableModel model =
      new DefaultTableModel(new Object[] {COL_KEYWORD, COL_TAG, COL_VR, COL_VALUE}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return false;
        }
      };
  private final JTable table = new JTable(model);

  public TagSearchTablePanel() {
    this(true);
  }

  public TagSearchTablePanel(boolean showSearch) {
    super(showSearch);
    table.setName("tagTable");
    table.setAutoCreateRowSorter(true);
    add(new JScrollPane(table), BorderLayout.CENTER);
    applyFilter(filtered());
  }

  public JTable table() {
    return table;
  }

  public DefaultTableModel model() {
    return model;
  }

  public int rowCount() {
    return model.getRowCount();
  }

  public String valueAt(int row, int column) {
    Object v = model.getValueAt(row, column);
    return v == null ? "" : v.toString();
  }

  @Override
  protected void applyFilter(List<TagRow> filtered) {
    model.setRowCount(0);
    if (filtered == null) {
      return;
    }
    for (TagRow row : filtered) {
      String keyword = row.depth() == 0 ? row.keyword() : "  ".repeat(row.depth()) + row.keyword();
      model.addRow(new Object[] {keyword, row.hex(), row.vr(), row.value()});
    }
  }
}
