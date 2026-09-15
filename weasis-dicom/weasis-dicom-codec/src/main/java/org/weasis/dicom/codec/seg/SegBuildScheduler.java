/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.seg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SegBuildScheduler {
  private final ExecutorService executor =
      Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));

  public void submit(Runnable task) {
    if (task != null) {
      executor.execute(task);
    }
  }

  public void shutdown() {
    executor.shutdownNow();
  }
}
