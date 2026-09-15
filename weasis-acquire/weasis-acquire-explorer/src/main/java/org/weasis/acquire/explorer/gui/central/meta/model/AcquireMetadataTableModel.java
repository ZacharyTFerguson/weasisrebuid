/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central.meta.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.swing.table.AbstractTableModel;
import org.weasis.acquire.explorer.AcquireMeta;

/**
 * Tag/value rows driven by {@code
 * weasis.acquire.meta.{global,series,image}.{display,edit,required}}.
 */
public class AcquireMetadataTableModel extends AbstractTableModel {

  public static final String TAG_COLUMN = "Tag";
  public static final String VALUE_COLUMN = "Value";

  private final AcquireMeta.Scope scope;
  private final Properties prefs;
  private final Map<String, String> values;
  private final List<String> displayTags;

  public AcquireMetadataTableModel(
      AcquireMeta.Scope scope, Properties prefs, Map<String, String> values) {
    this.scope = Objects.requireNonNull(scope, "scope");
    this.prefs = prefs == null ? new Properties() : prefs;
    this.values = values == null ? new LinkedHashMap<>() : values;
    this.displayTags =
        new ArrayList<>(AcquireMeta.tokens(this.prefs, scope, AcquireMeta.SetKind.DISPLAY));
  }

  public AcquireMeta.Scope scope() {
    return scope;
  }

  public Map<String, String> values() {
    return values;
  }

  public List<String> displayTags() {
    return List.copyOf(displayTags);
  }

  public boolean isEditableTag(String tag) {
    return AcquireMeta.tokens(prefs, scope, AcquireMeta.SetKind.EDIT).contains(tag);
  }

  public boolean isRequiredTag(String tag) {
    return AcquireMeta.required(prefs, scope, tag);
  }

  public List<String> missingRequired() {
    List<String> missing = new ArrayList<>();
    for (String tag : AcquireMeta.tokens(prefs, scope, AcquireMeta.SetKind.REQUIRED)) {
      String value = values.get(tag);
      if (value == null || value.isBlank()) {
        missing.add(tag);
      }
    }
    return List.copyOf(missing);
  }

  public boolean requiredComplete() {
    return missingRequired().isEmpty();
  }

  @Override
  public int getRowCount() {
    return displayTags.size();
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public String getColumnName(int column) {
    return column == 0 ? TAG_COLUMN : VALUE_COLUMN;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    String tag = displayTags.get(rowIndex);
    if (columnIndex == 0) {
      return tag;
    }
    String value = values.get(tag);
    return value == null ? "" : value;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == 1 && isEditableTag(displayTags.get(rowIndex));
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (!isCellEditable(rowIndex, columnIndex)) {
      return;
    }
    String tag = displayTags.get(rowIndex);
    values.put(tag, aValue == null ? "" : String.valueOf(aValue));
    fireTableCellUpdated(rowIndex, columnIndex);
  }
}
