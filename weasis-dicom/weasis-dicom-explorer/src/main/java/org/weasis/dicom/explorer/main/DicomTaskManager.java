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
import java.util.concurrent.CopyOnWriteArrayList;
import org.weasis.dicom.explorer.exp.ExplorerTask;

/** Tracks in-flight {@link ExplorerTask} loads and the explorer {@link LoadingPanel}. */
public class DicomTaskManager {

  private static final DicomTaskManager INSTANCE = new DicomTaskManager();

  private final LoadingPanel loadingPanel = new LoadingPanel();
  private final List<ExplorerTask<?, ?>> tasks = new CopyOnWriteArrayList<>();

  public static DicomTaskManager getInstance() {
    return INSTANCE;
  }

  public LoadingPanel getLoadingPanel() {
    return loadingPanel;
  }

  public LoadingTaskPanel addTask(ExplorerTask<?, ?> task) {
    if (task == null) {
      return null;
    }
    tasks.add(task);
    return loadingPanel.addTask(task);
  }

  public List<ExplorerTask<?, ?>> getTasks() {
    return List.copyOf(tasks);
  }

  public void reset() {
    tasks.clear();
    loadingPanel.clearTasks();
  }

  public List<String> messages() {
    List<String> out = new ArrayList<>();
    for (ExplorerTask<?, ?> task : tasks) {
      out.add(task.getMessage());
    }
    return List.copyOf(out);
  }
}
