/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central.meta.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Objects;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import org.weasis.acquire.explorer.AcquireMeta;
import org.weasis.acquire.explorer.gui.central.meta.model.AcquireMetadataTableModel;

/** Table chrome for one acquire metadata scope (global / series / image). */
public class AcquireMetadataPanel extends JPanel {

  private final AcquireMetadataTableModel model;
  private final JTable table;

  public AcquireMetadataPanel(AcquireMetadataTableModel model) {
    super(new BorderLayout());
    this.model = Objects.requireNonNull(model, "model");
    this.table = new JTable(model);
    table.setName("acquire-meta-" + model.scope().name().toLowerCase());
    table.getColumnModel().getColumn(1).setCellEditor(new MetaValueCellEditor());
    add(new JScrollPane(table), BorderLayout.CENTER);
  }

  public AcquireMetadataTableModel model() {
    return model;
  }

  public JTable table() {
    return table;
  }

  public AcquireMeta.Scope scope() {
    return model.scope();
  }

  public boolean requiredComplete() {
    return model.requiredComplete();
  }

  static boolean isPersonNameTag(String tag) {
    return "PatientName".equals(tag) || "ReferringPhysicianName".equals(tag);
  }

  static boolean isAnatomicRegionTag(String tag) {
    return AnatomicRegionView.LABEL_TAG.equals(tag)
        || AnatomicRegionView.BODY_PART_TAG.equals(tag)
        || AnatomicRegionView.CODE_TAG.equals(tag);
  }

  static final class MetaValueCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final PersonNameCellEditor personName = new PersonNameCellEditor();
    private final AnatomicRegionCellEditor anatomicRegion = new AnatomicRegionCellEditor();
    private final DefaultCellEditor text = new DefaultCellEditor(new JTextField());
    private TableCellEditor active = text;

    @Override
    public Object getCellEditorValue() {
      return active.getCellEditorValue();
    }

    @Override
    public Component getTableCellEditorComponent(
        JTable table, Object value, boolean isSelected, int row, int column) {
      String tag = "";
      if (table != null && row >= 0) {
        Object tagValue = table.getValueAt(row, 0);
        tag = tagValue == null ? "" : String.valueOf(tagValue);
      }
      if (isPersonNameTag(tag)) {
        active = personName;
      } else if (isAnatomicRegionTag(tag)) {
        anatomicRegion.setTag(tag);
        active = anatomicRegion;
      } else {
        active = text;
      }
      return active.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
  }
}
