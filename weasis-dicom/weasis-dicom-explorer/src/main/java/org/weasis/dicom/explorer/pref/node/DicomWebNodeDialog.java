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
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.weasis.core.api.net.auth.AuthMethod;
import org.weasis.dicom.explorer.pref.node.DicomWebNode.WebType;

/**
 * DICOMweb node editor: base URL, QIDO/WADO/STOW type, HTTP headers, and optional auth method.
 * Apply rejects a URL that is not {@code http://} or {@code https://}.
 */
public class DicomWebNodeDialog extends JDialog {

  public static final String TITLE = "DICOMweb Node";

  private final JTextField descriptionField = new JTextField(20);
  private final JTextField urlField = new JTextField(32);
  private final JComboBox<WebType> webType = new JComboBox<>(WebType.values());
  private final JComboBox<AuthMethod> authMethods = new JComboBox<>();
  private final HttpHeadersEditor headersEditor = new HttpHeadersEditor();

  public DicomWebNodeDialog() {
    this(null);
  }

  public DicomWebNodeDialog(Frame owner) {
    super(owner, TITLE, true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    descriptionField.setName("webDescription");
    urlField.setName("webUrl");
    webType.setName("webType");
    authMethods.setName("webAuth");
    webType.setSelectedItem(WebType.QIDO_RS);
    JPanel form = new JPanel(new GridLayout(0, 2));
    form.add(new JLabel("Description"));
    form.add(descriptionField);
    form.add(new JLabel("URL"));
    form.add(urlField);
    form.add(new JLabel("Type"));
    form.add(webType);
    form.add(new JLabel("Authentication"));
    form.add(authMethods);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(form, BorderLayout.NORTH);
    getContentPane().add(headersEditor, BorderLayout.CENTER);
    setSize(520, 360);
  }

  public void load(DicomWebNode node) {
    if (node == null) {
      descriptionField.setText("");
      urlField.setText("");
      webType.setSelectedItem(WebType.QIDO_RS);
      headersEditor.clearItems();
      authMethods.setSelectedItem(null);
      return;
    }
    descriptionField.setText(node.description());
    urlField.setText(node.baseUrl());
    webType.setSelectedItem(node.webType());
    headersEditor.clearItems();
    node.headers().forEach(headersEditor::addHeader);
    if (node.authMethod() != null) {
      authMethods.setSelectedItem(node.authMethod());
    }
  }

  public void setAuthChoices(List<AuthMethod> methods) {
    authMethods.removeAllItems();
    if (methods == null) {
      return;
    }
    for (AuthMethod method : methods) {
      if (method != null) {
        authMethods.addItem(method);
      }
    }
  }

  public JTextField descriptionField() {
    return descriptionField;
  }

  public JTextField urlField() {
    return urlField;
  }

  public JComboBox<WebType> webTypeCombo() {
    return webType;
  }

  public JComboBox<AuthMethod> authCombo() {
    return authMethods;
  }

  public HttpHeadersEditor headersEditor() {
    return headersEditor;
  }

  public void setDescription(String description) {
    descriptionField.setText(description == null ? "" : description);
  }

  public void setUrl(String url) {
    urlField.setText(url == null ? "" : url);
  }

  public void setWebType(WebType type) {
    webType.setSelectedItem(type == null ? WebType.QIDO_RS : type);
  }

  public void setAuthMethod(AuthMethod method) {
    if (method != null && !containsAuth(method)) {
      authMethods.addItem(method);
    }
    authMethods.setSelectedItem(method);
  }

  public DicomWebNode apply() {
    String url = urlField.getText();
    if (!DicomWebNode.httpUrl(url)) {
      return null;
    }
    DicomWebNode node =
        new DicomWebNode(descriptionField.getText(), url, (WebType) webType.getSelectedItem());
    headersEditor.applyTo(node);
    node.setAuthMethod((AuthMethod) authMethods.getSelectedItem());
    return node;
  }

  private boolean containsAuth(AuthMethod method) {
    for (int i = 0; i < authMethods.getItemCount(); i++) {
      if (method.equals(authMethods.getItemAt(i))) {
        return true;
      }
    }
    return false;
  }
}
