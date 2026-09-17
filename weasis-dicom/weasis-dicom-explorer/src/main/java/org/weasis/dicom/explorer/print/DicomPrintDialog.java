/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.print;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.dcm4che3.data.Attributes;
import org.weasis.dicom.explorer.pref.node.DefaultDicomNode;

/** File &gt; Print DICOM dialog. Builds an N-ACTION film session; no SCP required for Have. */
public class DicomPrintDialog extends JDialog {

  public static final String STATE = "print-state";
  public static final String NONE = "none";
  public static final String FILM_SESSION = "film-session";
  public static final String PRINT = "print";
  static final String SESSION_UID = "2.25.session";

  private final DefaultDicomNode printer;
  private final DicomPrintOptionPane options = new DicomPrintOptionPane();
  private final JButton film = new JButton("Film Session");
  private final JButton action = new JButton("Print");
  private final JLabel state = new JLabel(NONE);
  private Attributes lastSession;
  private DicomPrint.NAction lastAction;

  public DicomPrintDialog(Frame owner, DefaultDicomNode printer) {
    super(owner, "DICOM Print", true);
    this.printer = printer;
    bindPrintChrome();
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(printChrome(), BorderLayout.NORTH);
    getContentPane().add(options, BorderLayout.CENTER);
    setSize(420, 240);
  }

  void bindPrintChrome() {
    film.setName("print-film");
    action.setName("print-action");
    state.setName(STATE);
    film.addActionListener(e -> applyFilm());
    action.addActionListener(e -> applyAction());
  }

  JPanel printChrome() {
    JPanel chrome = new JPanel();
    chrome.add(film);
    chrome.add(action);
    chrome.add(state);
    return chrome;
  }

  public DicomPrintOptionPane getOptionPane() {
    return options;
  }

  public DicomPrint getPrint() {
    DicomPrint print = new DicomPrint(printer);
    print.setOptions(options.getOptions());
    return print;
  }

  public JButton filmButton() {
    return film;
  }

  public JButton actionButton() {
    return action;
  }

  public JLabel stateLabel() {
    return state;
  }

  public String stateText() {
    return state.getText();
  }

  public Attributes lastSession() {
    return lastSession;
  }

  public DicomPrint.NAction lastAction() {
    return lastAction;
  }

  void applyFilm() {
    lastSession = getPrint().nCreateFilmSession();
    state.setText(FILM_SESSION);
  }

  void applyAction() {
    lastAction = getPrint().nActionPrintFilmSession(SESSION_UID);
    state.setText(PRINT);
  }

  public static DicomPrintDialog open(Frame owner, DefaultDicomNode printer) {
    return new DicomPrintDialog(owner, printer);
  }
}
