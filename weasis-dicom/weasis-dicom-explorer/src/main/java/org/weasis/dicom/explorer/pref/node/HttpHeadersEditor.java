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
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.weasis.dicom.explorer.rs.RsQueryParams;

/**
 * DICOMweb extra HTTP headers ({@code Name: value}). Blank names are ignored; a later value for the
 * same name replaces the earlier row.
 */
public class HttpHeadersEditor extends AbstractListEditor<String> {

  private final JTextField nameField = new JTextField(16);
  private final JTextField valueField = new JTextField(24);

  public HttpHeadersEditor() {
    nameField.setName("headerName");
    valueField.setName("headerValue");
    JPanel fields = new JPanel();
    fields.add(new JLabel("Header"));
    fields.add(nameField);
    fields.add(new JLabel("Value"));
    fields.add(valueField);
    add(fields, BorderLayout.NORTH);
  }

  public JTextField nameField() {
    return nameField;
  }

  public JTextField valueField() {
    return valueField;
  }

  public void setDraft(String name, String value) {
    nameField.setText(name == null ? "" : name);
    valueField.setText(value == null ? "" : value);
  }

  @Override
  public String createItem() {
    return formatHeader(nameField.getText(), valueField.getText());
  }

  @Override
  public String modifyItem(String current) {
    String line = createItem();
    return line == null ? current : line;
  }

  @Override
  public boolean addContent() {
    String line = createItem();
    if (line == null) {
      return false;
    }
    return addHeader(nameOf(line), valueOf(line));
  }

  public boolean addHeader(String name, String value) {
    String line = formatHeader(name, value);
    if (line == null) {
      return false;
    }
    String key = nameOf(line);
    for (int i = 0; i < model().size(); i++) {
      if (key.equalsIgnoreCase(nameOf(model().getElementAt(i)))) {
        model().set(i, line);
        itemList().setSelectedIndex(i);
        return true;
      }
    }
    addElement(line);
    return true;
  }

  public boolean addHeaderLine(String line) {
    if (line == null || line.isBlank()) {
      return false;
    }
    return addHeader(nameOf(line), valueOf(line));
  }

  public boolean removeHeader(String name) {
    if (name == null || name.isBlank()) {
      return false;
    }
    String key = name.trim();
    for (int i = 0; i < model().size(); i++) {
      if (key.equalsIgnoreCase(nameOf(model().getElementAt(i)))) {
        model().remove(i);
        return true;
      }
    }
    return false;
  }

  public Map<String, String> headers() {
    Map<String, String> out = new LinkedHashMap<>();
    for (String line : items()) {
      String name = nameOf(line);
      if (!name.isEmpty()) {
        out.put(name, valueOf(line));
      }
    }
    return Map.copyOf(out);
  }

  public void applyTo(RsQueryParams params) {
    if (params == null) {
      return;
    }
    headers().forEach(params::addHeader);
  }

  public void applyTo(DicomWebNode node) {
    if (node == null) {
      return;
    }
    node.setHeaders(headers());
  }

  public static String formatHeader(String name, String value) {
    if (name == null || name.isBlank()) {
      return null;
    }
    return name.trim() + ": " + (value == null ? "" : value.trim());
  }

  public static String nameOf(String line) {
    if (line == null || line.isBlank()) {
      return "";
    }
    int colon = line.indexOf(':');
    return colon < 0 ? line.trim() : line.substring(0, colon).trim();
  }

  public static String valueOf(String line) {
    if (line == null) {
      return "";
    }
    int colon = line.indexOf(':');
    return colon < 0 ? "" : line.substring(colon + 1).trim();
  }
}
