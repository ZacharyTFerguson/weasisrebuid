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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Shared DICOM-tag search chrome. A blank query keeps every row; otherwise keyword, tag hex, VR, or
 * value must contain the query (case-insensitive). Subclasses must not name a method {@code
 * layout()}.
 */
public abstract class AbstractTagSearchPanel extends JPanel {

  private final JTextField searchField = new JTextField();
  private final List<TagRow> items = new ArrayList<>();
  private String query = "";

  protected AbstractTagSearchPanel() {
    this(true);
  }

  protected AbstractTagSearchPanel(boolean showSearch) {
    super(new BorderLayout());
    searchField.setName("tagSearch");
    searchField
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                syncQueryFromField();
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                syncQueryFromField();
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                syncQueryFromField();
              }
            });
    if (showSearch) {
      add(searchField, BorderLayout.NORTH);
    }
  }

  public JTextField searchField() {
    return searchField;
  }

  public String query() {
    return query;
  }

  public void setQuery(String query) {
    String next = query == null ? "" : query;
    this.query = next;
    if (!Objects.equals(searchField.getText(), next)) {
      searchField.setText(next);
    }
    refresh();
  }

  void syncQueryFromField() {
    this.query = searchField.getText() == null ? "" : searchField.getText();
    refresh();
  }

  public List<TagRow> items() {
    return List.copyOf(items);
  }

  public void setItems(List<TagRow> rows) {
    items.clear();
    if (rows != null) {
      items.addAll(rows);
    }
    refresh();
  }

  public List<TagRow> filtered() {
    List<TagRow> out = new ArrayList<>();
    for (TagRow row : items) {
      if (matches(row, query)) {
        out.add(row);
      }
    }
    return List.copyOf(out);
  }

  public static boolean matches(TagRow row, String query) {
    if (row == null) {
      return false;
    }
    if (query == null || query.isBlank()) {
      return true;
    }
    String needle = query.trim().toLowerCase(Locale.ROOT);
    return contains(row.keyword(), needle)
        || contains(row.hex(), needle)
        || contains(row.hex().replace("(", "").replace(")", ""), needle)
        || contains(row.vr(), needle)
        || contains(row.value(), needle);
  }

  static boolean contains(String haystack, String needle) {
    return haystack != null && haystack.toLowerCase(Locale.ROOT).contains(needle);
  }

  protected void refresh() {
    applyFilter(filtered());
  }

  protected abstract void applyFilter(List<TagRow> filtered);

  /** One dataset tag (or nested sequence item) shown in the table or document dump. */
  public static final class TagRow {

    private final int tag;
    private final String keyword;
    private final String hex;
    private final String vr;
    private final String value;
    private final int depth;

    public TagRow(int tag, String keyword, String hex, String vr, String value, int depth) {
      this.tag = tag;
      this.keyword = keyword == null ? "" : keyword;
      this.hex = hex == null ? "" : hex;
      this.vr = vr == null ? "" : vr;
      this.value = value == null ? "" : value;
      this.depth = Math.max(0, depth);
    }

    public int tag() {
      return tag;
    }

    public String keyword() {
      return keyword;
    }

    public String hex() {
      return hex;
    }

    public String vr() {
      return vr;
    }

    public String value() {
      return value;
    }

    public int depth() {
      return depth;
    }

    public String line() {
      String indent = "  ".repeat(depth);
      return indent + keyword + " " + hex + " " + vr + ": " + value;
    }
  }
}
