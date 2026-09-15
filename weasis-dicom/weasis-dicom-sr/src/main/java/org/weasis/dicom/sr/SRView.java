/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.sr;

import java.awt.BorderLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.dcm4che3.data.Attributes;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.dicom.codec.DicomElement;

/** Displays DICOM SR Content Sequence as plain text. */
public class SRView extends JPanel {

  private final JEditorPane editor = new JEditorPane("text/plain", "");
  private final SRReader reader = new SRReader();

  public SRView() {
    super(new BorderLayout());
    editor.setEditable(false);
    add(new JScrollPane(editor), BorderLayout.CENTER);
  }

  public void display(Attributes dataset) {
    editor.setText(reader.displayText(dataset));
    editor.setCaretPosition(0);
  }

  public void display(MediaElement media) {
    if (media instanceof DicomElement dicom) {
      display(dicom.getDicomObject());
    } else {
      display((Attributes) null);
    }
  }

  public String displayedText() {
    return editor.getText();
  }

  public SRReader reader() {
    return reader;
  }

  public JEditorPane editor() {
    return editor;
  }
}
