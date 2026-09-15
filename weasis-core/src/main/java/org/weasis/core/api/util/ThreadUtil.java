/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadUtil {
  private ThreadUtil() {}

  public static ExecutorService newFixedThreadPool(int n, String name) {
    AtomicInteger i = new AtomicInteger();
    ThreadFactory f =
        r -> {
          Thread t = new Thread(r, (name == null ? "weasis" : name) + "-" + i.incrementAndGet());
          t.setDaemon(true);
          return t;
        };
    return Executors.newFixedThreadPool(Math.max(1, n), f);
  }
}
