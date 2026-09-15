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

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Last / First / Middle / Prefix / Suffix fields for DICOM Person Name. Compose, validate, lexical
 * display, and 64-character preview match {@code PersonNameEditor} in acquire-editor.
 */
public class PersonNameView extends JPanel {

  private final JTextField last = field("last");
  private final JTextField first = field("first");
  private final JTextField middle = field("middle");
  private final JTextField prefix = field("prefix");
  private final JTextField suffix = field("suffix");
  private final JLabel preview = new JLabel();

  private boolean loading;
  private boolean preserveInbound;
  private String inboundRaw;

  public PersonNameView() {
    super(new GridLayout(0, 2, 4, 4));
    preview.setName("preview");
    add(new JLabel("Last"));
    add(last);
    add(new JLabel("First"));
    add(first);
    add(new JLabel("Middle"));
    add(middle);
    add(new JLabel("Prefix"));
    add(prefix);
    add(new JLabel("Suffix"));
    add(suffix);
    add(new JLabel("Preview"));
    add(preview);
    listen(last);
    listen(first);
    listen(middle);
    listen(prefix);
    listen(suffix);
    refreshPreview();
  }

  public void setComponents(
      String last, String first, String middle, String prefix, String suffix) {
    loading = true;
    preserveInbound = false;
    inboundRaw = null;
    try {
      this.last.setText(nz(last));
      this.first.setText(nz(first));
      this.middle.setText(nz(middle));
      this.prefix.setText(nz(prefix));
      this.suffix.setText(nz(suffix));
    } finally {
      loading = false;
    }
    refreshPreview();
  }

  public void setPersonName(String pn) {
    setPersonName(pn, false);
  }

  public void setPersonName(String pn, boolean fromWorklistOrCommand) {
    loading = true;
    try {
      String raw = fromWorklistOrCommand ? pn : (pn == null ? "" : pn.trim());
      preserveInbound = fromWorklistOrCommand && raw != null;
      inboundRaw = preserveInbound ? raw : null;
      String source = raw == null ? "" : raw;
      int eq = source.indexOf('=');
      String main = eq < 0 ? source : source.substring(0, eq);
      String[] parts = main.split("\\^", -1);
      last.setText(part(parts, 0));
      first.setText(part(parts, 1));
      middle.setText(part(parts, 2));
      prefix.setText(part(parts, 3));
      suffix.setText(part(parts, 4));
    } finally {
      loading = false;
    }
    refreshPreview();
  }

  public String last() {
    return last.getText();
  }

  public String first() {
    return first.getText();
  }

  public String middle() {
    return middle.getText();
  }

  public String prefix() {
    return prefix.getText();
  }

  public String suffix() {
    return suffix.getText();
  }

  public boolean preservesInbound() {
    return preserveInbound;
  }

  public String composed() {
    if (preserveInbound && inboundRaw != null) {
      return inboundRaw;
    }
    return String.join("^", nz(last()), nz(first()), nz(middle()), nz(prefix()), nz(suffix()));
  }

  public boolean componentsValid() {
    return validPart(last())
        && validPart(first())
        && validPart(middle())
        && validPart(prefix())
        && validPart(suffix());
  }

  public boolean isPreviewOverLength() {
    return composed() != null && composed().length() > 64;
  }

  public String lexicalDisplay() {
    String pn = composed();
    if (pn == null || pn.isBlank()) {
      return "";
    }
    int eq = pn.indexOf('=');
    String ideo = eq < 0 ? "" : pn.substring(eq);
    String main = eq < 0 ? pn : pn.substring(0, eq);
    String[] parts = main.split("\\^", -1);
    String family = parts.length > 0 ? parts[0] : "";
    String given = parts.length > 1 ? parts[1] : "";
    if (family.isBlank() && given.isBlank()) {
      return pn;
    }
    if (given.isBlank()) {
      return family + ideo;
    }
    return family + ", " + given + ideo;
  }

  public Color previewColor() {
    return isPreviewOverLength() ? Color.RED : Color.BLACK;
  }

  public JLabel preview() {
    return preview;
  }

  private void listen(JTextField field) {
    field
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                edited();
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                edited();
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                edited();
              }
            });
  }

  private void edited() {
    if (loading) {
      return;
    }
    preserveInbound = false;
    inboundRaw = null;
    refreshPreview();
  }

  private void refreshPreview() {
    preview.setText(lexicalDisplay());
    preview.setForeground(previewColor());
  }

  private static JTextField field(String name) {
    JTextField field = new JTextField();
    field.setName(name);
    return field;
  }

  private static boolean validPart(String part) {
    if (part == null || part.isEmpty()) {
      return true;
    }
    return part.indexOf('^') < 0 && part.indexOf('=') < 0 && part.indexOf('\\') < 0;
  }

  private static String part(String[] parts, int index) {
    return parts != null && index < parts.length ? parts[index] : "";
  }

  private static String nz(String s) {
    return s == null ? "" : s;
  }
}
