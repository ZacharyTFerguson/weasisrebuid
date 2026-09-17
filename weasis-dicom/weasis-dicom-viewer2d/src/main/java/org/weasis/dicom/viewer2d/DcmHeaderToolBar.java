/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;
import org.dcm4che3.util.TagUtils;
import org.weasis.core.ui.util.WtoolBar;
import org.weasis.dicom.codec.DicomElement;
import org.weasis.dicom.codec.TagD;

/** 2D chrome that dumps the selected image dataset tags (keyword, tag, value). */
public class DcmHeaderToolBar extends WtoolBar {

  public static final String NAME = "DICOM Header";
  public static final String DUMP = "dumpHeader";
  public static final String DUMP_TEXT = "headerDump";

  private final JButton dump = new JButton(new DumpAction());
  private final JTextArea dumpArea = new JTextArea(6, 36);
  private View2d view;
  private String lastDump = "";

  public DcmHeaderToolBar() {
    super(NAME, 25);
    dump.setName(DUMP);
    dump.setText("Dump");
    dumpArea.setName(DUMP_TEXT);
    dumpArea.setEditable(false);
    dumpArea.setLineWrap(true);
    dumpArea.setWrapStyleWord(true);
    JScrollPane scroll = new JScrollPane(dumpArea);
    scroll.setName(DUMP_TEXT + "Scroll");
    scroll.setPreferredSize(new Dimension(280, 96));
    add(dump);
    add(scroll);
  }

  public void bind(View2d view) {
    this.view = view;
  }

  public View2d boundView() {
    return view;
  }

  public JButton dumpButton() {
    return dump;
  }

  public JTextArea dumpArea() {
    return dumpArea;
  }

  public String lastDump() {
    return lastDump;
  }

  public String dumpSelected() {
    bindPainted();
    String text = dump(view == null ? null : view.getDataset());
    flushHost();
    return text;
  }

  void bindPainted() {
    if (view == null) {
      return;
    }
    Object host = view.getClientProperty(View2dContainer.class);
    if (host instanceof View2dContainer container) {
      View2d painted = container.paintedCell();
      if (painted != null) {
        this.view = painted;
      }
    }
  }

  void flushHost() {
    if (view == null) {
      return;
    }
    Object host = view.getClientProperty(View2dContainer.class);
    if (host instanceof View2dContainer container) {
      container.flushFlipPaint();
    }
  }

  public String dump(DicomElement element) {
    return dump(element == null ? null : element.getDicomObject());
  }

  public String dump(Attributes dataset) {
    if (dataset == null) {
      lastDump = "";
      dumpArea.setText("");
      return lastDump;
    }
    StringBuilder builder = new StringBuilder();
    for (int tag : dataset.tags()) {
      if (builder.length() > 0) {
        builder.append('\n');
      }
      builder.append(formatTag(dataset, tag));
    }
    lastDump = builder.toString();
    dumpArea.setText(lastDump);
    dumpArea.setCaretPosition(0);
    return lastDump;
  }

  static String formatTag(Attributes dataset, int tag) {
    String keyword = TagD.keywordOf(tag);
    String hex = TagUtils.toString(tag);
    VR vr = dataset.getVR(tag);
    if (vr == VR.SQ) {
      int n = dataset.getSequence(tag) == null ? 0 : dataset.getSequence(tag).size();
      return keyword + " " + hex + ": SQ[" + n + "]";
    }
    if (vr == VR.OB || vr == VR.OW || vr == VR.UN || vr == VR.OD || vr == VR.OF) {
      return keyword + " " + hex + ": [" + vr + "]";
    }
    try {
      String value = dataset.getString(tag, "");
      return keyword + " " + hex + ": " + (value == null ? "" : value);
    } catch (RuntimeException e) {
      return keyword + " " + hex + ": [" + vr + "]";
    }
  }

  final class DumpAction extends AbstractAction {
    DumpAction() {
      super("Dump");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      dumpSelected();
    }
  }
}
