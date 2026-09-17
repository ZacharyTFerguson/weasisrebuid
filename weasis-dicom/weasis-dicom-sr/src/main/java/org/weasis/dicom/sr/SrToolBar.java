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

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.weasis.core.ui.util.WtoolBar;

/** SR viewer chrome: HTML print of the displayed report plus IMAGE SOP references. */
public class SrToolBar extends WtoolBar {

  public static final String NAME = "SR";

  private SRView view;
  private String lastHtml = "";

  public SrToolBar() {
    super(NAME, 10);
    JButton print =
        new JButton(
            new AbstractAction("Print") {
              @Override
              public void actionPerformed(ActionEvent e) {
                printHtml();
              }
            });
    print.setName("printSr");
    add(print);
  }

  public void bind(SRView view) {
    this.view = view;
  }

  public SRView boundView() {
    return view;
  }

  public String lastHtml() {
    return lastHtml;
  }

  public List<SRImageReference> imageReferences() {
    if (view == null) {
      return List.of();
    }
    return SRImageReference.fromDocument(view.dataset());
  }

  public String printHtml() {
    if (view == null) {
      lastHtml = "";
      return lastHtml;
    }
    lastHtml = view.html();
    EditorPanePrinter printer = new EditorPanePrinter(lastHtml);
    printer.render();
    return lastHtml;
  }

  public BufferedImage printPreview() {
    return new EditorPanePrinter(printHtml()).render();
  }
}
