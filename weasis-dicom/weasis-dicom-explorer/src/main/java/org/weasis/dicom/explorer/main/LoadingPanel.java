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

import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.weasis.dicom.explorer.exp.ExplorerTask;

/** Stack of {@link LoadingTaskPanel} rows shown at the bottom of the DICOM explorer. */
public class LoadingPanel extends JPanel {

  private final List<LoadingTaskPanel> rows = new ArrayList<>();

  public LoadingPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  }

  public LoadingTaskPanel addTask(ExplorerTask<?, ?> task) {
    LoadingTaskPanel row = new LoadingTaskPanel(task);
    rows.add(row);
    add(row);
    revalidate();
    return row;
  }

  public void removeTask(LoadingTaskPanel row) {
    if (row == null) {
      return;
    }
    rows.remove(row);
    remove(row);
    revalidate();
  }

  public List<LoadingTaskPanel> getRows() {
    return List.copyOf(rows);
  }

  public void clearTasks() {
    rows.clear();
    removeAll();
    revalidate();
  }
}
