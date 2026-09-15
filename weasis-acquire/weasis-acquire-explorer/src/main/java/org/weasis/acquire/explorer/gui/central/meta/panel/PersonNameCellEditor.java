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
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/** Table editor for PatientName. Value is the composed DICOM PN string. */
public class PersonNameCellEditor extends AbstractCellEditor implements TableCellEditor {

  private final PersonNameView view = new PersonNameView();
  private boolean fromWorklistOrCommand;

  public PersonNameView view() {
    return view;
  }

  public void setFromWorklistOrCommand(boolean fromWorklistOrCommand) {
    this.fromWorklistOrCommand = fromWorklistOrCommand;
  }

  @Override
  public Object getCellEditorValue() {
    return view.composed();
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    view.setPersonName(value == null ? "" : String.valueOf(value), fromWorklistOrCommand);
    return view;
  }

  @Override
  public boolean stopCellEditing() {
    if (!view.componentsValid()) {
      return false;
    }
    if (!view.preservesInbound() && view.isPreviewOverLength()) {
      return false;
    }
    return super.stopCellEditing();
  }
}
