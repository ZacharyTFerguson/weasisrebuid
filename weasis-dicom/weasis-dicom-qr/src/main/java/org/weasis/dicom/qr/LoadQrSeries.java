/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import java.util.ArrayDeque;
import java.util.Deque;
import org.weasis.dicom.explorer.SeriesDownloadManager;

/**
 * MX-12: thumbnail raise-priority preempts one in-flight series. Does not add a fourth series slot.
 */
public final class LoadQrSeries {

  private final SeriesDownloadManager manager;
  private final Deque<String> running = new ArrayDeque<>();
  private String preempted;

  public LoadQrSeries(SeriesDownloadManager manager) {
    this.manager = manager;
  }

  public void start(String seriesUid) {
    if (running.size() >= manager.seriesCap()) {
      throw new IllegalStateException("exceeded series cap");
    }
    running.addLast(seriesUid);
  }

  public void preempt(String seriesUid) {
    if (running.size() >= manager.seriesCap() && !running.contains(seriesUid)) {
      preempted = running.removeFirst();
    }
    if (!running.contains(seriesUid)) {
      running.addLast(seriesUid);
    }
  }

  public int inFlight() {
    return running.size();
  }

  public String preempted() {
    return preempted;
  }
}
