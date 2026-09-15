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

import java.awt.Component;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.weasis.acquire.explorer.AcquireImageInfo;

/** Table editor for AnatomicRegion / BodyPartExamined. */
public class AnatomicRegionCellEditor extends AbstractCellEditor implements TableCellEditor {

  private final AnatomicRegionView view = new AnatomicRegionView();
  private String tag = AnatomicRegionView.BODY_PART_TAG;

  public AnatomicRegionView view() {
    return view;
  }

  public void setTag(String tag) {
    this.tag = tag == null ? AnatomicRegionView.BODY_PART_TAG : tag;
  }

  public void apply(AcquireImageInfo image, Map<String, String> seriesMeta) {
    view.apply(image, seriesMeta);
  }

  @Override
  public Object getCellEditorValue() {
    return view.cellValue(tag);
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    if (table != null && table.getModel().getColumnCount() > 0 && row >= 0) {
      Object tagValue = table.getValueAt(row, 0);
      if (tagValue != null) {
        setTag(String.valueOf(tagValue));
      }
    }
    view.setSelectedCode(value == null ? "" : String.valueOf(value));
    return view;
  }
}
