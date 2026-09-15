/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui.task;

public class TaskMonitor {
  private volatile int progress;
  private volatile int maximum = 100;
  private volatile boolean cancelled;
  private String note = "";

  public TaskMonitor() {}

  public TaskMonitor(int maximum) {
    this.maximum = Math.max(1, maximum);
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  public int getMaximum() {
    return maximum;
  }

  public void setMaximum(int maximum) {
    this.maximum = maximum;
  }

  public boolean isCancelled() {
    return cancelled;
  }

  public void cancel() {
    this.cancelled = true;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note == null ? "" : note;
  }
}
