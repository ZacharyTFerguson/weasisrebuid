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
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.weasis.core.api.net.auth.AuthMethod;
import org.weasis.core.api.net.auth.DefaultAuthMethod;
import org.weasis.core.api.service.WProperties;

/** Editor for a DICOMweb {@link AuthMethod} (id, header name, token). */
public class AuthMethodDialog extends JDialog {

  public static final String TITLE = "Authentication";
  public static final String DEFAULT_ID = "default";
  public static final String DEFAULT_HEADER = "Authorization";

  private final JTextField idField = new JTextField(16);
  private final JTextField headerField = new JTextField(16);
  private final JPasswordField tokenField = new JPasswordField(24);

  public AuthMethodDialog() {
    this(null);
  }

  public AuthMethodDialog(Frame owner) {
    super(owner, TITLE, true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    idField.setName("authId");
    headerField.setName("authHeader");
    tokenField.setName("authToken");
    headerField.setText(DEFAULT_HEADER);
    JPanel form = new JPanel(new GridLayout(0, 2));
    form.add(new JLabel("ID"));
    form.add(idField);
    form.add(new JLabel("Header"));
    form.add(headerField);
    form.add(new JLabel("Token"));
    form.add(tokenField);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(form, BorderLayout.CENTER);
    setSize(400, 180);
  }

  public void load(AuthMethod method) {
    if (method == null) {
      idField.setText("");
      headerField.setText(DEFAULT_HEADER);
      tokenField.setText("");
      return;
    }
    idField.setText(method.getId() == null ? "" : method.getId());
    if (method instanceof DefaultAuthMethod def) {
      headerField.setText(
          def.header() == null || def.header().isBlank() ? DEFAULT_HEADER : def.header());
      tokenField.setText(def.token() == null ? "" : def.token());
      return;
    }
    headerField.setText(DEFAULT_HEADER);
    Map<String, String> headers = method.authorizationHeaders();
    if (headers != null && !headers.isEmpty()) {
      Map.Entry<String, String> first = headers.entrySet().iterator().next();
      headerField.setText(first.getKey());
      tokenField.setText(first.getValue() == null ? "" : first.getValue());
    } else {
      tokenField.setText("");
    }
  }

  public JTextField idField() {
    return idField;
  }

  public JTextField headerField() {
    return headerField;
  }

  public JPasswordField tokenField() {
    return tokenField;
  }

  public void setId(String id) {
    idField.setText(id == null ? "" : id);
  }

  public void setHeader(String header) {
    headerField.setText(header == null ? "" : header);
  }

  public void setToken(String token) {
    tokenField.setText(token == null ? "" : token);
  }

  public DefaultAuthMethod apply() {
    String id = AuthenticationPersistence.sanitizeId(idField.getText());
    if (id.isEmpty()) {
      id = DEFAULT_ID;
    }
    String header = headerField.getText();
    if (header == null || header.isBlank()) {
      header = DEFAULT_HEADER;
    } else {
      header = header.trim();
    }
    char[] chars = tokenField.getPassword();
    String token = chars == null ? "" : new String(chars);
    return new DefaultAuthMethod(id, header, token);
  }

  public DefaultAuthMethod persistTo(WProperties prefs) {
    DefaultAuthMethod method = apply();
    AuthenticationPersistence.upsert(prefs, method);
    return method;
  }
}
