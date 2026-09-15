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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;
import org.dcm4che3.util.TagUtils;
import org.weasis.core.ui.util.WtoolBar;
import org.weasis.dicom.codec.DicomElement;
import org.weasis.dicom.codec.TagD;

/** 2D chrome that dumps the selected image dataset tags (keyword, tag, value). */
public class DcmHeaderToolBar extends WtoolBar {

  public static final String NAME = "DICOM Header";

  private View2d view;
  private String lastDump = "";

  public DcmHeaderToolBar() {
    super(NAME, 25);
    JButton dump =
        new JButton(
            new AbstractAction("Dump") {
              @Override
              public void actionPerformed(ActionEvent e) {
                dumpSelected();
              }
            });
    dump.setName("dumpHeader");
    add(dump);
  }

  public void bind(View2d view) {
    this.view = view;
  }

  public View2d boundView() {
    return view;
  }

  public String lastDump() {
    return lastDump;
  }

  public String dumpSelected() {
    if (view != null && view.getDataset() != null) {
      return dump(view.getDataset());
    }
    return dump((Attributes) null);
  }

  public String dump(DicomElement element) {
    return dump(element == null ? null : element.getDicomObject());
  }

  public String dump(Attributes dataset) {
    if (dataset == null) {
      lastDump = "";
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
}
