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
import javax.swing.JDialog;
import org.weasis.dicom.explorer.pref.node.DefaultDicomNode;

/** File &gt; Print DICOM dialog. Builds an N-ACTION film session; no SCP required for Have. */
public class DicomPrintDialog extends JDialog {

  private final DefaultDicomNode printer;
  private final DicomPrintOptionPane options = new DicomPrintOptionPane();

  public DicomPrintDialog(Frame owner, DefaultDicomNode printer) {
    super(owner, "DICOM Print", true);
    this.printer = printer;
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(options, BorderLayout.CENTER);
    setSize(420, 240);
  }

  public DicomPrintOptionPane getOptionPane() {
    return options;
  }

  public DicomPrint getPrint() {
    DicomPrint print = new DicomPrint(printer);
    print.setOptions(options.getOptions());
    return print;
  }

  public static DicomPrintDialog open(Frame owner, DefaultDicomNode printer) {
    return new DicomPrintDialog(owner, printer);
  }
}
