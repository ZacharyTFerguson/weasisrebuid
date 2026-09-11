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

/** Heap vs remaining budget. */
public final class MemoryManager {

  private MemoryManager() {}

  public static long getMaxMemory() {
    return Runtime.getRuntime().maxMemory();
  }

  public static long getUsedMemory() {
    Runtime rt = Runtime.getRuntime();
    return rt.totalMemory() - rt.freeMemory();
  }

  public static long getAvailableMemory() {
    return getMaxMemory() - getUsedMemory();
  }

  public static void runGarbageCollection() {
    System.gc();
  }
}
