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
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

/** Document dump of filtered DICOM tags (plain text plus a simple HTML view). */
public class TagSearchDocumentPanel extends AbstractTagSearchPanel {

  private final JEditorPane document = new JEditorPane();
  private String plainText = "";

  public TagSearchDocumentPanel() {
    this(true);
  }

  public TagSearchDocumentPanel(boolean showSearch) {
    super(showSearch);
    document.setName("tagDocument");
    document.setEditable(false);
    document.setContentType("text/html");
    add(new JScrollPane(document), BorderLayout.CENTER);
    applyFilter(filtered());
  }

  public JEditorPane document() {
    return document;
  }

  public String plainText() {
    return plainText;
  }

  public String html() {
    return document.getText() == null ? "" : document.getText();
  }

  @Override
  protected void applyFilter(List<TagRow> filtered) {
    StringBuilder plain = new StringBuilder();
    StringBuilder html = new StringBuilder();
    html.append("<html><body>");
    if (filtered != null) {
      for (TagRow row : filtered) {
        if (plain.length() > 0) {
          plain.append('\n');
        }
        String line = row.line();
        plain.append(line);
        html.append("<div>").append(escape(line)).append("</div>");
      }
    }
    html.append("</body></html>");
    this.plainText = plain.toString();
    document.setText(html.toString());
  }

  static String escape(String text) {
    if (text == null || text.isEmpty()) {
      return "";
    }
    return text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;");
  }
}
