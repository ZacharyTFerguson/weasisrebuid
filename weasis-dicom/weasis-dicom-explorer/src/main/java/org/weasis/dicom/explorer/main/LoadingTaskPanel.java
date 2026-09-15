/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.weasis.dicom.explorer.exp.ExplorerTask;

/** One in-flight explorer load row (message + progress + optional cancel). */
public class LoadingTaskPanel extends JPanel {

  private final ExplorerTask<?, ?> task;
  private final JLabel message = new JLabel();
  private final JProgressBar progress = new JProgressBar();
  private final JButton cancel = new JButton("Cancel");

  public LoadingTaskPanel() {
    this(null);
  }

  public LoadingTaskPanel(ExplorerTask<?, ?> task) {
    super(new BorderLayout(4, 0));
    this.task = task;
    message.setText(task == null ? "" : task.getMessage());
    progress.setIndeterminate(true);
    add(message, BorderLayout.CENTER);
    add(progress, BorderLayout.EAST);
    if (task != null && task.isInterruptible()) {
      cancel.addActionListener(e -> task.cancel(true));
      add(cancel, BorderLayout.WEST);
    }
  }

  public ExplorerTask<?, ?> getTask() {
    return task;
  }

  public String getMessage() {
    return message.getText();
  }

  public JProgressBar getProgressBar() {
    return progress;
  }

  public JButton getCancelButton() {
    return cancel;
  }
}
