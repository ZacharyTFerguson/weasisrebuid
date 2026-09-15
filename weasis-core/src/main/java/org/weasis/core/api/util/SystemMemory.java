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

public final class SystemMemory {
  private SystemMemory() {}

  public static long heapUsed() {
    return MemoryManager.getUsedMemory();
  }

  public static long heapMax() {
    return MemoryManager.getMaxMemory();
  }

  public static int usedPercent() {
    long max = heapMax();
    if (max <= 0) {
      return 0;
    }
    return (int) (heapUsed() * 100 / max);
  }
}
