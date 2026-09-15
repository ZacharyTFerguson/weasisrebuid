/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import org.weasis.core.ui.model.graphic.Graphic;

public class MeasureDialog extends JDialog {
  private final Graphic graphic;

  public MeasureDialog(Window parent, Graphic graphic) {
    super(parent, "Measure", ModalityType.APPLICATION_MODAL);
    this.graphic = graphic;
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    add(new JLabel(graphic == null ? "" : String.valueOf(graphic)), BorderLayout.CENTER);
    JButton close = new JButton("Close");
    close.addActionListener(e -> dispose());
    add(close, BorderLayout.SOUTH);
    pack();
  }

  public Graphic getGraphic() {
    return graphic;
  }
}
