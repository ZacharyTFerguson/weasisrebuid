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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/** File &gt; Preferences DIMSE node editor (called AE Title, host, port). */
public class DicomNodeDialog extends JDialog {

  public static final String TITLE = "DICOM Node";

  private final JTextField descriptionField = new JTextField(20);
  private final JTextField aeTitleField = new JTextField(16);
  private final JTextField hostField = new JTextField(24);
  private final JTextField portField = new JTextField(6);

  public DicomNodeDialog() {
    this(null);
  }

  public DicomNodeDialog(Frame owner) {
    super(owner, TITLE, true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    descriptionField.setName("nodeDescription");
    aeTitleField.setName("nodeAeTitle");
    hostField.setName("nodeHost");
    portField.setName("nodePort");
    resetToDefaults();
    JPanel form = new JPanel(new GridLayout(0, 2));
    form.add(new JLabel("Description"));
    form.add(descriptionField);
    form.add(new JLabel("AE Title"));
    form.add(aeTitleField);
    form.add(new JLabel("Hostname"));
    form.add(hostField);
    form.add(new JLabel("Port"));
    form.add(portField);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(form, BorderLayout.CENTER);
    setSize(420, 200);
  }

  public void resetToDefaults() {
    descriptionField.setText(AbstractDicomNode.DEFAULT_AE_TITLE);
    aeTitleField.setText(AbstractDicomNode.DEFAULT_AE_TITLE);
    hostField.setText(AbstractDicomNode.DEFAULT_HOSTNAME);
    portField.setText(Integer.toString(AbstractDicomNode.DEFAULT_PORT));
  }

  public void load(AbstractDicomNode node) {
    if (node == null) {
      resetToDefaults();
      return;
    }
    descriptionField.setText(node.description());
    aeTitleField.setText(node.aeTitle());
    hostField.setText(node.host());
    portField.setText(Integer.toString(node.port()));
  }

  public JTextField descriptionField() {
    return descriptionField;
  }

  public JTextField aeTitleField() {
    return aeTitleField;
  }

  public JTextField hostField() {
    return hostField;
  }

  public JTextField portField() {
    return portField;
  }

  public void setDescription(String description) {
    descriptionField.setText(description == null ? "" : description);
  }

  public void setAeTitle(String aeTitle) {
    aeTitleField.setText(aeTitle == null ? "" : aeTitle);
  }

  public void setHost(String hostname) {
    hostField.setText(hostname == null ? "" : hostname);
  }

  public void setPortText(String port) {
    portField.setText(port == null ? "" : port);
  }

  public DefaultDicomNode apply() {
    return new DefaultDicomNode(
        text(descriptionField),
        text(aeTitleField),
        text(hostField),
        parsePort(portField.getText()));
  }

  public DefaultDicomNode applyTo(DicomNodeListView view, AbstractDicomNode replacing) {
    DefaultDicomNode node = apply();
    if (view == null) {
      return node;
    }
    if (replacing != null) {
      int index = view.nodes().indexOf(replacing);
      if (index >= 0) {
        view.replaceAt(index, node);
        view.select(index);
        return node;
      }
    }
    view.addNode(node);
    return node;
  }

  static int parsePort(String raw) {
    if (raw == null || raw.isBlank()) {
      return 0;
    }
    try {
      return Integer.parseInt(raw.trim());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  static String text(JTextField field) {
    String value = field.getText();
    return value == null ? "" : value;
  }
}
