/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui.util;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public abstract class AbstractWizardDialog extends JDialog {
  private final List<AbstractItemDialogPage> pages = new ArrayList<>();
  private int index;

  protected AbstractWizardDialog(Window parent, String title) {
    super(parent, title, ModalityType.APPLICATION_MODAL);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JPanel buttons = new JPanel();
    JButton next = new JButton("Next");
    next.addActionListener(e -> nextPage());
    JButton finish = new JButton("Finish");
    finish.addActionListener(e -> finish());
    buttons.add(next);
    buttons.add(finish);
    add(buttons, BorderLayout.SOUTH);
  }

  public void addPage(AbstractItemDialogPage page) {
    if (page != null) {
      pages.add(page);
    }
  }

  public AbstractItemDialogPage getCurrentPage() {
    return pages.isEmpty() ? null : pages.get(Math.min(index, pages.size() - 1));
  }

  public void nextPage() {
    if (index < pages.size() - 1) {
      index++;
    }
  }

  public abstract void finish();
}
