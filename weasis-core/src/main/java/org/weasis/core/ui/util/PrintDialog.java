/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.util;

import java.awt.BorderLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Viewer page print (shortcut P). Rasters {@code ImagePrint} with {@link PrintOptions}; does not
 * send to a printer.
 */
public class PrintDialog extends JDialog {

  public static final String STATE = "img-print-state";
  public static final String NONE = "none";
  public static final String PAGE = "page";
  public static final String PRINTABLE = "printable";

  private final PrintOptions options = new PrintOptions();
  private final JButton page = new JButton(PAGE);
  private final JButton printable = new JButton(PRINTABLE);
  private final JLabel state = new JLabel(NONE);
  private boolean accepted;

  public PrintDialog(Window parent) {
    super(parent, "Print", ModalityType.APPLICATION_MODAL);
    setName("img-print-dialog");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    bindPrintChrome();
    JPanel chrome = new JPanel();
    chrome.add(page);
    chrome.add(printable);
    chrome.add(state);
    add(chrome, BorderLayout.NORTH);
    JCheckBox annotations = new JCheckBox("Annotations", true);
    annotations.addActionListener(e -> options.setShowingAnnotations(annotations.isSelected()));
    add(annotations, BorderLayout.CENTER);
    JButton ok = new JButton("OK");
    ok.addActionListener(
        e -> {
          accepted = true;
          dispose();
        });
    add(ok, BorderLayout.SOUTH);
    pack();
  }

  void bindPrintChrome() {
    page.setName("img-page");
    printable.setName("img-print");
    state.setName(STATE);
    page.addActionListener(e -> applyPage());
    printable.addActionListener(e -> applyPrintable());
  }

  void applyPage() {
    options.setShowingAnnotations(false);
    state.setText(PAGE);
  }

  void applyPrintable() {
    options.setShowingAnnotations(true);
    state.setText(PRINTABLE);
  }

  public JButton pageButton() {
    return page;
  }

  public JButton printableButton() {
    return printable;
  }

  public JLabel stateLabel() {
    return state;
  }

  public String stateText() {
    return state.getText();
  }

  public PrintOptions getOptions() {
    return options;
  }

  public boolean isAccepted() {
    return accepted;
  }
}
