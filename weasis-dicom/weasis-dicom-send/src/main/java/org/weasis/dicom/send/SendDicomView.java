/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.send;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

/**
 * File &gt; Export DICOM send page: C-STORE vs STOW-RS destination and file list. STOW multipart is
 * assembled in memory; Have tests do not contact a PACS.
 */
public class SendDicomView extends AbstractItemDialogPage {

  public static final String PAGE = "DICOM Send";
  public static final String STATE = "send-state";
  public static final String NONE = "none";
  public static final String C_STORE = "C-STORE";
  public static final String STOW_RS = "STOW-RS";

  private final StowRS stow = new StowRS();
  private final List<File> files = new ArrayList<>();
  private final JComboBox<SendDicomFactory.Protocol> protocolBox =
      new JComboBox<>(SendDicomFactory.Protocol.values());
  private final JTextField destinationField = new JTextField();
  private final JLabel status = new JLabel(" ");
  private final JButton cstore = new JButton(C_STORE);
  private final JButton stowBtn = new JButton(STOW_RS);
  private final JLabel state = new JLabel(NONE);

  public SendDicomView() {
    super(PAGE, 20);
    setName("send-page");
    bindSendChrome();
    JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
    form.add(sendChrome());
    form.add(new JLabel("Protocol"));
    form.add(protocolBox);
    form.add(new JLabel("Destination (AET or DICOMweb base)"));
    form.add(destinationField);
    JButton sendBtn = new JButton("Send");
    sendBtn.setName("send-run");
    sendBtn.addActionListener(e -> prepareSend());
    form.add(sendBtn);
    form.add(status);
    add(form, BorderLayout.NORTH);
  }

  void bindSendChrome() {
    protocolBox.setName("send-protocol");
    destinationField.setName("send-destination");
    status.setName("send-status");
    cstore.setName("send-cstore");
    stowBtn.setName("send-stow");
    state.setName(STATE);
    cstore.addActionListener(e -> applyCstore());
    stowBtn.addActionListener(e -> applyStow());
  }

  JPanel sendChrome() {
    JPanel chrome = new JPanel();
    chrome.add(cstore);
    chrome.add(stowBtn);
    chrome.add(state);
    return chrome;
  }

  public SendDicomFactory.Protocol protocol() {
    SendDicomFactory.Protocol selected = (SendDicomFactory.Protocol) protocolBox.getSelectedItem();
    return selected == null ? SendDicomFactory.Protocol.C_STORE : selected;
  }

  public void setProtocol(SendDicomFactory.Protocol protocol) {
    protocolBox.setSelectedItem(protocol == null ? SendDicomFactory.Protocol.C_STORE : protocol);
  }

  public String destination() {
    return destinationField.getText();
  }

  public void setDestination(String destination) {
    destinationField.setText(destination == null ? "" : destination);
  }

  public void addFile(File file) {
    if (file != null) {
      files.add(file);
    }
  }

  public List<File> files() {
    return Collections.unmodifiableList(files);
  }

  public String resolvedStowUrl() {
    String dest = destination();
    if (dest == null || dest.isBlank()) {
      return "";
    }
    return stow.resolveStowUrl(dest);
  }

  public byte[] buildStowBody(String boundary) throws IOException {
    List<byte[]> parts = new ArrayList<>();
    for (File file : files) {
      if (file != null && file.isFile()) {
        parts.add(Files.readAllBytes(file.toPath()));
      }
    }
    return stow.buildMultipartBody(boundary, parts);
  }

  public String prepareSend() {
    if (protocol() == SendDicomFactory.Protocol.STOW_RS) {
      String url = resolvedStowUrl();
      status.setText("STOW-RS " + url + " (" + files.size() + " file(s))");
      return url;
    }
    status.setText("C-STORE " + destination() + " (" + files.size() + " file(s))");
    return destination();
  }

  public JButton cstoreButton() {
    return cstore;
  }

  public JButton stowButton() {
    return stowBtn;
  }

  public JLabel stateLabel() {
    return state;
  }

  public String stateText() {
    return state.getText();
  }

  void applyCstore() {
    setProtocol(SendDicomFactory.Protocol.C_STORE);
    prepareSend();
    state.setText(C_STORE);
  }

  void applyStow() {
    setProtocol(SendDicomFactory.Protocol.STOW_RS);
    prepareSend();
    state.setText(STOW_RS);
  }

  @Override
  public void closeAdditionalWindow() {
    // no-op
  }

  @Override
  public void resetToDefaultValues() {
    files.clear();
    setProtocol(SendDicomFactory.Protocol.C_STORE);
    destinationField.setText("");
    status.setText(" ");
    state.setText(NONE);
  }
}
